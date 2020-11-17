package library.createbookclient

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import library.createbookclient.grpc.CreateBookConsumer
import mu.KotlinLogging.logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreateBookClientApplication(
    private val createBookConsumer: CreateBookConsumer
) : CommandLineRunner {
    private val log = logger {}

    //gradlew bootRun --args='HarryPotter,9783551557414'
    override fun run(vararg args: String?) {
        val errorMessage = "Please provider arguments in the order of book title and isbn number, comma separated.\n" +
                "Valid request --args='HarryPotter,9783551557414'"

        log.info { "Starting CLR..." }

        val inputValues = emptyList<String>().toMutableList()

        check(args.isNotEmpty()) { errorMessage }
        check(args.first()!!.contains(",")) { errorMessage }
        inputValues.addAll(args.first()!!.split(","))
        check(inputValues.size == 2) { errorMessage }

        val bookRequest = CreateBookPayload(
            isbn = inputValues[1],
            title = inputValues.first()
        )
        runBlocking {
            launch {
                createBookConsumer.sendBook(
                    isbn = bookRequest.isbn,
                    title = bookRequest.title
                )
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CreateBookClientApplication>(*args)
}


