#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to project directory
cd "$SELF_DIR"/..

source "$SELF_DIR/bash-buddy/lib/trap_error_info.sh"
source "$SELF_DIR/bash-buddy/lib/common_utils.sh"

# shellcheck disable=SC2154
[ $# -ne 2 ] && cu::die "need only 2 argument for old and new versions!"

readonly old_version=$1
readonly new_version=$2

ignoreFailRg() {
  rg "$@" || true
}

myXargs() {
  xargs --no-run-if-empty --delimiter='\n' --verbose "$@"
}


ignoreFailRg '1.x-SNAPSHOT' -Fl -g '!scripts/' | myXargs sd -F '1.x-SNAPSHOT' "$new_version"

ignoreFailRg "$old_version" -Fl -g '!scripts/' | myXargs sd -F "$old_version" "$new_version"
