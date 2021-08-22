package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConfigurableTmiSession
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.action.OnCommandAction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
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
                // these could also be activated by setting the tmi.default-capabilities list within
                // application.properties
                session.tagCapabilities()
                // manually join a channel; alternatively the channels member can be filled with all the channels which
                // should be joined when connecting
                session.join("sykq")
                session.textMessage("connected with default bot")
                startTime = LocalDateTime.now()
            }

            override fun onMessage(session: TmiSession, message: TmiMessage) {
                session.onCommand("!messageCounter", message) {
                    textMessage(
                        "counting for the last ${startTime.asUpTime().toMinutes()} minutes, ${totalMessages.get()} " +
                                "messages have been recorded of which ${subscriberMessages.get()} were written by " +
                                "subscribers of ${it.message.channel}. That's " +
                                "${getFormattedMessagesPerMinute(totalMessages, startTime)} messages per minute."
                    )
                }
                totalMessages.getAndIncrement()
                message.takeIf { it.isUserSubscribed() }
                    ?.run { subscriberMessages.getAndIncrement() }
            }

            override fun getProperties(): Map<String, Any> {
                return mapOf(
                    "totalMessages" to totalMessages.get(),
                    "subscriberMessages" to subscriberMessages.get(),
                    "messagesPerMinute" to getMessagesPerMinute(totalMessages, startTime)
                )
            }
        }
    }

    @Bean
    fun publishingMessageCountingBot(): PublishingBot {
        return object : PublishingBot {
            lateinit var startTime: LocalDateTime
            val totalMessages: AtomicInteger = AtomicInteger(0)
            val subscriberMessages: AtomicInteger = AtomicInteger(0)
            val showMessageCounterOnCommand = OnCommandAction("!messageCounter") {
                textMessage(
                    "counting for the last ${startTime.asUpTime().toMinutes()} minutes, ${totalMessages.get()} " +
                            "messages have been recorded of which ${subscriberMessages.get()} were written by " +
                            "subscribers of ${it.message.channel}. That's " +
                            "${getFormattedMessagesPerMinute(totalMessages, startTime)} messages per minute."
                )
            }

            override val name: String = "publishingMessageCountingBot"

            override fun onConnect(session: ConfigurableTmiSession) {
                // these could also be activated by setting the tmi.default-capabilities list within
                // application.properties
                session.tagCapabilities()
                // manually join a channel; alternatively the channels member can be filled with all the channels which
                // should be joined when connecting
                session.join("sykq")
                session.textMessage("connected with publishing bot")
                startTime = LocalDateTime.now()
            }

            override fun onMessage(session: TmiSession, messages: Flux<TmiMessage>): Flux<TmiMessage> {
                return messages.log()
                    .map {
                        showMessageCounterOnCommand(session, it)
                        totalMessages.getAndIncrement()
                        it.apply {
                            takeIf { msg -> msg.isUserSubscribed() }
                                ?.run { subscriberMessages.getAndIncrement() }
                        }
                    }
            }

            override fun getProperties(): Map<String, Any> {
                return mapOf(
                    "totalMessages" to totalMessages.get(),
                    "subscriberMessages" to subscriberMessages.get(),
                    "messagesPerMinute" to getMessagesPerMinute(totalMessages, startTime)
                )
            }
        }
    }

    private fun LocalDateTime.asUpTime(): Duration =
        Duration.between(this, LocalDateTime.now())

    private fun getMessagesPerMinute(totalMessages: AtomicInteger, startTime: LocalDateTime): Double =
        totalMessages.get() / startTime.asUpTime().toMinutesExact()

    private fun getFormattedMessagesPerMinute(totalMessages: AtomicInteger, startTime: LocalDateTime): String =
        String.format("%.4f", getMessagesPerMinute(totalMessages, startTime))

    private fun Duration.toMinutesExact(): Double = toSeconds().toDouble() / 60

}