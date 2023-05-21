#!/bin/bash
set -eEuo pipefail
# adjust current dir to script dir
cd "$(dirname "$(readlink -f "$0")")"/..

readonly BABY_ROOT="$PWD/../../scripts/bash-buddy"
source "$BABY_ROOT"/lib/trap_error_info.sh
source "$BABY_ROOT"/lib/common_utils.sh
source "$BABY_ROOT"/lib/java_build_utils.sh

jvb::mvn_cmd clean compile exec:exec
jvb::mvn_cmd exec:exec -Prun-kotlin-demo
