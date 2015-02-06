package com.springer.gocd.cloudfoundry;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.AbstractGoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialPoller;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProvider;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Extension
public class CloudFoundryPlugin implements PackageMaterialProvider {
    private final GoPluginIdentifier identifier;

    public CloudFoundryPlugin() {
        identifier = new GoPluginIdentifier("Springer CF Plugin", Collections.singletonList("1"));
    }


//    @Override
//    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        Map executionRequest = (Map) gsonBuilder.create().fromJson(request.requestBody(), Object.class);
//
//        if ("execute".equals(request.requestName())) {
//            CloudFoundryPoller requestExecutor = new CloudFoundryPoller();
//
//            System.out.println("DEBUG - execute");
//        }
//
//
//
//        throw new UnhandledRequestTypeException(request.requestName());
//    }

    @Override
    public PackageMaterialConfiguration getConfig() {
        return new CloudFoundryConfig();
    }

    @Override
    public PackageMaterialPoller getPoller() {
        return new CloudFoundryPoller();
    }
}
