package utils.grpc

import library.enrichment.gateways.grpc.BookResponse
import library.enrichment.gateways.grpc.UpdateBookGrpcKt
import library.enrichment.gateways.grpc.UpdateNumberOfPagesRequest
import org.springframework.boot.test.context.TestComponent

/**
 * Mock for UpdateBookCoroutineImplBase Service.
 */
@TestComponent
class UpdateBookService() : UpdateBookGrpcKt.UpdateBookCoroutineImplBase() {

    override suspend fun updateNumberOfPages(request: UpdateNumberOfPagesRequest): BookResponse =
        BookResponse.newBuilder().build()
}
