package library.enrichment.gateways

import feign.Request
import feign.RequestTemplate
import feign.Target
import kotlin.reflect.KClass

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