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
    public static final String USERNAME_FIELD_NAME = "USERNAME";

    public static final String REPO_URL_FIELD_NAME = "REPO_URL";

    public static final String PASSWORD_FIELD_NAME = "PASSWORD";

    public static final String SPACE_FIELD_NAME = "SPACE";

    public static final String APP_NAME_FIELD_NAME = "APP_NAME";

    public static final String ORG_NAME_FIELD_NAME = "ORG_NAME";


    private static final Logger LOGGER = Logger.getLoggerFor(CloudFoundryConfig.class);


    public static final Property apiUrl = new PackageMaterialProperty(REPO_URL_FIELD_NAME)
            .with(DISPLAY_NAME, "CloudFoundry API URL")
            .with(Property.REQUIRED, Boolean.TRUE)
            .with(DISPLAY_ORDER, 0);

    public static final Property username = new PackageMaterialProperty(USERNAME_FIELD_NAME)
            .with(DISPLAY_NAME, "Username")
            .with(Property.REQUIRED, Boolean.TRUE)
            .with(DISPLAY_ORDER, 1);


    public static final Property password = new PackageMaterialProperty(PASSWORD_FIELD_NAME)
            .with(DISPLAY_NAME, "Password")
            .with(DISPLAY_ORDER, 2)
            .with(SECURE, Boolean.TRUE);


    public static final Property space = new PackageMaterialProperty(SPACE_FIELD_NAME)
            .with(DISPLAY_NAME, "Space");

    public static final Property appName = new PackageMaterialProperty(APP_NAME_FIELD_NAME)
        .with(DISPLAY_NAME, "App Name");

    public static final Property orgName = new PackageMaterialProperty(ORG_NAME_FIELD_NAME)
        .with(DISPLAY_NAME, "Organisation Name");


    @Override
    public RepositoryConfiguration getRepositoryConfiguration() {
        LOGGER.debug("getRepositoryConfiguration called.");

        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(apiUrl);
        repoConfig.add(username);
        repoConfig.add(password);

        return repoConfig;
    }

    @Override
    public PackageConfiguration getPackageConfiguration() {
        LOGGER.debug("getPackageConfiguration called.");
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
        LOGGER.debug("isPackageConfigurationValid called.");
        // Not certain what to validate here.
        return new ValidationResult();
    }
}
