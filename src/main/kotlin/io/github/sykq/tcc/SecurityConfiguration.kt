package io.github.sykq.tcc

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests { it.anyRequest().authenticated() }
            .csrf { it.disable() }
            .sessionManagement { it.disable() }
            .httpBasic {}
            .formLogin {}
    }

    override fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .passwordEncoder {
                PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(it)
            }
            .username("user")
            .password("")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

}