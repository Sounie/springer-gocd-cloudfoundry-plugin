package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;

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
        final String appNamePrefix = packageConfiguration.get("APP_NAME").getValue();

        LOGGER.debug("getLatestRevision called, app name " + appNamePrefix);

        CloudFoundryClient client = getClient(repositoryConfiguration);

        client.login();

        List<String> appNames = lookupAppNames(client, appNamePrefix);

        List<AppInstanceDetails> instances = new ArrayList<AppInstanceDetails>();
        for (String appName : appNames) {
            InstancesInfo applicationInstances = client.getApplicationInstances(appName);

            if (applicationInstances != null) {
                addRunningInstances(appNamePrefix, instances, appName, applicationInstances);
            }
        }

        client.logout();

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
            // TODO: what is suitable fallback?
            return new PackageRevision("None found", new Date(), "");
        }
    }

    private void addRunningInstances(String appNamePrefix, List<AppInstanceDetails> instances, String appName, InstancesInfo applicationInstances) {
        applicationInstances.getInstances().stream()
                .filter(instance -> instance.getState().equals(InstanceState.RUNNING))
                .forEach(instance -> instances.add(new AppInstanceDetails(
                        appName,
                        instance.getSince(),
                        RevisionNumberParser.parse(appNamePrefix, appName)
                )));
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
            return null;
        }
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        LOGGER.debug("checkConnectionToRepository called");

        CloudFoundryClient client = getClient(repositoryConfiguration);

        OAuth2AccessToken login = client.login();
        client.logout();
        if (login == null || login.getExpiration().before(new Date())) {
            return ExecutionResult.failure("Invalid login");
        }

        return ExecutionResult.success("Connected with supplied credentials.");
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        CloudFoundryClient client = getClient(repositoryConfiguration);

        Result result = new Result();

        OAuth2AccessToken login = client.login();

        if (login == null || login.getExpiration().before(new Date())) {
            LOGGER.warn("Invalid login");
            result = ExecutionResult.failure("Invalid login");
        } else {
            final String appNamePrefix = packageConfiguration.get("APP_NAME").getValue();
            if (lookupAppNames(client, appNamePrefix).isEmpty()) {
                LOGGER.warn("No app found");
                result = ExecutionResult.failure("No such app found in CloudFoundry.");
            }
        }

        client.logout();

        return result;
    }

    private CloudFoundryClient getClient(RepositoryConfiguration repositoryConfiguration) {
        String api = repositoryConfiguration.get("REPO_URL").getValue();
        String username = repositoryConfiguration.get("USERNAME").getValue();
        String password = repositoryConfiguration.get("PASSWORD").getValue();

        LOGGER.debug("Cloud Foundry connection details: api: " + api + ", username " + username);

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
