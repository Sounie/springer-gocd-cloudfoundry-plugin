package com.springer.gocd.cloudfoundry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RevisionNumberParser {
    private static final Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|[0-9]+");


    public static String parse(String appNamePrefix, String fullAppName) {
        String revisionPart = fullAppName.replace(appNamePrefix, "");

        StringBuffer stringBuffer = new StringBuffer();
        Matcher m = pattern.matcher(revisionPart);
        while (m.find()) {
            stringBuffer.append(m.group());
        }

        return stringBuffer.toString();
    }
}
