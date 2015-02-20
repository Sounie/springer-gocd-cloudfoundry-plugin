# springer-gocd-cloudfoundry-plugin
Go plugin for monitoring app status in Cloud Foundry.

Status: Version 1.0.0 released.

## Requirements
JDK 6 or higher.  Future versions may jump to 8+, but for now we'll stay aligned with Go CD.

## Building
./gradlew clean build

## Installing
After building, copy the generated jar file from build/libs/springer-gocd-cloudfoundry-plugin-1.0.jar to your Go server's external plugins directory.

Restart Go server to allow it to detect and install the new plugin.
