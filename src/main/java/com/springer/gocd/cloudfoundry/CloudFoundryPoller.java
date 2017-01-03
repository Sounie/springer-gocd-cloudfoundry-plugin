package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.*;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import reactor.core.publisher.Mono;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class CloudFoundryPoller implements PackageMaterialPoller {
    private static final Logger LOGGER = Logger.getLoggerFor(CloudFoundryPoller.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        final String appNamePrefix = packageConfiguration.get("APP_NAME").getValue();

        LOGGER.debug("getLatestRevision called, app name " + appNamePrefix);

        CloudFoundryClient client = getClient(repositoryConfiguration);

        return getLatestV2PackageRevision(client, appNamePrefix);
    }


    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration,
                                                   RepositoryConfiguration repositoryConfiguration,
                                                   PackageRevision previouslyKnownRevision) {
        LOGGER.debug("latestModificationSince called with previous revision " + previouslyKnownRevision);

        PackageRevision latestRevision = getLatestRevision(packageConfiguration, repositoryConfiguration);

        LOGGER.debug("latestRevision: " + latestRevision);

        if (latestRevision.getTimestamp().after(previouslyKnownRevision.getTimestamp())) {
            return latestRevision;
        } else {
            return previouslyKnownRevision;
        }
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        LOGGER.debug("checkConnectionToRepository called");

        CloudFoundryClient client = getClient(repositoryConfiguration);

        GetInfoResponse response = client.info().get(GetInfoRequest.builder().build()).block(Duration.ofSeconds(5));

        String apiVersion = response.getApiVersion();

        return ExecutionResult.success("Connected with supplied credentials, apiVersion: " + apiVersion);
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        CloudFoundryClient client = getClient(repositoryConfiguration);

        Result result = new Result();

        final String appNamePrefix = packageConfiguration.get("APP_NAME").getValue();
            if (lookupAppNames(client, appNamePrefix).isEmpty()) {
                LOGGER.warn("No app found");
                result = ExecutionResult.failure("No such app found in CloudFoundry.");
            }

        return result;
    }

    private CloudFoundryClient getClient(RepositoryConfiguration repositoryConfiguration) {
        String api = repositoryConfiguration.get("REPO_URL").getValue();
        String username = repositoryConfiguration.get("USERNAME").getValue();
        String password = repositoryConfiguration.get("PASSWORD").getValue();

        LOGGER.debug("Cloud Foundry connection details: api: " + api + ", username " + username);

        ConnectionContext connectionContext = DefaultConnectionContext.builder()
                .apiHost(api)
                .skipSslValidation(true)
                .build();

        ReactorCloudFoundryClient client = ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(PasswordGrantTokenProvider.builder()
                        .username(username)
                        .password(password)
                        .build())
                .build();

        return client;
    }

    private List<String> lookupAppNames(CloudFoundryClient client, String appNamePrefix) {
        List<String> matchingApps = new ArrayList<String>();

        accumulateV2Apps(client, matchingApps);

        return matchingApps.stream()
                .filter(i -> i.startsWith(appNamePrefix))
                .collect(Collectors.toList());
    }

    private PackageRevision getLatestV2PackageRevision(CloudFoundryClient client, String appNamePrefix) {
        PackageRevision latestRevision = null;

        ApplicationsV2 applicationsV2 = client.applicationsV2();

        ListApplicationsRequest listApplicationsRequest = ListApplicationsRequest.builder()
                .build();

        Mono<ListApplicationsResponse> responseMono = applicationsV2.list(listApplicationsRequest);

        try {
            ListApplicationsResponse response = responseMono.block();

            if (response.getTotalResults() > 0) {
                ApplicationResource resource = response.getResources()
                        .stream()
                        .filter(i -> i.getEntity().getName().startsWith(appNamePrefix)
                                && "STARTED".equals(i.getEntity().getState()))
                        .sorted(Comparator.comparing(a -> a.getMetadata().getUpdatedAt()))
                        .findFirst().get();

                try {
                    latestRevision = new PackageRevision(
                            RevisionNumberParser.parse(appNamePrefix, resource.getEntity().getName()),
                            new SimpleDateFormat(DATE_FORMAT).parse(resource.getMetadata().getUpdatedAt()),
                            "");
                } catch (ParseException e) {
                    LOGGER.warn("Failed to parse date: " + resource.getMetadata().getUpdatedAt());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to obtain revision.");
        }

        return latestRevision;
    }

    private void accumulateV2Apps(CloudFoundryClient client, List<String> matchingApps) {
        ApplicationsV2 applicationsV2 = client.applicationsV2();

        ListApplicationsRequest listApplicationsRequest = ListApplicationsRequest.builder()
                .build();

        Mono<ListApplicationsResponse> responseMono = applicationsV2.list(listApplicationsRequest);

        try {
            ListApplicationsResponse response = responseMono.block();

            if (response.getTotalResults() > 0) {
                System.out.println(response.getTotalResults());
                response.getResources().forEach(app ->
                        matchingApps.add(app.getEntity().getName()));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to lookup V2 apps.");
        }
    }
}
