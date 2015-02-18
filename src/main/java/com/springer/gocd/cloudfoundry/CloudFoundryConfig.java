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

    public static final Property apiUrl = new PackageMaterialProperty("REPO_URL")
            .with(DISPLAY_NAME, "CloudFoundry API URL")
            .with(Property.REQUIRED, Boolean.TRUE)
            .with(DISPLAY_ORDER, 0);

    public static final Property username = new PackageMaterialProperty("USERNAME")
            .with(DISPLAY_NAME, "Username")
            .with(Property.REQUIRED, Boolean.TRUE)
            .with(DISPLAY_ORDER, 1);

    public static final Property password = new PackageMaterialProperty("PASSWORD")
            .with(DISPLAY_NAME, "Password")
            .with(DISPLAY_ORDER, 2);

    public static final Property space = new PackageMaterialProperty("SPACE")
            .with(DISPLAY_NAME, "Space");

    public static final Property appName = new PackageMaterialProperty("APP_NAME")
        .with(DISPLAY_NAME, "App Name");

    public static final Property orgName = new PackageMaterialProperty("ORG_NAME")
        .with(DISPLAY_NAME, "Organisation Name");


    @Override
    public RepositoryConfiguration getRepositoryConfiguration() {
        LOGGER.info("getRepositoryConfiguration called.");

        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(apiUrl);
        repoConfig.add(username);
        repoConfig.add(password);

        return repoConfig;
    }

    @Override
    public PackageConfiguration getPackageConfiguration() {
        LOGGER.info("getPackageConfiguration called.");
        PackageConfiguration packageConfig = new PackageConfiguration();

        packageConfig.add(space);
        packageConfig.add(appName);
        packageConfig.add(orgName);
        return packageConfig;
    }

    @Override
    public ValidationResult isRepositoryConfigurationValid(RepositoryConfiguration repositoryConfiguration) {
        LOGGER.debug("isRepositoryConfigurationValid called.");

        // Not certain what to validate here.
        ValidationResult result = new ValidationResult();

        return result;
    }

    @Override
    public ValidationResult isPackageConfigurationValid(PackageConfiguration packageConfiguration, RepositoryConfiguration repositoryConfiguration) {
        LOGGER.info("isPackageConfigurationValid called.");
        // Not certain what to validate here.
        return new ValidationResult();
    }
}
