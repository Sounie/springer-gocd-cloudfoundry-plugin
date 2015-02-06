package com.springer.gocd.cloudfoundry;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class CloudFoundryRequestExecutor {
    private final CloudFoundryClient client;

    public CloudFoundryRequestExecutor(CloudFoundryConfig config) {
        try {
             client = new CloudFoundryClient(
                    new CloudCredentials(config.getUsername(), config.getPassword()),
                    new URL(config.getApiUrl()),
                     new CloudSpace(null, config.getSpace(), new CloudOrganization(null, config.getOrgName())));
            config.getAppName();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid api url");
        }
    }

    public Collection<String> getVersions() {

        return null;
    };
}
