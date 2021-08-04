package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConfigurableTmiSession
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import io.github.sykq.tcc.action.OnCommandAction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
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
                session.textMessage("connected")
                startTime = LocalDateTime.now()
            }

            override fun onMessage(session: TmiSession, message: TmiMessage) {
                session.onCommand("!messageCounter", message) {
                    textMessage(
                        "counting for the last ${startTime.asUpTime().toMinutes()} minutes, ${totalMessages.get()} " +
                                "messages have been recorded of which ${subscriberMessages.get()} were written by " +
                                "subscribers of ${it.message.channel}. That's " +
                                "${getMessagesPerMinute(totalMessages, startTime)} messages per minute."
                    )
                }
                totalMessages.getAndIncrement()
                message.tags["subscriber"]?.also {
                    if (it.contains("1")) {
                        subscriberMessages.getAndIncrement()
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

    @Bean
    fun reactiveMessageCountingBot(): ReactiveBot {
        return object : ReactiveBot {
            lateinit var startTime: LocalDateTime
            val totalMessages: AtomicInteger = AtomicInteger(0)
            val subscriberMessages: AtomicInteger = AtomicInteger(0)
            val showMessageCounterOnCommand = OnCommandAction("!messageCounter") {
                textMessage(
                    "counting for the last ${startTime.asUpTime().toMinutes()} minutes, ${totalMessages.get()} " +
                            "messages have been recorded of which ${subscriberMessages.get()} were written by " +
                            "subscribers of ${it.message.channel}. That's " +
                            "${getMessagesPerMinute(totalMessages, startTime)} messages per minute."
                )
            }

            override val name: String = "reactiveMessageCountingBot"

            override fun onConnect(session: ConfigurableTmiSession) {
                session.tagCapabilities()
                // manually join a channel; alternatively the channels member can be filled with all the channels which
                // should be joined when connecting
                session.join("sykq")
                session.textMessage("connected")
                startTime = LocalDateTime.now()
            }

            override fun onMessage(session: TmiSession, messages: Flux<TmiMessage>): Flux<TmiMessage> {
                return messages.log()
                    .flatMap {
                        // we have to create a new publisher when sending a message, otherwise the message won't be sent
                        // if the incoming message is not a subscriber message (since it will be filtered)
                        Flux.merge(
                            it.toMono()
                                .doOnNext { message -> showMessageCounterOnCommand(session, message) },
                            it.toMono()
                                .doOnNext { totalMessages.getAndIncrement() }
                                .filter { message -> message.tags["subscriber"]?.contains("1") == true }
                                .doOnNext { subscriberMessages.getAndIncrement() })
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

    private fun Duration.toMinutesExact(): Double = toSeconds().toDouble() / 60

}