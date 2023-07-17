#!/bin/bash
set -eEuo pipefail
cd "$(dirname "$(readlink -f "$0")")/.."

source "scripts/bash-buddy/lib/java_utils.sh"

jvu::switch_to_jdk 19

########################################
# integration test
########################################
rm -rf "$HOME/.m2/repository/io/foldright"/cffu*
cu::log_then_run scripts/integration_test
cu::log_then_run demos/scripts/integration_test

########################################
# maven deploy
########################################
rm -rf "$HOME/.m2/repository/io/foldright"/cffu*
cu::log_then_run ./mvnw clean
cu::log_then_run ./mvnw deploy -DperformRelease -Dmaven.test.skip
