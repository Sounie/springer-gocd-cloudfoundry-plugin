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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CloudFoundryPoller implements PackageMaterialPoller {
    private static final Logger LOGGER = Logger.getLoggerFor(CloudFoundryPoller.class);

    @Override
    public PackageRevision getLatestRevision(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        CloudFoundryClient client = getClient(repositoryConfiguration);

        client.login();

        // TODO - find latest version
        client.getApplication(packageConfiguration.get("appName").getValue());
        final String appName = "oscar-journal-dev-0_81";

        LOGGER.info("getLatestRevision called");

        InstancesInfo applicationInstances = client.getApplicationInstances(appName);

        List<Date> dates = new ArrayList<Date>();
        if (applicationInstances != null) {
            for (InstanceInfo instance : applicationInstances.getInstances()) {
LOGGER.info("instance: " + instance);
                dates.add(instance.getSince());
            }
        }
        dates.sort(new Comparator<Date>() {
            @Override
            public int compare(Date date1, Date date2) {
                return date1.compareTo(date2);
            }
        });

        // FIXME: handling when no dates
        return new PackageRevision("fake revision", dates.iterator().next(), "fake user");
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
}
