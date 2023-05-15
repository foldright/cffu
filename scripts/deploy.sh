#!/bin/bash
set -eEuo pipefail
cd "$(dirname "$(readlink -f "$0")")/.."

source "scripts/bash-buddy/lib/common_utils.sh"

readonly jh_var_name="JAVA19_HOME"
[ -d "${!jh_var_name:-}" ] ||
  cu::die "\$${jh_var_name}(${!jh_var_name:-}) dir is not existed!"
export JAVA_HOME="${!jh_var_name}"

rm -rf "$HOME/.m2/repository/io/foldright"/cffu*
scripts/integration_test

rm -rf "$HOME/.m2/repository/io/foldright"/cffu*
cu::log_then_run ./mvnw clean
cu::log_then_run ./mvnw deploy -DperformRelease -Dmaven.test.skip
