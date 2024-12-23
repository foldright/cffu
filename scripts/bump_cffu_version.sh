#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}
# cd to script dir
cd "$SELF_DIR"/..

source "$SELF_DIR/bash-buddy/lib/trap_error_info.sh"
source "$SELF_DIR/bash-buddy/lib/common_utils.sh"
source "$SELF_DIR/bash-buddy/lib/java_utils.sh"
source "$SELF_DIR/bash-buddy/lib/maven_utils.sh"

# shellcheck disable=SC2154
[ $# -ne 2 ] && cu::die "need only 2 argument for old and new versions!"

readonly old_version="$1"
readonly new_version="$2"

{ rg "1.x-SNAPSHOT" -Fl -g '!scripts/' || true; } |
  tr '\n' '\0' |
  xargs --no-run-if-empty --null --verbose sed -i "s/1\.x-SNAPSHOT/$new_version/g"

{ rg "$old_version" -Fl -g '!scripts/' || true; } |
  tr '\n' '\0' |
  xargs --no-run-if-empty --null --verbose sed -i "s/$old_version/$new_version/g"
