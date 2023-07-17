#!/bin/bash
set -eEuo pipefail
# adjust current dir to script dir
cd "$(dirname "$(readlink -f "$0")")"

source "$PWD/bash-buddy/lib/trap_error_info.sh"
source "$PWD/bash-buddy/lib/common_utils.sh"
source "$PWD/bash-buddy/lib/java_utils.sh"

readonly nl=$'\n' # new line

# shellcheck disable=SC2154
[ $# -ne 1 ] && cu::die "need only 1 argument for version!$nl${nl}usage:$nl $0 x.y.z"
readonly new_version="$1"

bump_cffu_version() {
  jvb::mvn_cmd \
    org.codehaus.mojo:versions-maven-plugin:2.15.0:set \
    -DgenerateBackupPoms=false \
    -DprocessAllModules=true \
    -DnewVersion="$new_version"
}

cd ..
bump_cffu_version

cd demos
bump_cffu_version
