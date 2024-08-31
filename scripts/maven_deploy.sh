#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to the project root directory
readonly PROJECT_ROOT=${SELF_DIR%/*}
cd "$PROJECT_ROOT"

# cd to the project root directory
readonly PROJECT_ROOT=${SELF_DIR%/*}
cd "$PROJECT_ROOT"

source "scripts/bash-buddy/lib/java_utils.sh"

jvu::switch_to_jdk 21

########################################
# integration test
########################################
if [ "${1:-}" = -it ]; then
  rm -rf "$HOME/.m2/repository/io/foldright"/cffu*
  cu::log_then_run scripts/integration_test
  cu::log_then_run demos/scripts/integration_test
fi

########################################
# maven deploy
########################################
rm -rf "$HOME/.m2/repository/io/foldright"/cffu*
cu::log_then_run ./mvnw clean
cu::log_then_run ./mvnw deploy -DperformRelease -Dmaven.test.skip
