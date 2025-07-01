package io.github.breninsul.logging.aspect.annotation

import io.github.breninsul.logging.aspect.JavaLoggingLevel


/**
 * Annotation to enable logging of errors in methods or classes.
 *
 * This annotation can be applied to methods, property getters, setters, or classes
 * to automatically log errors when they occur. It provides configurable options such as
 * the logging level, whether to log the stack trace, and a custom log message prefix.
 *
 * @property level The logging level to be used for error messages.
 *                 Defaults to `JavaLoggingLevel.INFO`.
 * @property logStacktrace Indicates whether a stack trace should be included in the log
 *                         message. Defaults to `true`.
 * @property logPrefix A custom prefix to include in log messages for additional context.
 *                     Defaults to an empty string.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
)
annotation class LogError(val level: JavaLoggingLevel = JavaLoggingLevel.INFO,
                          val logStacktrace:Boolean=true,
                          val logPrefix:String= LOG_PREFIX
    ){

}
