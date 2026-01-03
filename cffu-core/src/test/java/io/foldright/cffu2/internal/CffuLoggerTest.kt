package io.foldright.cffu2.internal

import io.foldright.cffu2.config.CffuConfiguration
import io.kotest.core.spec.style.FunSpec


private val exceptionLogFormat = CffuLogger.exceptionLogFormat

private val logger = CffuLogger.getLogger(CffuLoggerTest::class.java)

class CffuLoggerTest : FunSpec({
    val rte = RuntimeException("CffuLoggerTest")

    test("CffuLogger should not log exception stack trace by default") {
        CffuConfiguration.setExceptionLogFormat(CffuConfiguration.ExceptionLogFormat.NONE)
        logger.logException(CffuLogger.Level.WARN, "NONE - logException - Hello", rte)
        logger.log(CffuLogger.Level.WARN, "NONE - log - Hello")

        CffuConfiguration.setExceptionLogFormat(CffuConfiguration.ExceptionLogFormat.SHORT)
        logger.logException(CffuLogger.Level.WARN, "SHORT - logException - Hello", rte)
        logger.log(CffuLogger.Level.WARN, "SHORT - log - Hello")

        CffuConfiguration.setExceptionLogFormat(CffuConfiguration.ExceptionLogFormat.FULL)
        logger.logException(CffuLogger.Level.WARN, "FULL - logException - Hello", rte)
        logger.log(CffuLogger.Level.WARN, "FULL - log - Hello")
    }

    afterSpec {
        CffuLogger.exceptionLogFormat = exceptionLogFormat
    }
})
