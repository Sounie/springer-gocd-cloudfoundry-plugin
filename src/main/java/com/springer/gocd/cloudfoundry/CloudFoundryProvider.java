package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProvider;

@Extension
public class CloudFoundryProvider implements PackageMaterialProvider {

    @Override
    public PackageMaterialConfiguration getConfig() {
        return new CloudFoundryConfig();
    }

    @Override
    public PackageMaterialPoller getPoller() {
        return new CloudFoundryPoller();
    }
}
