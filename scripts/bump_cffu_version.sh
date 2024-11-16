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

# shellcheck disable=SC2046
sed -i "s/1.0.0-Alpha29/$new_version/g" $(rg 1.0.0-Alpha29 -l)
# shellcheck disable=SC2046
sed -i "s/$old_version/$new_version/g" $(rg "$old_version" -l)
