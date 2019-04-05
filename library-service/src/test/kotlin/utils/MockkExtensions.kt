package utils

import io.mockk.clearAllMocks
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

@Target(AnnotationTarget.CLASS)
@ExtendWith(ResetMocksAfterEachTestExtension::class)
annotation class ResetMocksAfterEachTest

class ResetMocksAfterEachTestExtension : AfterEachCallback {

    override fun afterEach(context: ExtensionContext?) {
        clearAllMocks()
    }

}