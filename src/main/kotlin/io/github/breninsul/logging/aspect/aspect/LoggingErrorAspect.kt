package io.github.breninsul.logging.aspect.aspect

import io.github.breninsul.logging.aspect.JavaLoggingLevel
import io.github.breninsul.logging.aspect.annotation.LogError
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.logging.Level

/**
 * Aspect responsible for intercepting methods or classes annotated with `@LogError` and
 * logging any exceptions that are thrown during their execution. This aspect enables
 * centralized error logging and provides the capability to set logging levels and control
 * whether stack traces are logged.
 *
 * The aspect uses a pointcut to identify annotated methods or classes and applies error
 * logging logic accordingly. The logging behavior is customizable by using properties
 * defined in the `@LogError` annotation.
 */
@Aspect
open class LoggingErrorAspect:CachedLoggerAspect {

    /**
     * Defines a pointcut for intercepting methods or classes annotated with the `@LogError` annotation,
     * or within classes annotated with `@LogError`. This pointcut helps in handling logging of errors
     * as defined in the logging aspect.
     *
     * The pointcut identifies join points where error-related logging should be applied, specifically
     * targeting methods or classes marked with the `@LogError` annotation.
     */
    @Pointcut("@annotation(io.github.breninsul.logging.aspect.annotation.LogError)|| within(@io.github.breninsul.logging.aspect.annotation.LogError *)")
    open fun callAt() {
    }

    /**
     * Logs the execution of a method and any exceptions thrown during its execution.
     * Captures details about the error, including a custom message and optionally
     * the stack trace, based on the logging level and configurations provided
     * through method or class annotations.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method invocation.
     *                  Contains details about the target method, class, and arguments.
     * @return The result of the method's execution, if no exception is thrown.
     * @throws Throwable If an exception occurs during the method execution, it is
     *                   rethrown after logging.
     */
    @Around(value = "callAt()")
    @Throws(Throwable::class)
    open fun logMethodTime(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            joinPoint.proceed()
        } catch (t: Throwable) {
            val signature = joinPoint.signature as MethodSignature
            val declaringType = signature.declaringType
            val config = getLoggingLevel(signature, declaringType)
            if (config == null) {
                throw t
            }
            val (loggingLevel,logStacktrace,logPrefix) = config
            val logMsg = "${declaringType.simpleName}:${signature.name}"
            val logger = getLogger(joinPoint)
            if (logStacktrace){
                logger.log(loggingLevel, "$logPrefix$logMsg. Exception:", t)
            } else {
                logger.log(loggingLevel, "$logPrefix$logMsg")
            }
            throw t
        }
    }

    /**
     * Determines the logging level and whether to log the stack trace for a method or class
     * based on the presence of the `@LogError` annotation.
     *
     * If the method includes the `@LogError` annotation, its configuration is used.
     * Otherwise, if the class contains the `@LogError` annotation, its configuration is applied.
     * If neither the method nor the class has the annotation, a default logging level of `INFO`
     * with logging of the stack trace enabled is returned.
     *
     * @param sign The `MethodSignature` of the method being analyzed.
     *             Provides access to method-specific annotations.
     * @param type The `Class` object of the class containing the method.
     *             Used to check for class-level annotations.
     * @return A `Pair` where the first value is the determined `Level` for logging
     *         and the second value is a `Boolean` indicating whether stack trace logging is enabled.
     */
    open fun getLoggingLevel(
        sign: MethodSignature,
        type: Class<Any>,
    ): Triple<Level, Boolean, String>? {
        val methodAnnotation = sign.method.getAnnotation(LogError::class.java)
//        val methodLevel = methodAnnotation?.level
        if (methodAnnotation!=null){
            if (methodAnnotation.level==JavaLoggingLevel.OFF){
                return null;
            }
            return Triple(methodAnnotation.level.javaLevel , methodAnnotation.logStacktrace,methodAnnotation.logPrefix)
        }

        val classAnnotation = type.getAnnotation(LogError::class.java)
        if (classAnnotation!=null){
            if (classAnnotation.level==JavaLoggingLevel.OFF){
                return null;
            }
            return Triple(classAnnotation.level.javaLevel , classAnnotation.logStacktrace,classAnnotation.logPrefix)
        }
        return Triple(JavaLoggingLevel.INFO.javaLevel, true,"")
    }

}
