package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConfigurableTmiSession
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

@Configuration
class BotConfiguration {

    @Bean
    fun messageCountingBot(): Bot {
        return object : Bot {
            lateinit var startTime: LocalDateTime
            val totalMessages: AtomicInteger = AtomicInteger(0)
            val subscriberMessages: AtomicInteger = AtomicInteger(0)

            override val name: String = "messageCountingBot"

            override fun onConnect(session: ConfigurableTmiSession) {
                session.tagCapabilities()
                // manually join a channel; alternatively the channels member can be filled with all the channels which
                // should be joined when connecting
                session.join("sykq")
                session.textMessage("sykq", "connected")
                startTime = LocalDateTime.now()
            }

            override fun onMessage(session: TmiSession, message: TmiMessage) {
                totalMessages.getAndIncrement()
                message.tags["subscriber"]?.also {
                    if (it.contains("1")) {
                        subscriberMessages.getAndIncrement()
                    }
                }
            }

            override fun getProperties(): Map<String, Any> {
                val messagesPerMinute =
                    totalMessages.get() / Duration.between(startTime, LocalDateTime.now()).toMinutesExact()
                return mapOf(
                    "totalMessages" to totalMessages.get(),
                    "subscriberMessages" to subscriberMessages.get(),
                    "messagesPerMinute" to messagesPerMinute
                )
            }
        }
    }

    private fun Duration.toMinutesExact(): Double = toSeconds().toDouble() / 60

}