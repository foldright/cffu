#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to project directory
cd "$SELF_DIR"/..

source "$SELF_DIR/bash-buddy/lib/trap_error_info.sh"
source "$SELF_DIR/bash-buddy/lib/common_utils.sh"

isValidVersion() {
  [[ $1 =~ ^[-.[:alnum:]]+$ ]]
}

readonly NEW_VERSION=$1
isValidVersion "$NEW_VERSION" || cu::die "invalid new version: $2"

cu::log_then_run sed -i \
  "0,/<version>/s#<version>.*#<version>$NEW_VERSION</version>#" \
  pom.xml cffu*/pom.xml demos/pom.xml demos/*/pom.xml
