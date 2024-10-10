#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}
# cd to script dir
cd "$SELF_DIR"

source "$PWD/bash-buddy/lib/trap_error_info.sh"
source "$PWD/bash-buddy/lib/common_utils.sh"
source "$PWD/bash-buddy/lib/java_utils.sh"
source "$PWD/bash-buddy/lib/maven_utils.sh"

readonly newline=$'\n'

# shellcheck disable=SC2154
[ $# -ne 1 ] && cu::die "need only 1 argument for version!$newline${newline}usage:$newline $0 x.y.z"
readonly new_version="$1"

bump_cffu_version() {
  mvu::mvn_cmd \
    org.codehaus.mojo:versions-maven-plugin:2.15.0:set \
    -DgenerateBackupPoms=false \
    -DprocessAllModules=true \
    -DnewVersion="$new_version"
}

cd ..
bump_cffu_version

cd demos
bump_cffu_version
