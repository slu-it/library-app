package library.enrichment.security

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories

@Configuration
@Profile("!unsecured")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(UserSettings::class)
class SecurityConfiguration(
        private val userSettings: UserSettings
) : WebSecurityConfigurerAdapter() {

    private val infoEndpoint = InfoEndpoint::class.java
    private val healthEndpoint = HealthEndpoint::class.java

    private val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    override fun configure(http: HttpSecurity): Unit = with(http) {
        csrf().disable()
        httpBasic()
        sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        authorizeRequests {
            requestMatchers(EndpointRequest.to(infoEndpoint, healthEndpoint)).permitAll()
            requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Roles.ACTUATOR)
            anyRequest().fullyAuthenticated()
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder): Unit = with(auth) {
        inMemoryAuthentication {
            withUser(userSettings.admin.toUser(Roles.ACTUATOR))
        }
    }

    @Bean override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    private fun UserSettings.UserCredentials.toUser(vararg roles: String) = User
            .withUsername(username)
            .password(encoder.encode(password))
            .roles(*roles)
            .build()

    private fun HttpSecurity.authorizeRequests(
            body: ExpressionUrlAuthorizationConfigurer<*>.ExpressionInterceptUrlRegistry.() -> Unit
    ) = body(this.authorizeRequests())

    private fun AuthenticationManagerBuilder.inMemoryAuthentication(
            body: InMemoryUserDetailsManagerConfigurer<*>.() -> Unit
    ) = body(this.inMemoryAuthentication())

}