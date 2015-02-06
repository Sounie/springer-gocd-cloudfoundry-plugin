package com.springer.gocd.cloudfoundry;

import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class CloudFoundryConfigTest {
    private CloudFoundryConfig config;

    @Before
    public void setUp() {
        config = new CloudFoundryConfig();
    }

    @Test
    public void shouldGetRepositoryConfiguration() {
        RepositoryConfiguration repositoryConfiguration = config.getRepositoryConfiguration();

        assertThat(repositoryConfiguration, is(notNullValue()));
        assertThat(repositoryConfiguration.get("USERNAME"), is(notNullValue()));
    }
}