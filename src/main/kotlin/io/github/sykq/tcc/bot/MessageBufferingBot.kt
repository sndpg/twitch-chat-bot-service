package io.github.sykq.tcc.bot

import io.github.sykq.tcc.ConfigurableTmiSession
import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageBufferingBot: Bot {
    override val name = "messageBufferingBot"
    override val channels = listOf("sykq")

    private val messages = ArrayDeque<TmiMessage>(100)

    override fun onConnect(session: ConfigurableTmiSession) {
        session.tagCapabilities()
    }

    override fun onMessage(session: TmiSession, message: TmiMessage) {
        messages.push(message)
        if (messages.size > 100) {
            messages.removeLast()
        }
    }

    override fun getProperties(): Map<String, Any> = mapOf("messages" to messages)

}
