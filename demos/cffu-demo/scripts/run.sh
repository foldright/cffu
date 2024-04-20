#!/bin/bash
set -eEuo pipefail
# adjust current dir to script dir
cd "$(dirname "$(readlink -f "$0")")"/..

readonly BABY_ROOT="$PWD/../../scripts/bash-buddy"
source "$BABY_ROOT"/lib/trap_error_info.sh
source "$BABY_ROOT"/lib/common_utils.sh
source "$BABY_ROOT"/lib/maven_utils.sh

mvu::mvn_cmd clean compile exec:exec
mvu::mvn_cmd exec:exec -Dexec.main.class=io.foldright.demo.cffu.CompletableFutureUtilsDemo
mvu::mvn_cmd exec:exec -Dexec.main.class=io.foldright.demo.cffu.CovariantDemo
