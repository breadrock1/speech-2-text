#!/bin/sh

set -e

export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)

./gradlew updateRevision
./gradlew generateDocumentation

gcloud builds submit --tag gcr.io/nodal-thunder-279319/transcriber