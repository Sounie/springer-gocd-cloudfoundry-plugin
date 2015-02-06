package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class CloudFoundryPoller implements PackageMaterialPoller {

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        getClient(
                packageConfiguration.get("apiUrl").getValue(),
                packageConfiguration.get("username").getValue(),
                packageConfiguration.get("password").getValue());
        return new PackageRevision("fake revision", new Date(), "fake user");
    }

    @Override
    public PackageRevision latestModificationSince(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration, PackageRevision previouslyKnownRevision) {
        return new PackageRevision("fake revision", new Date(), "fake user");
    }

    @Override
    public Result checkConnectionToRepository(RepositoryConfiguration repositoryConfiguration) {
        return new Result();
    }

    @Override
    public Result checkConnectionToPackage(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
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
