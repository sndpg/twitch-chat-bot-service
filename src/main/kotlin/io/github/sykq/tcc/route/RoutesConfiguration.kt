package io.github.sykq.tcc.route

import io.github.sykq.tcc.bot.BotRegistry
import io.github.sykq.tcc.client.MessageSpec
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Sinks

@Configuration
class RoutesConfiguration {

    @Bean
    fun routerFunction(botRegistry: BotRegistry, messageSink: Sinks.Many<String>) = router {
        GET("/bots") {
            ServerResponse.ok().bodyValue(botRegistry.botNames)
        }
        GET("/bots/{botName}") {
            ServerResponse.ok().bodyValue(botRegistry.bots[it.pathVariable("botName")]?.getProperties()!!)
        }
        POST("/messages") {
            it.bodyToMono<MessageSpec>()
                .map { spec -> messageSink.tryEmitNext(spec.message) }
                .flatMap { ServerResponse.noContent().build() }
        }
    }
}