package utils

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.snippet.Snippet


fun document(identifier: String, vararg snippets: Snippet): RestDocumentationResultHandler {
    return MockMvcRestDocumentation.document(identifier, Preprocessors.preprocessRequest(Preprocessors.prettyPrint()), Preprocessors.preprocessResponse(Preprocessors.prettyPrint()), *snippets)
}