package com.springer.gocd.cloudfoundry;

import java.util.Date;

public class AppInstanceDetails {
    private final String appName;
    private final Date since;
    private final String revision;


    public AppInstanceDetails(String appName, Date since, String revision) {
        this.appName = appName;
        this.since = new Date(since.getTime());
        this.revision = revision;
    }

    public Date getSince() {
        return new Date(since.getTime());
    }

    public String getRevision() {
        return revision;
    }

    public String getAppName() {
        return appName;
    }
}
