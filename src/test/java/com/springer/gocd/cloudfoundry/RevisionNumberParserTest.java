package com.springer.gocd.cloudfoundry;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RevisionNumberParserTest {
    @Test
    public void shouldParseDigitsFromAppName() {
        RevisionNumberParser parser = new RevisionNumberParser();
        String appRevision = parser.parse("my-app", "my-app-123");

        assertThat(appRevision, is("123"));
    }

    @Test
    public void shouldAllowDecimalPointInAppName() {
        RevisionNumberParser parser = new RevisionNumberParser();
        String appRevision = parser.parse("pi-app", "pi-app-3.14");

        assertThat(appRevision, is("3.14"));
    }

    @Test
    public void shouldFindRevisionMidString() {
        RevisionNumberParser parser = new RevisionNumberParser();
        String appRevision = parser.parse("pi-app", "pi-app-1.25-stuff");

        assertThat(appRevision, is("1.25"));
    }
}