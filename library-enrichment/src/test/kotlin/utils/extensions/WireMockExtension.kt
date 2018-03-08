package utils.extensions

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import mu.KotlinLogging.logger
import org.junit.jupiter.api.extension.*

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(WireMockExtension::class)
annotation class EnableWireMockExtension

class WireMockExtension : BeforeAllCallback, BeforeEachCallback, AfterAllCallback, ParameterResolver {

    private companion object {
        val EXTENSION_NAMESPACE = ExtensionContext.Namespace.create("WireMockExtension")
        val SERVER_PROPERTY = "server"
    }

    private val log = logger {}

    override fun beforeAll(context: ExtensionContext) = onlyForInitialContext(context) { store ->
        val options = options()
                .dynamicPort()
                .dynamicHttpsPort()

        log.debug { "Starting WireMock server ..." }
        val server = WireMockServer(options).apply { start() }
        store.setServer(server)
        log.debug { "... WireMock server started. [port=${server.port()}, httpsPort=${server.httpsPort()}]" }
    }

    override fun beforeEach(context: ExtensionContext) {
        log.debug { "Resetting WireMock server ..." }
        context.getExtensionStore().getServer()?.apply { resetAll() }
        log.debug { "... WireMock server reset." }
    }

    override fun afterAll(context: ExtensionContext) = onlyForInitialContext(context) { store ->
        log.debug { "Stopping WireMock server ..." }
        store.getServer()?.apply { stop() }
        log.debug { "... WireMock server stopped." }
    }

    private fun onlyForInitialContext(context: ExtensionContext, body: (ExtensionContext.Store) -> Unit) {
        val store = context.getExtensionStore()
        val initialContext = store.get("initialContext", ExtensionContext::class.java)
        if (initialContext == null || initialContext == context) {
            body(store)
            store.put("initialContext", context)
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == WireMockServer::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return extensionContext.getExtensionStore().getServer() ?: error("WireMock server not initialized!")
    }

    private fun ExtensionContext.Store.setServer(server: WireMockServer) = put(SERVER_PROPERTY, server)
    private fun ExtensionContext.Store.getServer(): WireMockServer? = get(SERVER_PROPERTY, WireMockServer::class.java)
    private fun ExtensionContext.getExtensionStore() = getStore(EXTENSION_NAMESPACE)

}