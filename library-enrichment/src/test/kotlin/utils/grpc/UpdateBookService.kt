package utils.grpc

import library.enrichment.gateways.library.grpc.BookResponse
import library.enrichment.gateways.library.grpc.UpdateAuthorsRequest
import library.enrichment.gateways.library.grpc.UpdateBookGrpcKt
import library.enrichment.gateways.library.grpc.UpdateNumberOfPagesRequest
import org.springframework.boot.test.context.TestComponent

/**
 * Mock for UpdateBookCoroutineImplBase Service.
 */
@TestComponent
class UpdateBookService() : UpdateBookGrpcKt.UpdateBookCoroutineImplBase() {

    override suspend fun updateNumberOfPages(request: UpdateNumberOfPagesRequest): BookResponse =
        BookResponse.newBuilder().build()

    override suspend fun updateAuthors(request: UpdateAuthorsRequest): BookResponse =
        BookResponse.newBuilder().build()
}
