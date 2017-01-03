package com.springer.gocd.cloudfoundry;

import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.junit.*;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Optional tests for running CloudFoundry client calls.
 * <p>
 *     Based on interactions with pcfdev.
 * </p>
 * <p>
 *     Initially based on interactions with V2 of CF API.
 * </p>
 */
@Ignore("Only enable these tests if you have a PCFDev running with a cd-helloworld app deployed")
public class LocalCfTest {

    private ReactorCloudFoundryClient client;
    private int majorApiVersion;

    @Before
    public void setup() {
        ConnectionContext connectionContext = DefaultConnectionContext.builder()
                .apiHost("api.local.pcfdev.io")
                .skipSslValidation(true)
                .build();

        String username = "user";
        String password = "pass";

        client = ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(PasswordGrantTokenProvider.builder()
                        .password(password)
                        .username(username)
                        .build())
                .build();

        Mono<GetInfoResponse> getInfoResponseMono = client.info().get(GetInfoRequest.builder().build());
        GetInfoResponse response = getInfoResponseMono.block(Duration.ofSeconds(5L));
        String apiVersion = response.getApiVersion();
        assertThat(apiVersion, is(notNullValue()));
        majorApiVersion = Integer.parseInt(apiVersion.split("\\.")[0]);
    }

    @Test
    public void shouldLookupApp() throws Exception {
        List<String> appNames = lookupAppNames(client, "cf-helloworld");

        appNames.forEach(System.out::println);

        assertThat(appNames, hasSize(greaterThan(0)));
        assertThat(appNames, hasItem("cf-helloworld"));
    }

    @Test
    public void shouldParseApiMajorVersion() {
        Mono<GetInfoResponse> getInfoResponseMono = client.info().get(GetInfoRequest.builder().build());

        GetInfoResponse response = getInfoResponseMono.block(Duration.ofSeconds(5L));

        String apiVersion = response.getApiVersion();
        assertThat(apiVersion, is(notNullValue()));
        int majorVersion = Integer.parseInt(apiVersion.split("\\.")[0]);
        assertThat(majorVersion, greaterThan(1));
    }

    @Test
    public void shouldParseAppDates() {
        if (majorApiVersion == 2) {
            Mono<ListApplicationsResponse> responseMono = client.applicationsV2().list(ListApplicationsRequest.builder().build());

            ListApplicationsResponse listApplicationsResponse = responseMono.block(Duration.ofSeconds(5L));

            Integer totalResults = listApplicationsResponse.getTotalResults();
            assertThat(totalResults, greaterThan(0));

            List<ApplicationResource> resources = listApplicationsResponse.getResources();
            resources.forEach(
                    r -> {
                        Date created = null;
                        Date updated = null;
                        try {
                            created = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(r.getMetadata().getCreatedAt());
                            updated = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(r.getMetadata().getUpdatedAt());

                            System.out.println("DEBUG - state " + r.getEntity().getState());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        assertThat(updated.getTime() > created.getTime(), is(true));
                    }
            );
        }
    }

    private List<String> lookupAppNames(ReactorCloudFoundryClient client, String appName) {
        // TODO: consider flexibility to support v2 / v3

        List<String> matchingApps = new ArrayList<>();

        accumulateV2Apps(client, appName, matchingApps);
        accumulateV3Apps(client, appName, matchingApps);

        return matchingApps;
    }

    private void accumulateV2Apps(ReactorCloudFoundryClient client, String appName, List<String> matchingApps) {
        ApplicationsV2 applicationsV2 = client.applicationsV2();

        ListApplicationsRequest listApplicationsRequest = ListApplicationsRequest.builder()
                .build();

        Mono<ListApplicationsResponse> responseMono = applicationsV2.list(listApplicationsRequest);

        ListApplicationsResponse response = responseMono.block();

        if (response.getTotalResults() > 0) {
            System.out.println(response.getTotalResults());
            response.getResources().forEach(app ->
            {
                if (app.getEntity().getName().equals(appName)) {
                    matchingApps.add(appName);
                }
            });
        }
    }

    private void accumulateV3Apps(ReactorCloudFoundryClient client, String appName, List<String> matchingApps) {
        ApplicationsV3 applicationsV3 = client.applicationsV3();

        org.cloudfoundry.client.v3.applications.ListApplicationsRequest listApplicationsRequest =
                org.cloudfoundry.client.v3.applications.ListApplicationsRequest.builder()
                        .build();
        Mono<org.cloudfoundry.client.v3.applications.ListApplicationsResponse> responseMono = applicationsV3.list(listApplicationsRequest);

        org.cloudfoundry.client.v3.applications.ListApplicationsResponse response = responseMono.block();

        response.getResources().forEach(app ->
        {
            if (app.getName().equals(appName)) {
                matchingApps.add(app.getName());
            }
        });
    }
}
