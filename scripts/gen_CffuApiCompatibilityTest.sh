#!/bin/bash
set -eEuo pipefail
# the canonical path of this script
SELF_PATH=$(realpath -- "$0")
readonly SELF_PATH SELF_DIR=${SELF_PATH%/*}
# cd to script dir
cd "$SELF_DIR"

readonly TEST_FILE_DIR="$PWD/../cffu-core/src/test/java/io/foldright/compatibility_test"
readonly source_file="$TEST_FILE_DIR/CompletableFutureApiCompatibilityTest.java"
readonly target_file="$TEST_FILE_DIR/CffuApiCompatibilityTest.java"

trap 'chmod -w "$target_file"' EXIT
chmod +w "$target_file"

UNAME=$(uname)
if [[ $UNAME = Darwin* ]]; then
  if ! type -P gsed &>/dev/null; then
    echo 'missing gnu-sed, install:'
    echo '  brew install gnu-sed'
    exit 1
  fi
  readonly SED_CMD=gsed
else
  readonly SED_CMD=sed
fi

"$SED_CMD" -r '
  /^import /,/^import static/ {
    # adjust imports
    s/import io\.foldright\.test_utils\.TestingExecutorUtils;/import io.foldright.cffu2.Cffu;\nimport io.foldright.cffu2.CffuFactory;\nimport io.foldright.test_utils.TestingExecutorUtils;/
    /import java.util.concurrent.CompletableFuture;/d
  }

  /^class /,$ {
    # skip if GEN_MARK_KEEP
    /\bGEN_MARK_KEEP\b/b

    # replace JUnit test class name
    s/\bclass\s+CompletableFutureApiCompatibilityTest\b/class CffuApiCompatibilityTest/

    /@EnabledForJreRange\(.*\)$/d

    # replace CompatibilityTestHelper methods
    s/\b(TestUtils)\.(\w*)CompletableFuture(\w*)/\1\.\2Cffu\3/g

    # replace CompletableFuture constructor to cffuFactory.newIncompleteCffu() methods
    s/\bnew\s+CompletableFuture<>/cffuFactory.newIncompleteCffu/g

    # replace static methods of CompletableFuture
    s/\bCompletableFuture\./cffuFactory\./g

    # replace CompletableFuture class name
    s/\bCompletableFuture\b/Cffu/g

    # generate new contents
    s/^(\s*).*\bGEN_MARK_FACTORY_FIELD\b.*$/\1private static CffuFactory cffuFactory;/
    s/^(\s*).*\bGEN_MARK_FACTORY_INIT\b.*$/\1cffuFactory = CffuFactory.builder\(executorService\).build\(\);/
  }
' "$source_file" >"$target_file"
