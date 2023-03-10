#!/bin/bash
set -eEuo pipefail
cd "$(dirname "$(readlink -f "$0")")"

readonly TEST_FILE_DIR="$PWD/../src/test/java/io/foldright/apitest"

sed -r '/^import /,${
  /\bKEEP_IN_GEN\b/b

  s/import org.junit.jupiter.api.Test;/import io.foldright.cffu.Cffu;\nimport io.foldright.cffu.CffuFactory;\nimport io.foldright.cffu.CffuFactoryBuilder;\nimport org.junit.jupiter.api.Test;/

  /import java.util.concurrent.CompletableFuture;/d
  /@EnabledForJreRange\(.*\)$/d

  s/public class CompletableFutureUserApiTest \{/public class CffuUserApiTest \{\n    private static final CffuFactory cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder\(commonPool\(\)\).build\(\);\n/

  s/\bCompletableFuture\./cffuFactory\./g
  s/\bCompletableFuture\b/Cffu/g
  s/\bApiTestHelper\.newFailedCompletableFuture\(/ApiTestHelper\.newFailedCffu\(/g
}' "$TEST_FILE_DIR/CompletableFutureUserApiTest.java" >"$TEST_FILE_DIR/CffuUserApiTest.java"
