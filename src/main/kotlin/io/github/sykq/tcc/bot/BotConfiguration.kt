package io.github.sykq.tcc.bot

import io.github.sykq.tcc.TmiMessage
import io.github.sykq.tcc.TmiSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.atomic.AtomicInteger

@Configuration
class BotConfiguration {

    @Bean
    fun messageCountingBot(): Bot {
        return object : Bot {
            val counter: AtomicInteger = AtomicInteger(0)
            override val name: String = "messageCountingBot"

            override fun onConnect(session: TmiSession) {
                session.join("sykq")
                session.textMessage("sykq","connected")
            }

            override fun onMessage(session: TmiSession, message: TmiMessage) {
                counter.getAndIncrement()
            }

            override fun getProperties(): Map<String, Any> {
                return mapOf("messageCount" to counter.get())
            }
        }
    }
}