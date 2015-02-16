package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstanceState;
import org.cloudfoundry.client.lib.domain.InstancesInfo;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class CloudFoundryPoller implements PackageMaterialPoller {
    private static final Logger LOGGER = Logger.getLoggerFor(CloudFoundryPoller.class);

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        CloudFoundryClient client = getClient(repositoryConfiguration);

        client.login();

        final String appNamePrefix = packageConfiguration.get("appName").getValue();

        LOGGER.info("getLatestRevision called");

        List<String> appNames = lookupAppNames(client, appNamePrefix);

        List<AppInstanceDetails> instances = new ArrayList<AppInstanceDetails>();
        for (String appName : appNames) {
            InstancesInfo applicationInstances = client.getApplicationInstances(appName);

            if (applicationInstances != null) {
                for (InstanceInfo instance : applicationInstances.getInstances()) {
                    if (instance.getState().equals(InstanceState.RUNNING)) {
                        LOGGER.info("instance: " + instance);
                        // Assuming app name has version suffix
                        instances.add(
                                new AppInstanceDetails(
                                    appName,
                                    instance.getSince(),
                                    appName.replace(appNamePrefix, "").replaceAll("\\D", "")
                                )
                        );
                    }
                }
            }
        }

        Collections.sort(instances, new Comparator<AppInstanceDetails>() {
            @Override
            public int compare(AppInstanceDetails i1, AppInstanceDetails i2) {
                return i1.getSince().compareTo(i2.getSince());
            }
        });

        if (instances.size() > 0) {
            AppInstanceDetails instanceInfo = instances.iterator().next();

            // FIXME: handling when no dates
            return new PackageRevision(instanceInfo.getRevision(), instanceInfo.getSince(), "");
        } else {
            // TODO: what is a suitable fallback?
            return new PackageRevision("None found", new Date(), "");
        }
    }

    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration,
                                                   RepositoryConfiguration repositoryConfiguration,
                                                   PackageRevision previouslyKnownRevision) {
        LOGGER.info("latestModificationSince called");
        return new PackageRevision("fake revision", new Date(), "fake user");
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        LOGGER.info("checkConnectionToRepository called");

        CloudFoundryClient client = getClient(repositoryConfiguration);

        Result result = new Result();

        OAuth2AccessToken login = client.login();
        client.logout();
        if (login == null || login.getExpiration().before(new Date())) {
            return result.withErrorMessages("Invalid login");
        }

        return result;
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        CloudFoundryClient client = getClient(repositoryConfiguration);

        Result result = new Result();

        OAuth2AccessToken login = client.login();

        if (login == null || login.getExpiration().before(new Date())) {
            LOGGER.warn("Invalid login");
            result = result.withErrorMessages("Invalid login");
        } else {
            final String appNamePrefix = packageConfiguration.get("appName").getValue();
            if (lookupAppNames(client, appNamePrefix).isEmpty()) {
                LOGGER.warn("No app found");
                result = result.withErrorMessages("No such app found in CloudFoundry.");
            }
        }

        client.logout();

        return result;
    }

    private CloudFoundryClient getClient(RepositoryConfiguration repositoryConfiguration) {
        String api = repositoryConfiguration.get("REPO_URL").getValue();
        String username = repositoryConfiguration.get("USERNAME").getValue();
        String password = repositoryConfiguration.get("PASSWORD").getValue();

        try {
            return new CloudFoundryClient(new CloudCredentials(username, password), new URL(api));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid api URL", e);
        }
    }

    private List<String> lookupAppNames(CloudFoundryClient client, String appName) {
        List<CloudApplication> applications = client.getApplications();

        // Assuming apps are deployed with the app name having a version appended
        List<String> matchingApps = new ArrayList<String>();
        for (CloudApplication application : applications) {
            if (application.getName().startsWith(appName)) {
                matchingApps.add(application.getName());
            }
        }

        return matchingApps;
    }
}
