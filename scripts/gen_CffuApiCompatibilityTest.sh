#!/bin/bash
set -eEuo pipefail
cd "$(dirname "$(readlink -f "$0")")"

readonly TEST_FILE_DIR="$PWD/../cffu-core/src/test/java/io/foldright/compatibility_test"
readonly source_file="$TEST_FILE_DIR/CompletableFutureApiCompatibilityTest.java"
readonly target_file="$TEST_FILE_DIR/CffuApiCompatibilityTest.java"

trap 'chmod -w "$target_file"' EXIT
chmod +w "$target_file"

sed -r '/^import /,${
  # skip if GEN_MARK_KEEP
  /\bGEN_MARK_KEEP\b/b

  # adjust imports
  s/import io\.foldright\.test_utils\.TestThreadPoolManager;/import io.foldright.cffu.Cffu;\nimport io.foldright.cffu.CffuFactory;\nimport io.foldright.cffu.CffuFactoryBuilder;\nimport io.foldright.test_utils.TestThreadPoolManager;/
  /import java.util.concurrent.CompletableFuture;/d

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
  s/^(\s*).*\bGEN_MARK_FACTORY_INIT\b.*$/\1cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder\(executorService\).build\(\);/

}' "$source_file" >"$target_file"
