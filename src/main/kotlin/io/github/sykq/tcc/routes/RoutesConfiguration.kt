package io.github.sykq.tcc.routes

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RoutesConfiguration {

    @Bean
    fun routerFunction() = router {
        GET("/bots") {
            ServerResponse.ok().bodyValue(mapOf("key" to "value"))
        }
    }
}