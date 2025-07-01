package io.github.breninsul.logging.aspect.aspect

import io.github.breninsul.logging.aspect.LoggerClassesCache
import org.aspectj.lang.ProceedingJoinPoint
import java.util.logging.Logger

interface CachedLoggerAspect {
    /**
     * Retrieves a logger instance associated with the target class of the provided join point.
     * This method utilizes the LoggerClassesCache to ensure thread-safe access and management of loggers.
     *
     * @param joinPoint The `ProceedingJoinPoint` representing the method or code execution point for which
     *                  the logger is required.
     * @return A `Logger` instance associated with the target class of the specified join point.
     */
     open fun getLogger(joinPoint: ProceedingJoinPoint): Logger {
        return LoggerClassesCache.INSTANCE.getLogger(joinPoint)
    }
}