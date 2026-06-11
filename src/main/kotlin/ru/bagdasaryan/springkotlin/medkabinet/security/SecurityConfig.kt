package ru.bagdasaryan.springkotlin.medkabinet.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import ru.bagdasaryan.springkotlin.medkabinet.domain.UserRole

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun authenticationSuccessHandler(): AuthenticationSuccessHandler =
        AuthenticationSuccessHandler { _, response, authentication ->
            val principal = authentication.principal as? AuthUserPrincipal
            val targetUrl = when (principal?.role) {
                UserRole.PATIENT -> "/patients/${principal.patientId?.value}"
                UserRole.DOCTOR,
                UserRole.ADMIN,
                null -> "/"
            }
            response.sendRedirect(targetUrl)
        }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationSuccessHandler: AuthenticationSuccessHandler
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests {
                it.requestMatchers("/login", "/access-denied", "/error", "/img/**", "/favicon.ico").permitAll()
                it.requestMatchers(HttpMethod.GET, "/").hasAnyRole("ADMIN", "DOCTOR")
                it.requestMatchers(HttpMethod.GET, "/doctors/*/edit").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.POST, "/doctors/*/edit").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.GET, "/patients").hasAnyRole("ADMIN", "DOCTOR")
                it.requestMatchers(HttpMethod.GET, "/patients/new").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.POST, "/patients").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.GET, "/appointments").hasAnyRole("ADMIN", "DOCTOR")
                it.requestMatchers(HttpMethod.POST, "/appointments").hasAnyRole("ADMIN", "DOCTOR")
                it.requestMatchers(HttpMethod.GET, "/patients/*/history/new").hasRole("DOCTOR")
                it.requestMatchers(HttpMethod.POST, "/patients/*/history").hasRole("DOCTOR")
                it.requestMatchers(HttpMethod.GET, "/patients/*/edit").hasAnyRole("ADMIN", "PATIENT")
                it.requestMatchers(HttpMethod.POST, "/patients/*/edit").hasAnyRole("ADMIN", "PATIENT")
                it.requestMatchers(HttpMethod.GET, "/patients/*/history/*/edit").hasAnyRole("ADMIN", "DOCTOR")
                it.requestMatchers(HttpMethod.POST, "/patients/*/history/*/edit").hasAnyRole("ADMIN", "DOCTOR")
                it.requestMatchers(HttpMethod.GET, "/patients/*").authenticated()
                it.anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login")
                it.loginProcessingUrl("/login")
                it.successHandler(authenticationSuccessHandler)
                it.failureUrl("/login?error")
                it.permitAll()
            }
            .logout {
                it.logoutUrl("/logout")
                it.logoutSuccessUrl("/login?logout")
                it.permitAll()
            }
            .exceptionHandling {
                it.accessDeniedPage("/access-denied")
            }
        return http.build()
    }
}
