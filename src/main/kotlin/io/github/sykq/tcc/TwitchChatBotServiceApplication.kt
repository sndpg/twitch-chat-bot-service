package io.github.sykq.tcc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TwitchChatBotServiceApplication

fun main(args: Array<String>) {
    runApplication<TwitchChatBotServiceApplication>(*args)
}
