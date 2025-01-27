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

readonly OLD_VERSION=$1
readonly OLD_ALPHA_VERSION=$1-Alpha
readonly NEW_VERSION=$2
readonly NEW_ALPHA_VERSION=$2-Alpha

ignoreFailRg() {
  rg "$@" || true
}

myXargs() {
  xargs --no-run-if-empty --delimiter='\n' --verbose "$@"
}



cu::log_then_run sed -i -r \
  's/(\s*).*UPDATE to Alpha version WHEN RELEASE.*/\1<version>'"$NEW_ALPHA_VERSION"'<\/version>/' \
  pom.xml ./*/pom.xml ./*/*/pom.xml

ignoreFailRg "$OLD_ALPHA_VERSION" -Fl -g '!scripts/' | myXargs sd -F "$OLD_ALPHA_VERSION" "$NEW_ALPHA_VERSION"
ignoreFailRg '1.x-SNAPSHOT' -Fl -g '!scripts/' | myXargs sd -F '1.x-SNAPSHOT' "$NEW_VERSION"

ignoreFailRg "$OLD_VERSION" -Fl -g '!scripts/' | myXargs sd -F "$OLD_VERSION" "$NEW_VERSION"
