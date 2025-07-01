package io.github.breninsul.logging.aspect

import org.aspectj.lang.ProceedingJoinPoint
import java.util.concurrent.Callable
import java.util.concurrent.Semaphore
import java.util.logging.Logger

/**
 * A cache for storing and retrieving logger instances for specific classes. This class ensures
 * thread-safe access and management of logger instances, and provides a mechanism to retrieve
 * or create a logger for a given class.
 *
 * The implementation uses a `Semaphore` to synchronize access to the logger cache, ensuring that
 * logger instances are created and accessed in a thread-safe manner.
 */
open class LoggerClassesCache {

    protected open val loggerMap: MutableMap<Class<*>, Logger> = HashMap()

    protected open val loggerSemaphore = Semaphore(1)

    /**
     * Retrieves or creates a logger instance associated with the target class of the provided join point.
     * This ensures thread-safe access and creation of logger instances by utilizing a semaphore for synchronization.
     *
     * @param joinPoint The `ProceedingJoinPoint` representing the method or code execution point whose target class's logger is required.
     * @return A `Logger` instance associated with the target class of the specified join point.
     */
    open fun getLogger(joinPoint: ProceedingJoinPoint): Logger {
        return loggerSemaphore.sync {
            val clazz: Class<*> = joinPoint.target.javaClass
            val logger = loggerMap[clazz]
            if (logger == null) {
                val loggerVal = Logger.getLogger(clazz.name)
                loggerMap[clazz] = loggerVal
                return@sync loggerVal
            } else {
                return@sync logger
            }
        }
    }

    object INSTANCE : LoggerClassesCache()

    fun <T> Semaphore.sync(runnable: Callable<T>): T {
        try {
            this.acquire()
            return runnable.call()
        } finally {
            this.release()
        }
    }

}
