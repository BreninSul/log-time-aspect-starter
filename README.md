# Log Time Aspect Starter

A lightweight Spring Boot starter library that provides aspect-oriented logging capabilities for method execution time and error handling.

## Features

- Log method execution time with customizable thresholds
- Log method errors with customizable stack trace options
- Support for both method-level and class-level annotations
- Configurable logging levels
- Custom log message prefixes
- Spring Boot auto-configuration

## Requirements

- Java 17 or higher
- Spring Boot 3.x
- AspectJ

## Installation

Add the following dependency to your project:

```kotlin
dependencies {
    implementation("io.github.breninsul:log-time-aspect-starter:1.0.1")
}
```

For Maven:

```xml
<dependency>
    <groupId>io.github.breninsul</groupId>
    <artifactId>log-time-aspect-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Usage

### Logging Method Execution Time

Use the `@LogExecutionTime` annotation to log the execution time of methods:

```kotlin
import io.github.breninsul.logging.aspect.annotation.LogExecutionTime
import io.github.breninsul.logging.aspect.JavaLoggingLevel

class MyService {
    
    //Log with different logging level depends on execution time
    @LogExecutionTime(logIfTookMoreThenMs = 10, level = JavaLoggingLevel.FINE)
    @LogExecutionTime(logIfTookMoreThenMs = 100, level = JavaLoggingLevel.INFO)
    @LogExecutionTime(logIfTookMoreThenMs = 200, level = JavaLoggingLevel.WARNING)
    open fun logCorrectLevel() {
        // This method should log because it takes more than 100ms
        TimeUnit.MILLISECONDS.sleep(120)
        // Log directly to verify the logger is working
        val logger = Logger.getLogger(this.javaClass.name)
        logger.info("Direct log from logCorrectLevel")
    }
    
    // Log with default settings (INFO level, always log)
    @LogExecutionTime
    fun defaultMethod() {
        // Method implementation
    }

    // Log with WARNING level
    @LogExecutionTime(level = JavaLoggingLevel.WARNING)
    fun warningLevelMethod() {
        // Method implementation
    }

    // Log only if execution takes more than 100ms
    @LogExecutionTime(logIfTookMoreThenMs = 100)
    fun logIfTookMoreThanMethod() {
        // Method implementation
    }

    // Log with custom prefix
    @LogExecutionTime(logPrefix = "PERFORMANCE: ")
    fun customPrefixMethod() {
        // Method implementation
    }
    
}
```

You can also apply the annotation at the class level to log all methods in the class:

```kotlin
@LogExecutionTime
class MyService {
    // All methods in this class will have execution time logged
}
```

### Logging Method Errors

Use the `@LogError` annotation to log errors that occur during method execution:

```kotlin
import io.github.breninsul.logging.aspect.annotation.LogError
import io.github.breninsul.logging.aspect.JavaLoggingLevel

class MyService {

    // Log errors with default settings (INFO level, with stack trace)
    @LogError
    fun defaultMethod() {
        // Method implementation that might throw an exception
    }

    // Log errors with WARNING level
    @LogError(level = JavaLoggingLevel.WARNING)
    fun warningLevelMethod() {
        // Method implementation
    }

    // Log errors without stack trace
    @LogError(logStacktrace = false)
    fun noStacktraceMethod() {
        // Method implementation
    }

    // Log errors with custom prefix
    @LogError(logPrefix = "ERROR: ")
    fun customPrefixMethod() {
        // Method implementation
    }
}
```

You can also apply the annotation at the class level to log errors for all methods in the class:

```kotlin
@LogError
class MyService {
    // Errors in all methods of this class will be logged
}
```

## Configuration

The library can be configured using Spring Boot properties:

```properties

# Disable aspect auto-configuration
aspect.logging.disabled=true
```

## Logging Levels

The library supports the following logging levels (from highest to lowest severity):

- `JavaLoggingLevel.SEVERE`
- `JavaLoggingLevel.WARNING`
- `JavaLoggingLevel.INFO` (default)
- `JavaLoggingLevel.CONFIG`
- `JavaLoggingLevel.FINE`
- `JavaLoggingLevel.FINER`
- `JavaLoggingLevel.FINEST`

## License

This project is licensed under the MIT License - see the LICENSE file for details.
