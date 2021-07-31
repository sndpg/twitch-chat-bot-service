package io.github.sykq.tcc

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = ["tmi.bots[0].name=testBot", "tmi.bots[0].username=testBot", "tmi.bots[0].password=secret"]
)
internal class TwitchChatBotServiceApplicationTests {

    @Test
    fun contextLoads() {
    }

}
