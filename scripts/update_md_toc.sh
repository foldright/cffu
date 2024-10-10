#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

readonly BASH_BUDDY_ROOT="$SELF_DIR/bash-buddy"
# shellcheck disable=SC1091
source "$BASH_BUDDY_ROOT/lib/common_utils.sh"

# cd to the project root directory
readonly PROJECT_ROOT=${SELF_DIR%/*}
cd "$PROJECT_ROOT"

readonly doctoc_start='<!-- START doctoc generated TOC '

update_toc() {
  local f md_files_contained_toc=()
  for f; do
    ! grep -Fq "$doctoc_start" "$f" && continue
    md_files_contained_toc=(${md_files_contained_toc[@]:+"${md_files_contained_toc[@]}"} "$f")
  done
  cu::log_then_run doctoc --notitle "${md_files_contained_toc[@]}"
}

update_toc ./*.md ./*/*.md
