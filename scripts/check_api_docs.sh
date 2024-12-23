#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to project directory
cd "$SELF_DIR"/..


DOC_DIRS=(
 target/reports/apidocs
 cffu-core/target/reports/apidocs
 cffu-ttl-executor-wrapper/target/reports/apidocs
)

invalid=false

for d in "${DOC_DIRS[@]}"; do
  if [ -f "$d/allclasses-index.html" ]; then
    grep -F '<summary>invalid reference</summary>' -n -C2 -r "$d" && invalid=true
  fi
done

! $invalid
