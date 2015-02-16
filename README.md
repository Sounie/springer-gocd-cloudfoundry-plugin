# springer-gocd-cloudfoundry-plugin
Go plugin for Cloud Foundry

Status: Partially-functional work in progress.

## Building
./gradlew clean build

## Installing
After building, copy the generated jar file from build/libs/springer-gocd-cloudfoundry-plugin-1.0.jar to your Go server's external plugins directory.

Restart Go server to allow it to detect and install the new plugin.
