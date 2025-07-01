package io.github.breninsul.logging.aspect.annotation

import io.github.breninsul.logging.aspect.JavaLoggingLevel


/**
 * Annotation for logging the execution time of methods or classes.
 *
 * This annotation can be used to measure and log the execution time of annotated methods or classes.
 * It provides options to specify a logging level, a threshold for logging if the execution
 * exceeds a specified duration, and a custom log message prefix.
 *
 * The annotation can be applied to the following targets:
 * - Functions
 * - Property getters
 * - Property setters
 * - Classes
 * - Other annotations
 *
 * @property level Specifies the logging level to be applied when logging execution time.
 *                 Defaults to `JavaLoggingLevel.INFO`.
 * @property logIfTookMoreThenMs Defines a threshold in milliseconds. If the execution time exceeds
 *                               this threshold, the time is logged. Defaults to `DEFAULT_LOG_TIME`,
 *                               which means no threshold is applied.
 * @property logPrefix A custom string to prepend to log messages for additional context.
 *                     Defaults to `LOG_PREFIX`, which is an empty string.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
)
@Repeatable
annotation class LogExecutionTime(
    val level: JavaLoggingLevel = JavaLoggingLevel.INFO,
    val logIfTookMoreThenMs: Long = DEFAULT_LOG_TIME,
    val logPrefix:String= LOG_PREFIX
)
