package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageMaterialProperty;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;

import static com.thoughtworks.go.plugin.api.config.Property.*;

public class CloudFoundryConfig implements PackageMaterialConfiguration {
    private static final Logger LOGGER = Logger.getLoggerFor(CloudFoundryConfig.class);

    // TODO: Determine whether this setup is optimal

    public static final Property apiUrl = new PackageMaterialProperty("apiUrl")
            .with(DISPLAY_NAME, "CloudFoundry API URL")
            .with(DISPLAY_ORDER, 0);


    public static final Property username = new PackageMaterialProperty("username")
            .with(DISPLAY_NAME, "Username")
            .with(DISPLAY_ORDER, 1);

    public static final Property password = new PackageMaterialProperty("password")
            .with(DISPLAY_NAME, "Password")
            .with(DISPLAY_ORDER, 2);
//    private final String space;
//    private final String appName;
//    private final String orgName;


    @Override
    public RepositoryConfiguration getRepositoryConfiguration() {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(apiUrl);
        repoConfig.add(username);
        repoConfig.add(password);

        return repoConfig;
    }

    @Override
    public PackageConfiguration getPackageConfiguration() {
        PackageConfiguration packageConfig = new PackageConfiguration();

//         TODO: implement
         return packageConfig;
    }

    @Override
    public ValidationResult isRepositoryConfigurationValid(RepositoryConfiguration repositoryConfiguration) {
        // TODO: implement
        return new ValidationResult();
    }

    @Override
    public ValidationResult isPackageConfigurationValid(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        // TODO: implement
        return new ValidationResult();
    }
}
