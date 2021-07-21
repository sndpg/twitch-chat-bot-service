package io.github.sykq.tcc.routes

import io.github.sykq.tcc.TmiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RoutesConfiguration {

    @Bean
    fun routerFunction(tmiClient: TmiClient) = router {
        GET("/bots") {
            tmiClient
            ServerResponse.ok().bodyValue(mapOf("key" to "value"))
        }
    }
}