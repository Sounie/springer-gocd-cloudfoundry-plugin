package com.springer.gocd.cloudfoundry;

import java.util.Map;

public class CloudFoundryConfig {
    private final String apiUrl;
    private final String username;
    private final String password;
    private final String space;
    private final String appName;
    private final String orgName;


    public CloudFoundryConfig(Map<String, String> config) {
        this.apiUrl = config.get("apiUrl");
        this.username = config.get("username");
        this.password = config.get("password");
        this.space = config.get("space");
        this.appName = config.get("appName");
        this.orgName = config.get("orgName");
    }


    public String getApiUrl() {
        return apiUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSpace() {
        return space;
    }

    public String getAppName() {
        return appName;
    }

    public String getOrgName() {
        return orgName;
    }
}
