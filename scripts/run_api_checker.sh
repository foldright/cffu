#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to the project root directory
readonly PROJECT_ROOT=${SELF_DIR%/*}
cd "$PROJECT_ROOT"

readonly BASH_BUDDY_ROOT="$SELF_DIR/bash-buddy"
# shellcheck disable=SC1091
source "$BASH_BUDDY_ROOT/lib/trap_error_info.sh"
# shellcheck disable=SC1091
source "$BASH_BUDDY_ROOT/lib/java_utils.sh"
# shellcheck disable=SC1091
source "$BASH_BUDDY_ROOT/lib/maven_utils.sh"

# shellcheck disable=SC2034
readonly MVU_MVN_OPTS=(
  "${MVU_DEFAULT_MVN_OPTS[@]}"
  -Denforcer.skip -Dmaven.test.skip
  -Dmoditect.skip -Djacoco.skip
  ${CI_MORE_MVN_OPTS:+${CI_MORE_MVN_OPTS}}
)

jvu::switch_to_jdk 21

########################################
# find released versions
########################################

MAVEN_METADATA=$(curl -s https://repo1.maven.org/maven2/io/foldright/cffu/maven-metadata.xml)
readonly MAVEN_METADATA
cu::blue_echo "cat maven-metadata.xml:"
echo "$MAVEN_METADATA"

REL_VERSIONS=$(awk -F'</?version>' '/1\.[0-9]+\.[0-9]+<\/version>/{print $2}' <<<"$MAVEN_METADATA" | sort -V)
LATEST_REL_VERSION=$(tail -1 <<<"$REL_VERSIONS")
readonly REL_VERSIONS LATEST_REL_VERSION
cu::blue_echo "Found released GA versions:"
echo "$REL_VERSIONS"

########################################
# show next release version
########################################

HEAD_COMMIT_ID=$(git rev-parse HEAD)
readonly HEAD_COMMIT_ID

cu::head_line_echo "Determinate next release version by revapi:update-release-properties"

rm -rf release.properties
mvu::mvn_cmd install
mvu::mvn_cmd -Dmaven.resources.skip -Dmaven.main.skip -Dmaven.compile.skip -Dkotlin.skip \
  -pl '.,cffu-core,cffu-ttl-executor-wrapper' \
  -Drevapi.oldVersion="$LATEST_REL_VERSION" revapi:update-release-properties

cu::blue_echo "release.properties:"
cat release.properties

NEXT_REL_VERSION=$(awk -F'[=-]' '/developmentVersion=/ {print $2}' release.properties)
readonly NEXT_REL_VERSION
cu::yellow_echo "Next release version: $NEXT_REL_VERSION"

printf "API_CHECKER_NEXT_REL_VERSION=%s\nAPI_CHECKER_LATEST_REL_VERSION=%s\n" \
  "$NEXT_REL_VERSION" "$LATEST_REL_VERSION" >"next.release.version.info.$HEAD_COMMIT_ID"

########################################
# check API to all released GA versions
########################################

if [ "${1:-}" = "--skip-api-check" ]; then
  exit
fi

cu::head_line_echo "Check API compatibility by revapi:check"
for v in $REL_VERSIONS; do
  cu::blue_echo "Check API compatibility with released GA version $v"
  mvu::mvn_cmd -pl cffu-core,cffu-ttl-executor-wrapper \
    -Drevapi.oldVersion="$v" revapi:check
done
