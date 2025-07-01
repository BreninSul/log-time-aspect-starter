package io.github.breninsul.logging.aspect.config

import io.github.breninsul.logging.aspect.aspect.LoggingErrorAspect
import io.github.breninsul.logging.aspect.aspect.LoggingTimeAspect
import org.aspectj.lang.annotation.Aspect
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * Configuration class for enabling and managing aspect-based logging.
 *
 * This class is responsible for registering beans that provide functionalities
 * for logging errors and execution times, using Aspect-Oriented Programming (AOP).
 * The configuration is conditional and only activates if the required conditions
 * are met:
 * - The `Aspect` class is present in the classpath.
 * - The property `aspect.aspect.disabled` is set to `false` or is missing.
 *
 * The beans defined in this class enable the following:
 * - Logging errors during method execution through the `LoggingErrorAspect`.
 * - Logging the execution time of methods through the `LoggingTimeAspect`.
 *
 * An instance of `AspectLoggingProperties` is used to configure whether
 * aspect-based logging is enabled or disabled.
 */
@ConditionalOnClass(Aspect::class)
@ConditionalOnProperty(prefix = "aspect.logging", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(AspectLoggingProperties::class)
class AspectsLoggingConfig {

    @Bean
    fun getLoggingErrorAspect(): LoggingErrorAspect {
        return LoggingErrorAspect()
    }

    @Bean
    fun getLoggingTimeAspect(): LoggingTimeAspect {
        return LoggingTimeAspect()
    }
}
