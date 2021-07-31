package io.github.sykq.tcc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http.authorizeExchange { it.anyExchange().authenticated() }
            .csrf{ it.disable() }
            .httpBasic().and()
            .formLogin()
        return http.build()
    }

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService? {
        val user = User.builder()
            .passwordEncoder {
                PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(it)
            }
            .username("user")
            .password("password")
            .roles("USER")
            .build()
        return MapReactiveUserDetailsService(user)
    }

}