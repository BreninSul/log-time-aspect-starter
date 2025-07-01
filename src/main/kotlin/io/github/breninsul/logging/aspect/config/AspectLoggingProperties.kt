package io.github.breninsul.logging.aspect.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for aspect-based logging.
 *
 * This class is used to configure and manage the behavior of logging aspects
 * in an application. The properties defined here can be set through
 * application configuration files using the "aspect.logging" prefix.
 *
 * @property disabled Indicates whether aspect logging is disabled. Default is false.
 */
@ConfigurationProperties("aspect.logging")
open class AspectLoggingProperties {
    var disabled: Boolean = false
}
