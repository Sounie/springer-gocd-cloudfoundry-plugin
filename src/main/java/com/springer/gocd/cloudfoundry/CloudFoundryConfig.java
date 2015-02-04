package com.springer.gocd.cloudfoundry;

import java.util.Map;

public class CloudFoundryConfig {
    private final String apiUrl;
    private final String username;
    private final String password;
    private final String space;


    public CloudFoundryConfig(Map<String, String> config) {
        this.apiUrl = config.get("apiUrl");
        this.username = config.get("username");
        this.password = config.get("password");
        this.space = config.get("space");
    }


}
