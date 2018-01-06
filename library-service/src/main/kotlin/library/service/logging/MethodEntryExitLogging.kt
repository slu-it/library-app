package library.service.logging

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

/**
 * This annotation can be used to activate automatic entry/exit logging for
 * the annotated class' _public_ methods. As long as the class' instance is
 * a Spring bean and Spring AOP is enabled for the current context.
 *
 * The log entries are created on the `DEBUG` level. There will be one just
 * before a method is executed and one just after the execution returned.
 *
 * Because the logging is done using runtime aspects (proxies) only executions
 * from _outside_ the bean will actually log anything. Invoking a public method
 * from within another method of the annotated class will not trigger the logging!
 * In addition only non-final methods can be proxied. Since we are using the
 * Spring extension for Kotlin during compile time, all methods of bean classes
 * are non-final by default.
 *
 * **Usage Example:**
 *
 * ```
 * @Service
 * @LogMethodEntryAndExit
 * class FooService {
 *
 *     // execution will be logged
 *     fun foo() { .. }
 *
 *     // execution will NOT be logged
 *     protected fun bar() { .. }
 *
 * }
 * ```
 *
 * @see LogMethodEntryAndExitAspect
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMethodEntryAndExit

/**
 * Aspect responsible for implementing the logging behaviour described for the
 * [LogMethodEntryAndExit] annotation.
 */
@Aspect
@Component
class LogMethodEntryAndExitAspect {

    private val log = LogMethodEntryAndExitAspect::class.logger

    @Around("(@within(LogMethodEntryAndExit) && execution(public * *(..)))")
    fun aroundMethod(pjp: ProceedingJoinPoint): Any? {
        val signature = pjp.signature
        log.debug("executing method: {}", signature)
        val returnValue = pjp.proceed()
        log.debug("successfully executed method: {}", signature)
        return returnValue
    }

}