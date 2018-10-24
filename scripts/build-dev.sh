#!/bin/bash -e
: ${WORKSPACE=.}

set -x

LATEST_RC_BRANCH=$(git branch --sort=-refname -r | grep 'origin/rc' | head -n 1)
LATEST_RC_MAJOR_MINOR_VERSION=$(echo "$LATEST_RC_BRANCH" | cut -d'-' -f 2)
LATEST_RC_MAJOR=$(echo $LATEST_RC_MAJOR_MINOR_VERSION | cut -d'.' -f 1)
LATEST_RC_MINOR=$(echo $LATEST_RC_MAJOR_MINOR_VERSION | cut -d'.' -f 2)
INCREASED_MINOR=$((LATEST_RC_MINOR+1))
ACTUAL_VERSION="$LATEST_RC_MAJOR.$INCREASED_MINOR"


VERSION=2.10.0-dev.66

./gradlew -Penv=jenkins -b build.gradle clean build uploadArchives -Pversion=$VERSION --info --stacktrace --parallel -x test -x checkstyleMain -x checkstyleTest -x spotbugsMain -x spotbugsTest

echo "Computed next dev version: $VERSION"
echo VERSION=$VERSION > $WORKSPACE/version