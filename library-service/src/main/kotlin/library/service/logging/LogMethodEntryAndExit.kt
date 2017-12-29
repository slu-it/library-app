package library.service.logging

/**
 * Annotating a class with this annotation will trigger the logging of public
 * method's entries and exits. Only non final classes and methods (`open`) are
 * considered.
 *
 * Note: In Kotlin all classes and methods are final by default! With the
 * Spring extension this is changed for all `@Component` bean classes.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMethodEntryAndExit
