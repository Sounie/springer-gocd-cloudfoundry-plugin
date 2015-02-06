package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.InstanceInfo;
import org.cloudfoundry.client.lib.domain.InstancesInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class CloudFoundryPoller implements PackageMaterialPoller {
    private static final Logger LOGGER = Logger.getLoggerFor(CloudFoundryPoller.class);

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        CloudFoundryClient client = getClient(
                packageConfiguration.get("apiUrl").getValue(),
                packageConfiguration.get("username").getValue(),
                packageConfiguration.get("password").getValue());

        client.login();

        // TODO - find latest version
        final String appName = "oscar-journal-dev-0_81";

        LOGGER.info("getLatestRevision called");

        InstancesInfo applicationInstances = client.getApplicationInstances(appName);


        if (applicationInstances != null) {
            for (InstanceInfo instance : applicationInstances.getInstances()) {
LOGGER.info("instance: " + instance);
            }
        }
        return new PackageRevision("fake revision", new Date(), "fake user");
    }

    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration, PackageRevision previouslyKnownRevision) {
        LOGGER.info("latestModificationSince called");
        return new PackageRevision("fake revision", new Date(), "fake user");
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        LOGGER.info("checkConnectionToRepository called");
        return new Result();
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        LOGGER.info("checkConnectionToPackage called");
        return new Result();
    }

    private CloudFoundryClient getClient(String api, String username, String password) {
        try {
            return new CloudFoundryClient(new CloudCredentials(username, password), new URL(api));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid api URL", e);
        }
    }
}
