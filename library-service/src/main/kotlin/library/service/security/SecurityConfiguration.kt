package library.service.security

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Configuration
    @ConditionalOnProperty("application.secured", havingValue = "false", matchIfMissing = false)
    class UnsecuredConfiguration : WebSecurityConfigurerAdapter() {

        override fun configure(http: HttpSecurity): Unit = with(http) {
            csrf().disable()
            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            authorizeRequests().anyRequest().permitAll()
        }

    }

    @Configuration
    @ConditionalOnProperty("application.secured", havingValue = "true", matchIfMissing = true)
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @EnableConfigurationProperties(UserSettings::class, CorsSettings::class)
    class SecuredConfiguration(
            private val userSettings: UserSettings,
            private val corsSettings: CorsSettings
    ) : WebSecurityConfigurerAdapter() {

        private val infoEndpoint = InfoEndpoint::class.java
        private val healthEndpoint = HealthEndpoint::class.java

        private val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        override fun configure(http: HttpSecurity): Unit = with(http) {
            csrf().disable()
            cors()
            httpBasic()
            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            authorizeRequests {
                antMatchers(HttpMethod.GET, "/", "/help", "/docs", "/docs/**").permitAll()
                requestMatchers(EndpointRequest.to(infoEndpoint, healthEndpoint)).permitAll()
                requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Roles.ACTUATOR)
                anyRequest().fullyAuthenticated()
            }
        }

        override fun configure(auth: AuthenticationManagerBuilder): Unit = with(auth) {
            inMemoryAuthentication {
                withUser(userSettings.admin.toUser(Roles.USER, Roles.CURATOR, Roles.ACTUATOR))
                withUser(userSettings.curator.toUser(Roles.USER, Roles.CURATOR))
                withUser(userSettings.user.toUser(Roles.USER))
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

        @Bean
        fun corsConfigurationSource(): CorsConfigurationSource {
            val configuration = CorsConfiguration()
            configuration.allowedOrigins = corsSettings.origins
            configuration.allowedMethods = corsSettings.methods
            configuration.allowedHeaders = mutableListOf("*")
            configuration.allowCredentials = true
            val source = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/**", configuration)
            return source
        }

    }

}