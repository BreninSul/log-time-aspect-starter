package io.github.breninsul.logging.aspect.aspect

import io.github.breninsul.logging.aspect.JavaLoggingLevel
import io.github.breninsul.logging.aspect.annotation.DEFAULT_LOG_TIME
import io.github.breninsul.logging.aspect.annotation.LogExecutionTime
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.logging.Level

/**
 * The LoggingTimeAspect class provides an aspect implementation for
 * logging method execution times. It leverages annotations to determine if
 * and when method execution times should be logged, providing a mechanism
 * to monitor and evaluate performance.
 *
 * This class uses AspectJ for defining pointcuts and around advice to
 * intercept method execution.
 */
@Aspect
open class LoggingTimeAspect : CachedLoggerAspect {
    /**
     * Defines a pointcut for AspectJ to intercept the execution of methods
     * annotated with `@LogExecutionTime` or methods within classes annotated
     * with `@LogExecutionTime`.
     *
     * This pointcut is used to identify the join points where logging of
     * method execution time should be applied. It acts as a selector and
     * is leveraged by aspects, such as those performing logging or timing
     * measurements.
     */
    @Pointcut("""
        @annotation(io.github.breninsul.logging.aspect.annotation.LogExecutionTime)||
        @annotation(io.github.breninsul.logging.aspect.annotation.LogExecutionTime.Container)||
        within(@io.github.breninsul.logging.aspect.annotation.LogExecutionTime *)||
        within(@io.github.breninsul.logging.aspect.annotation.LogExecutionTime.Container *)
        """)
    open fun callAt() {
    }

    /**
     * Logs the execution time of a method annotated with `@LogExecutionTime`
     * or residing in a class annotated with this annotation. If the method
     * execution time exceeds the configured threshold, a log message is
     * generated. The log message includes the declaring class name, method
     * name, execution time, and whether an exception occurred during the
     * method execution.
     *
     * @param joinPoint The `ProceedingJoinPoint` representing the target
     *    method being intercepted. This provides access to method details and
     *    facilitates method execution.
     * @return The result of the method execution, or `null` if the method has
     *    no return value.
     * @throws Throwable If the target method throws any exceptions during its
     *    execution.
     */
    @Around(value = "callAt()")
    @Throws(Throwable::class)
    open fun logMethodTime(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        var exception = false
        return try {
            joinPoint.proceed()
        } catch (t: Throwable) {
            exception = true
            throw t
        } finally {
            val signature = joinPoint.signature as MethodSignature
            val declaringType = signature.declaringType
            val timeTook = System.currentTimeMillis() - start
            val config = getLoggingConfig(signature, declaringType, timeTook)
            if (config != null) {
                val (loggingLevel,minTime,logPrefix) = config
                val logger = getLogger(joinPoint)
                val msg = "Time metric ${declaringType.simpleName}:${signature.name} took $timeTook ms. Exception:$exception"
                logger.log(loggingLevel, "$logPrefix$msg")
            }
        }
    }

    /**
     * Retrieves the logging configuration based on the presence of
     * `LogExecutionTime` annotations on the provided method or class and the
     * time taken to execute a method. It checks method-level annotations first
     * and falls back to class-level annotations if none are found for the
     * method.
     *
     * @param sign The `MethodSignature` representing the method being
     *    evaluated for logging configuration.
     * @param type The `Class` on which the method resides, used to check for
     *    class-level logging annotations.
     * @param timeTook The time (in milliseconds) taken to execute the method.
     * @return A `Pair` containing the logging `Level` and the corresponding
     *    time threshold in milliseconds (`logIfTookMoreThenMs`) if a suitable
     *    annotation is found, otherwise `null`.
     */
    protected open fun getLoggingConfig(
        sign: MethodSignature,
        type: Class<Any>,
        timeTook: Long
    ): Triple<Level, Long, String>? {
        val methodAnnotations = sign.method.getAnnotationsByType(LogExecutionTime::class.java).toList()
        if (methodAnnotations.isNotEmpty()) {
            return getTimeConfig(methodAnnotations, timeTook)
        }
        val classAnnotations = type.getAnnotationsByType(LogExecutionTime::class.java).toList()
        if (classAnnotations.isNotEmpty()) {
            return getTimeConfig(classAnnotations, timeTook)
        }
        return null
    }

    /**
     * Determines the appropriate logging configuration based on the provided
     * method annotations and the time taken to execute a method. It evaluates
     * a list of `LogExecutionTime` annotations and selects the one with the
     * highest `logIfTookMoreThenMs` value that is still less than the provided
     * `timeTook` parameter.
     *
     * @param annotations A list of `LogExecutionTime` annotations defined on
     *    the method being logged.
     * @param timeTook The time (in milliseconds) taken to execute the method.
     * @return A `Pair` of the corresponding `Level` and `logIfTookMoreThenMs`
     *    value from the selected annotation.
     */
    protected open fun getTimeConfig(
        annotations: List<LogExecutionTime>,
        timeTook: Long
    ): Triple<Level, Long, String>? {
        // Filter annotations where logIfTookMoreThenMs is less than timeTook or is DEFAULT_LOG_TIME (-1)
        val filteredAnnotations = annotations.filter {
            it.logIfTookMoreThenMs < timeTook
        }
        // If no annotations match, return null (this will prevent logging)
        if (filteredAnnotations.isEmpty()) {
            // Use the first annotation's level but with a threshold that won't be logged
            return null
        }
        // Find the annotation with the highest logIfTookMoreThenMs value
        val config = filteredAnnotations.maxBy { it.logIfTookMoreThenMs }
        if (config.level== JavaLoggingLevel.OFF){
            return null
        }
        return Triple(config.level.javaLevel, config.logIfTookMoreThenMs, config.logPrefix)
    }

}
