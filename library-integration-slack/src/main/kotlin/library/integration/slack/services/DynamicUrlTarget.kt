package library.integration.slack.services

import feign.Request
import feign.RequestTemplate
import feign.Target
import kotlin.reflect.KClass

/**
 * This [Target] implementation allows the Feign client's base URL to be
 * changed at runtime. For this purpose a String supplier function is used
 * instead of a String value.
 *
 * The main purpose of this is testability (e.g. using dynamic WireMock ports).
 * But it also allows for runtime configuration changes without having to
 * restart the whole service.
 *
 * Examples of how to use this can be seen in the configuration classes of
 * Feign client using modules.
 *
 * @see Target
 * @see SlackMessageClient
 * @see SlackMessageClientConfiguration
 */

class DynamicUrlTarget<T : Any>(
    private val name: String,
    private val type: KClass<T>,
    private val urlSupplier: () -> String
) : Target<T> {

    override fun type() = type.java
    override fun name() = name
    override fun url() = urlSupplier()

    override fun apply(input: RequestTemplate): Request {
        input.insert(0, url())
        return input.request()
    }
}