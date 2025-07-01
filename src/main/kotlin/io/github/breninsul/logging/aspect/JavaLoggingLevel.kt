package io.github.breninsul.logging.aspect

import java.util.logging.Level

/**
 * Enum representing the various logging levels in Java's `java.util.logging.Level`.
 *
 * This enumeration maps the Java logging levels to their corresponding
 * `Level` constants, which are used for logging messages with varying
 * levels of severity or importance.
 *
 * The levels are ordered from highest severity (`SEVERE`) to lowest severity
 * (`FINEST`), providing flexibility for defining and filtering log messages
 * according to their priority.
 *
 * @property javaLevel The corresponding `java.util.logging.Level` constant
 *                     for this logging level.
 */
enum class JavaLoggingLevel(val javaLevel: Level) {
    SEVERE(Level.SEVERE), //(highest value)
    WARNING(Level.WARNING),
    INFO(Level.INFO),
    CONFIG(Level.CONFIG),
    FINE(Level.FINE),
    FINER(Level.FINER),
    FINEST(Level.FINEST),
    OFF(Level.OFF)
// (lowest value)
}