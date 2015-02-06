package com.springer.gocd.cloudfoundry;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.AbstractGoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Extension
public class CloudFoundryPlugin extends AbstractGoPlugin {
    private final GoPluginIdentifier identifier;

    public CloudFoundryPlugin() {
        identifier = new GoPluginIdentifier("Springer CF Plugin", Collections.singletonList("1"));
    }


    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Map executionRequest = (Map) gsonBuilder.create().fromJson(request.requestBody(), Object.class);

        if ("execute".equals(request.requestName())) {
            CloudFoundryRequestExecutor requestExecutor = new CloudFoundryRequestExecutor(
                    new CloudFoundryConfig(executionRequest)
            );

            Collection<String> versions =  requestExecutor.getVersions();

            System.out.println("DEBUG - versions = " + versions);
        }



        throw new UnhandledRequestTypeException(request.requestName());
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return null;
    }
}
