package utils.testapi

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class TestController(private val testService: TestService) {

    @PostMapping("/test")
    fun post(@RequestParam(required = false) foo: String?) = testService.doSomething()

}

interface TestService {
    @Throws(Throwable::class)
    fun doSomething()
}