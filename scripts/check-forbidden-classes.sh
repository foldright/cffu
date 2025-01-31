#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}

# cd to project directory
cd "$SELF_DIR"/..

source "$SELF_DIR/bash-buddy/lib/common_utils.sh"

readonly forbidden_classes=(
  # prefer edu.umd.cs.findbugs.annotations.Nullable
  javax.annotation.Nullable
  org.jetbrains.annotations.Nullable

  # prefer edu.umd.cs.findbugs.annotations.NonNull
  javax.annotation.Nonnull
  org.jetbrains.annotations.NotNull

  # prefer edu.umd.cs.findbugs.annotations.CheckForNull
  javax.annotation.CheckReturnValue
  org.jetbrains.annotations.CheckReturnValue

  # prefer @edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters(NonNull.class)
  javax.annotation.ParametersAreNonnullByDefault

  # prefer static import methods of `Assertions`
  'org.junit.jupiter.api.Assertions;'
)

PATTERN=$(printf '%s\n' "${forbidden_classes[@]}")
[[ "${GITHUB_ACTIONS:-}" = true || -t 1 ]] && MORE_RG_OPTIONS=(--color=always)
readonly PATTERN MORE_RG_OPTIONS

! cu::log_then_run rg -f <(printf '%s\n' "$PATTERN") -F -n -C2 \
  --ignore-vcs --glob='!scripts/' --glob='!package-info.java' \
  ${MORE_RG_OPTIONS[@]:+"${MORE_RG_OPTIONS[@]}"}
