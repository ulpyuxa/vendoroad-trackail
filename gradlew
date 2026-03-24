#!/bin/sh

# GitHub Actions will use 'gradle wrapper' or similar to download a real one.
# This is a stub to satisfy the 'chmod +x' and allow the build to bootstrap.

gradle wrapper --gradle-version 8.9
./gradlew "$@"
