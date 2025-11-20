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

if [ "${1:-}" = --version-from-revapi ]; then
  rm -rf cffu2.maven.metadata.tmp
  scripts/run_api_checker.sh --only-latest-version

  HEAD_COMMIT_ID=$(git rev-parse HEAD)
  readonly HEAD_COMMIT_ID
  # shellcheck disable=SC1090
  source "next.release.version.info.$HEAD_COMMIT_ID"

  readonly OLD_VERSION=$API_CHECKER_LATEST_REL_VERSION
  readonly NEW_VERSION=$API_CHECKER_NEXT_REL_VERSION
else
  # shellcheck disable=SC2154
  [ $# -eq 2 ] || cu::die "need exact 2 argument for old and new versions!"

  readonly OLD_VERSION=$1
  readonly NEW_VERSION=$2
fi

isValidVersion "$OLD_VERSION" || cu::die "invalid old version: $OLD_VERSION"
isValidVersion "$NEW_VERSION" || cu::die "invalid new version: $NEW_VERSION"

cu::log_then_run scripts/bump_proj_pom_versions.sh "$NEW_VERSION"

readonly NON_VERSION_CHAR_REGEX='[^-.[:alnum:]]'
SEARCH_PATTERN="(^|$NON_VERSION_CHAR_REGEX)($(escapeLiteralForRegex "$OLD_VERSION"))($NON_VERSION_CHAR_REGEX|$)"
ignoreFailRg "$SEARCH_PATTERN" -l -g '!scripts/' -g '!pom.xml' |
  myXargs sed -i -r "s#$SEARCH_PATTERN#\1$NEW_VERSION\3#g"
