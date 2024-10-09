package com.discord.mydiscord

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication()
class MyDiscordApplication

fun main(args: Array<String>) {
    runApplication<MyDiscordApplication>(*args)
}
