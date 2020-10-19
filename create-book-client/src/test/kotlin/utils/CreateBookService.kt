package utils

import library.createbookclient.CreateBookGrpcKt
import library.createbookclient.CreateBookRequest
import library.createbookclient.CreateBookResponse
import org.springframework.boot.test.context.TestComponent

/**
 * Mock for CreateBookCoroutineImplBase Service.
 */
@TestComponent
class CreateBookService() : CreateBookGrpcKt.CreateBookCoroutineImplBase() {

    /**
     * @return CreateBookResponse that contains only the information used by Create Book Client, namely isbn and title.
     */
    override suspend fun createBook(request: CreateBookRequest) = CreateBookResponse
        .newBuilder()
        .apply {
            this.isbn = request.isbn
            this.title = request.title
        }
        .build()
}
