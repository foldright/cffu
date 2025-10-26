#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to project directory
cd "$SELF_DIR"/..

source "$SELF_DIR/bash-buddy/lib/trap_error_info.sh"
source "$SELF_DIR/bash-buddy/lib/common_utils.sh"

################################################################################
# util functions
################################################################################

escapeLiteralForRegex() {
  # shellcheck disable=SC2001
  sed -r 's#([/\^$.|?*+([{])#\\\1#g' <<<"$1"
}

isValidVersion() {
  [[ $1 =~ ^[-.[:alnum:]]+$ ]]
}

ignoreFailRg() {
  cu::log_then_run rg "$@" || true
}

myXargs() {
  xargs --no-run-if-empty --delimiter='\n' --verbose "$@"
}

################################################################################
# biz logic
################################################################################

HEAD_COMMIT_ID=$(git rev-parse HEAD)
readonly HEAD_COMMIT_ID REL_VERSION_INFO_FILE="next.release.version.info.$HEAD_COMMIT_ID"

if [ -f "$REL_VERSION_INFO_FILE" ]; then
  # shellcheck disable=SC1090
  source "$REL_VERSION_INFO_FILE"
fi

if [ -n "${API_CHECKER_NEXT_REL_VERSION:-}" ]; then
  readonly OLD_VERSION=$API_CHECKER_LATEST_REL_VERSION
  readonly NEW_VERSION=$API_CHECKER_NEXT_REL_VERSION
else
  # shellcheck disable=SC2154
  [ $# -eq 2 ] || cu::die "need exact 2 argument for old and new versions!"

  readonly OLD_VERSION=$1
  readonly NEW_VERSION=$2
fi

isValidVersion "$OLD_VERSION" || cu::die "invalid old version: $1"
isValidVersion "$NEW_VERSION" || cu::die "invalid new version: $2"

#readonly ALPHA_SUFFIX=-Alpha
#[[ $NEW_VERSION != *"$ALPHA_SUFFIX" ]] || cu::die "invalid new version, must NOT end with $ALPHA_SUFFIX: $2"
#readonly NEW_ALPHA_VERSION=$2$ALPHA_SUFFIX

#cu::log_then_run sed -i -r \
#  's#(\s*).*UPDATE to Alpha version WHEN RELEASE.*#\1<version>'"$NEW_ALPHA_VERSION"'</version>#' \
#  pom.xml ./*/pom.xml ./*/*/pom.xml

readonly NON_VERSION_CHAR_REGEX='[^-.[:alnum:]]'

SEARCH_PATTERN="(^|$NON_VERSION_CHAR_REGEX)($(escapeLiteralForRegex "2.x-SNAPSHOT")|$(escapeLiteralForRegex "$OLD_VERSION"))($NON_VERSION_CHAR_REGEX|$)"
readonly SEARCH_PATTERN

ignoreFailRg "$SEARCH_PATTERN" -l -g '!scripts/' |
  myXargs sed -i -r "s#$SEARCH_PATTERN#\1$NEW_VERSION\3#g"
