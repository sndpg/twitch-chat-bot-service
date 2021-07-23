package io.github.sykq.tcc.route

import io.github.sykq.tcc.bot.BotRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RoutesConfiguration {

    @Bean
    fun routerFunction(botRegistry: BotRegistry) = router {
        GET("/bots") {
            ServerResponse.ok().bodyValue(botRegistry.botNames)
        }
        GET("/bots/{botName}") {
            ServerResponse.ok().bodyValue(botRegistry.bots[it.pathVariable("botName")]?.getProperties()!!)
        }
    }
}