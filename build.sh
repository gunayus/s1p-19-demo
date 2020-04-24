#!/usr/bin/env bash

set -e
echo "Build the system"
mvn clean package -U -Dmaven.test.skip=true





