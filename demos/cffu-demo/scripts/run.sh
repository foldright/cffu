#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}
# cd to demo project
cd "$SELF_DIR/.."

readonly BABY_ROOT="$PWD/../../scripts/bash-buddy"
source "$BABY_ROOT"/lib/trap_error_info.sh
source "$BABY_ROOT"/lib/common_utils.sh
source "$BABY_ROOT"/lib/maven_utils.sh

mvu::mvn_cmd clean compile exec:exec
mvu::mvn_cmd exec:exec -Dexec.main.class=io.foldright.demo.cffu.CompletableFutureUtilsDemo
mvu::mvn_cmd exec:exec -Dexec.main.class=io.foldright.demo.cffu.CovariantDemo
