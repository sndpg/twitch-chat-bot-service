package io.github.sykq.tcc.client

import io.github.sykq.tcc.TmiClient
import io.github.sykq.tcc.tmiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks

@Configuration
class TmiClientConfiguration {

    @Bean
    fun tmiClientMessageSink(): Sinks.Many<String> =
        Sinks.many().unicast().onBackpressureBuffer()

    @Bean
    fun tmiClientBean(tmiClientMessageSink: Sinks.Many<String>): TmiClient =
        tmiClient {
            channels += "sykq"
            messageSink = tmiClientMessageSink

            onMessage { message ->
                println(message.toString())
            }
        }

}
