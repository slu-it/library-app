package library.enrichment.common.feign

import feign.Request
import feign.RequestTemplate
import feign.Target
import kotlin.reflect.KClass

class DynamicUrlTarget<T : Any>(
        private val type: KClass<T>,
        private val urlSupplier: () -> String
) : Target<T> {

    override fun type() = type.java
    override fun name() = url()
    override fun url() = urlSupplier()

    override fun apply(input: RequestTemplate): Request {
        input.insert(0, url())
        return input.request()
    }

}