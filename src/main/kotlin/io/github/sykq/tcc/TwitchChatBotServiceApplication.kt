package io.github.sykq.tcc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.blockhound.BlockHound

@SpringBootApplication
class TwitchChatBotServiceApplication

fun main(args: Array<String>) {
    BlockHound.builder()
        .allowBlockingCallsInside(
            "kotlin.reflect.jvm.internal.impl.metadata.builtins.BuiltInsBinaryVersion\$Companion",
            "readFrom"
        )
        .install()
    runApplication<TwitchChatBotServiceApplication>(*args)
}
