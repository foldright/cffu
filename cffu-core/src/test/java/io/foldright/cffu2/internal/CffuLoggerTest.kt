package io.foldright.cffu2.internal

import io.foldright.cffu2.config.CffuConfiguration
import io.kotest.core.spec.style.FunSpec


private val exceptionLoggingFormat = CffuLogger.exceptionLoggingFormat

class CffuLoggerTest : FunSpec({
    val rte = RuntimeException("CffuLoggerTest")

    test("CffuLogger should not log exception stack trace by default") {
        CffuConfiguration.setExceptionLoggingFormat(CffuConfiguration.ExceptionLoggingFormat.NONE)
        CffuLogger.logException(CffuLogger.Level.WARN, "NONE - logException - Hello", rte)
        CffuLogger.log(CffuLogger.Level.WARN, "NONE - log - Hello")

        CffuConfiguration.setExceptionLoggingFormat(CffuConfiguration.ExceptionLoggingFormat.SHORT)
        CffuLogger.logException(CffuLogger.Level.WARN, "SHORT - logException - Hello", rte)
        CffuLogger.log(CffuLogger.Level.WARN, "SHORT - log - Hello")

        CffuConfiguration.setExceptionLoggingFormat(CffuConfiguration.ExceptionLoggingFormat.FULL)
        CffuLogger.logException(CffuLogger.Level.WARN, "FULL - logException - Hello", rte)
        CffuLogger.log(CffuLogger.Level.WARN, "FULL - log - Hello")
    }

    afterSpec {
        CffuConfiguration.setExceptionLoggingFormat(exceptionLoggingFormat)
    }
})
