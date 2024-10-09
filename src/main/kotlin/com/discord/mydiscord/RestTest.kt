package com.discord.mydiscord

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestTest {

    init {
        println("HELLO HELLO")
    }

    @GetMapping
    fun t() : String {
        return "HELLo"
    }
}