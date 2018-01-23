package library.service.security

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@Profile("!unsecured")
@RequestMapping("/userinfo")
class UserInfoController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun get(@AuthenticationPrincipal user: User) = UserInfo(
            username = user.username,
            authorities = user.authorities.map { it.authority }
    )

    data class UserInfo(
            val username: String,
            val authorities: List<String>
    )

}