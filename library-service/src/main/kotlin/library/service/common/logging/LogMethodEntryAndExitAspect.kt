package library.service.common.logging

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Aspect responsible for logging method's entries and exits if their declaring
 * class is annotated with [LogMethodEntryAndExit]. And the methods are public
 * and `open` for extension.
 */
@Aspect
@Component
class LogMethodEntryAndExitAspect {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Around("(@within(LogMethodEntryAndExit) && execution(public * *(..)))")
    fun aroundMethod(pjp: ProceedingJoinPoint): Any? {
        val signature = pjp.signature
        log.debug("executing method: {}", signature)
        val returnValue = pjp.proceed()
        log.debug("successfully executed method: {}", signature)
        return returnValue
    }

}