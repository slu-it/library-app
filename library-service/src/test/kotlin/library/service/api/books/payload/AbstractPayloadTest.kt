package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import javax.validation.Validation
import javax.validation.Validator
import kotlin.reflect.KClass

@UnitTest
internal abstract class AbstractPayloadTest<T : Any> {

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    protected abstract val payloadType: KClass<T>

    protected abstract val jsonExample: String
    protected abstract val deserializedExample: T

    @Test fun `can be de-serialized from JSON`() {
        val deserialized = objectMapper.readValue(jsonExample, payloadType.java)
        assertThat(deserialized).isEqualTo(deserializedExample)
    }

    protected fun validate(it: T) = validator.validate(it).map { it.message }

}