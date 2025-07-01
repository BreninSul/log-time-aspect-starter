package io.github.breninsul.logging.aspect.annotation

import io.github.breninsul.logging.aspect.JavaLoggingLevel
import io.github.breninsul.logging.aspect.aspect.LoggingErrorAspect
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import java.util.logging.Level
import java.util.logging.Logger
import org.assertj.core.api.Assertions.assertThat
import java.util.logging.Handler
import java.util.logging.LogRecord
import org.junit.jupiter.api.assertThrows

/**
 * Test for LogError annotation.
 * 
 * This test verifies that the LogError annotation correctly logs errors
 * based on the annotation parameters.
 */
@SpringBootTest(classes = [LogErrorTest.TestConfig::class])
class LogErrorTest {

    /**
     * Custom log handler to capture log records for verification
     */
    class TestLogHandler : Handler() {
        val logRecords = mutableListOf<LogRecord>()

        override fun publish(record: LogRecord) {
            logRecords.add(record)
        }

        override fun flush() {
            // No-op
        }

        override fun close() {
            logRecords.clear()
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    class TestConfig {
        @Bean
        fun testService(): TestService {
            return TestService()
        }

        @Bean
        fun loggingErrorAspect(): LoggingErrorAspect {
            return LoggingErrorAspect()
        }
    }

    @Autowired
    lateinit var testService: TestService

    @Autowired
    lateinit var loggingErrorAspect: LoggingErrorAspect

    private val logHandler = TestLogHandler()

    /**
     * Set up the test by adding the custom log handler to the root logger
     */
    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        // Add handler to root logger to capture all logs
        val rootLogger = Logger.getLogger("")
        rootLogger.addHandler(logHandler)

        // Set the log level to ALL to capture all log messages
        rootLogger.level = Level.ALL

        // Print debug information
        println("[DEBUG_LOG] Root logger level: ${rootLogger.level}")
        println("[DEBUG_LOG] Root logger handlers: ${rootLogger.handlers.joinToString { it.javaClass.simpleName }}")
        println("[DEBUG_LOG] TestService class: ${TestService::class.java.name}")
    }

    /**
     * Clean up after the test by removing the custom log handler
     */
    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        // Remove handler and clear captured logs
        Logger.getLogger("").removeHandler(logHandler)
        logHandler.logRecords.clear()
    }

    @Test
    fun testAnnotationRetention() {
        // Given
        val method = TestService::class.java.getDeclaredMethod("defaultLogMethod")

        // When
        val annotation = method.getAnnotation(LogError::class.java)

        // Then
        assertThat(annotation).isNotNull
        assertThat(annotation.level).isEqualTo(JavaLoggingLevel.INFO)
        assertThat(annotation.logStacktrace).isTrue()
        assertThat(annotation.logPrefix).isEqualTo(LOG_PREFIX)
    }

    @Test
    fun testWarningLevelAnnotation() {
        // Given
        val method = TestService::class.java.getDeclaredMethod("warningLevelMethod")

        // When
        val annotation = method.getAnnotation(LogError::class.java)

        // Then
        assertThat(annotation).isNotNull
        assertThat(annotation.level).isEqualTo(JavaLoggingLevel.WARNING)
    }

    @Test
    fun testNoStacktraceAnnotation() {
        // Given
        val method = TestService::class.java.getDeclaredMethod("noStacktraceMethod")

        // When
        val annotation = method.getAnnotation(LogError::class.java)

        // Then
        assertThat(annotation).isNotNull
        assertThat(annotation.logStacktrace).isFalse()
    }

    @Test
    fun testCustomPrefixAnnotation() {
        // Given
        val method = TestService::class.java.getDeclaredMethod("customPrefixMethod")

        // When
        val annotation = method.getAnnotation(LogError::class.java)

        // Then
        assertThat(annotation).isNotNull
        assertThat(annotation.logPrefix).isEqualTo("CUSTOM_PREFIX: ")
    }

    @Test
    fun testDefaultLogMethod() {
        // Clear any previous log records
        logHandler.logRecords.clear()

        // Print debug information before calling the method
        println("[DEBUG_LOG] Before calling defaultLogMethod")
        println("[DEBUG_LOG] LogHandler records count: ${logHandler.logRecords.size}")

        // Call the method that will throw an exception
        // This will trigger the aspect which will log the error
        val exception = assertThrows<RuntimeException> {
            testService.defaultLogMethod()
        }

        // Print debug information after calling the method
        println("[DEBUG_LOG] After calling defaultLogMethod")
        println("[DEBUG_LOG] LogHandler records count: ${logHandler.logRecords.size}")
        if (logHandler.logRecords.isNotEmpty()) {
            println("[DEBUG_LOG] First log record: ${logHandler.logRecords.first().message}")
            println("[DEBUG_LOG] First log record level: ${logHandler.logRecords.first().level}")
        }

        // Print all log records for debugging
        logHandler.logRecords.forEachIndexed { index, record ->
            println("[DEBUG_LOG] Log record $index: ${record.message}")
            println("[DEBUG_LOG] Log record $index level: ${record.level}")
        }

        // Verify that log records were created
        assertThat(logHandler.logRecords).isNotEmpty()

        // Find the aspect log record (contains the class and method name)
        val aspectLogRecord = logHandler.logRecords.find { 
            it.message.contains("TestService:defaultLogMethod") 
        }

        // If we found an aspect log record, verify it
        if (aspectLogRecord != null) {
            // Verify the log level is INFO (default)
            assertThat(aspectLogRecord.level).isEqualTo(Level.INFO)

            // Verify the log message contains the expected information
            assertThat(aspectLogRecord.message).contains("TestService:defaultLogMethod")
            assertThat(aspectLogRecord.message).contains("Exception:")

            // Verify the thrown is the same as the one we caught
            assertThat(aspectLogRecord.thrown).isEqualTo(exception)
        } else {
            // If no aspect log record was found, fail the test
            assertThat(logHandler.logRecords.map { it.message }).anySatisfy {
                assertThat(it).contains("TestService:defaultLogMethod")
            }
        }
    }

    @Test
    fun testWarningLevelMethod() {
        // Clear any previous log records
        logHandler.logRecords.clear()

        // Call the method that will throw an exception
        // This will trigger the aspect which will log the error
        val exception = assertThrows<RuntimeException> {
            testService.warningLevelMethod()
        }

        // Print all log records for debugging
        logHandler.logRecords.forEachIndexed { index, record ->
            println("[DEBUG_LOG] Log record $index: ${record.message}")
            println("[DEBUG_LOG] Log record $index level: ${record.level}")
        }

        // Verify that log records were created
        assertThat(logHandler.logRecords).isNotEmpty()

        // Find the aspect log record
        val aspectLogRecord = logHandler.logRecords.find { 
            it.message.contains("TestService:warningLevelMethod") 
        }

        // If we found an aspect log record, verify it
        if (aspectLogRecord != null) {
            // Verify the log level is WARNING (as specified in the annotation)
            assertThat(aspectLogRecord.level).isEqualTo(Level.WARNING)

            // Verify the log message contains the expected information
            assertThat(aspectLogRecord.message).contains("TestService:warningLevelMethod")
            assertThat(aspectLogRecord.message).contains("Exception:")

            // Verify the thrown is the same as the one we caught
            assertThat(aspectLogRecord.thrown).isEqualTo(exception)
        } else {
            // If no aspect log record was found, fail the test
            assertThat(logHandler.logRecords.map { it.message }).anySatisfy {
                assertThat(it).contains("TestService:warningLevelMethod")
            }
        }
    }

    @Test
    fun testNoStacktraceMethod() {
        // Clear any previous log records
        logHandler.logRecords.clear()

        // Call the method that will throw an exception
        // This will trigger the aspect which will log the error
        val exception = assertThrows<RuntimeException> {
            testService.noStacktraceMethod()
        }

        // Print all log records for debugging
        logHandler.logRecords.forEachIndexed { index, record ->
            println("[DEBUG_LOG] Log record $index: ${record.message}")
            println("[DEBUG_LOG] Log record $index level: ${record.level}")
            println("[DEBUG_LOG] Log record $index thrown: ${record.thrown}")
        }

        // Verify that log records were created
        assertThat(logHandler.logRecords).isNotEmpty()

        // Find the aspect log record
        val aspectLogRecord = logHandler.logRecords.find { 
            it.message.contains("TestService:noStacktraceMethod") 
        }

        // If we found an aspect log record, verify it
        if (aspectLogRecord != null) {
            // Verify the log level is INFO (default)
            assertThat(aspectLogRecord.level).isEqualTo(Level.INFO)

            // Verify the log message contains the expected information
            assertThat(aspectLogRecord.message).contains("TestService:noStacktraceMethod")
            
            // Verify that no stack trace was logged (thrown should be null)
            assertThat(aspectLogRecord.thrown).isNull()
        } else {
            // If no aspect log record was found, fail the test
            assertThat(logHandler.logRecords.map { it.message }).anySatisfy {
                assertThat(it).contains("TestService:noStacktraceMethod")
            }
        }
    }

    @Test
    fun testCustomPrefixMethod() {
        // Clear any previous log records
        logHandler.logRecords.clear()

        // Call the method that will throw an exception
        // This will trigger the aspect which will log the error
        val exception = assertThrows<RuntimeException> {
            testService.customPrefixMethod()
        }

        // Print all log records for debugging
        logHandler.logRecords.forEachIndexed { index, record ->
            println("[DEBUG_LOG] Log record $index: ${record.message}")
            println("[DEBUG_LOG] Log record $index level: ${record.level}")
        }

        // Verify that log records were created
        assertThat(logHandler.logRecords).isNotEmpty()

        // Find the aspect log record
        val aspectLogRecord = logHandler.logRecords.find { 
            it.message.contains("TestService:customPrefixMethod") 
        }

        // If we found an aspect log record, verify it
        if (aspectLogRecord != null) {
            // Verify the log level is INFO (default)
            assertThat(aspectLogRecord.level).isEqualTo(Level.INFO)

            // Verify the log message contains the expected information
            assertThat(aspectLogRecord.message).contains("CUSTOM_PREFIX: TestService:customPrefixMethod")
            assertThat(aspectLogRecord.message).contains("Exception:")

            // Verify the thrown is the same as the one we caught
            assertThat(aspectLogRecord.thrown).isEqualTo(exception)
        } else {
            // If no aspect log record was found, fail the test
            assertThat(logHandler.logRecords.map { it.message }).anySatisfy {
                assertThat(it).contains("CUSTOM_PREFIX: TestService:customPrefixMethod")
            }
        }
    }

    @Test
    fun testClassLevelAnnotation() {
        // Clear any previous log records
        logHandler.logRecords.clear()

        // Call the method that will throw an exception
        // This will trigger the aspect which will log the error
        val exception = assertThrows<RuntimeException> {
            testService.methodInAnnotatedClass()
        }

        // Print all log records for debugging
        logHandler.logRecords.forEachIndexed { index, record ->
            println("[DEBUG_LOG] Log record $index: ${record.message}")
            println("[DEBUG_LOG] Log record $index level: ${record.level}")
        }

        // Verify that log records were created
        assertThat(logHandler.logRecords).isNotEmpty()

        // Find the aspect log record
        val aspectLogRecord = logHandler.logRecords.find { 
            it.message.contains("TestService:methodInAnnotatedClass") 
        }

        // If we found an aspect log record, verify it
        if (aspectLogRecord != null) {
            // Verify the log level is INFO (default from class annotation)
            assertThat(aspectLogRecord.level).isEqualTo(Level.INFO)

            // Verify the log message contains the expected information
            assertThat(aspectLogRecord.message).contains("TestService:methodInAnnotatedClass")
            assertThat(aspectLogRecord.message).contains("Exception:")

            // Verify the thrown is the same as the one we caught
            assertThat(aspectLogRecord.thrown).isEqualTo(exception)
        } else {
            // If no aspect log record was found, fail the test
            assertThat(logHandler.logRecords.map { it.message }).anySatisfy {
                assertThat(it).contains("TestService:methodInAnnotatedClass")
            }
        }
    }

    /**
     * Interface for the test service
     */
    interface ITestService {
        fun defaultLogMethod()
        fun warningLevelMethod()
        fun noStacktraceMethod()
        fun customPrefixMethod()
        fun methodInAnnotatedClass()
    }

    /**
     * Test service class with methods annotated with @LogError
     */
    @LogError // Class-level annotation for testing
    @org.springframework.stereotype.Component
    open class TestService : ITestService {
        @LogError
        override fun defaultLogMethod() {
            // This method should log errors with default settings (INFO level)
            throw RuntimeException("Test exception in defaultLogMethod")
        }

        @LogError(level = JavaLoggingLevel.WARNING)
        override fun warningLevelMethod() {
            // This method should log errors with WARNING level
            throw RuntimeException("Test exception in warningLevelMethod")
        }

        @LogError(logStacktrace = false)
        override fun noStacktraceMethod() {
            // This method should log errors without stack trace
            throw RuntimeException("Test exception in noStacktraceMethod")
        }

        @LogError(logPrefix = "CUSTOM_PREFIX: ")
        override fun customPrefixMethod() {
            // This method should log errors with a custom prefix
            throw RuntimeException("Test exception in customPrefixMethod")
        }

        // No annotation on this method, but class has annotation
        override fun methodInAnnotatedClass() {
            // This method should log errors using the class-level annotation
            throw RuntimeException("Test exception in methodInAnnotatedClass")
        }
    }
}