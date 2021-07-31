package io.github.sykq.tcc.client

import io.github.sykq.tcc.tmiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks

@Configuration
class TmiClientConfiguration {

    @Bean
    fun tmiClientBean(tmiClientMessageSink: Sinks.Many<String>) = tmiClient {
        channels += "sykq"
        messageSink = tmiClientMessageSink

        onMessage { message ->
            println(message.toString())
        }
    }

    @Bean
    fun tmiClientMessageSink() = Sinks.many().unicast().onBackpressureBuffer<String>()

}