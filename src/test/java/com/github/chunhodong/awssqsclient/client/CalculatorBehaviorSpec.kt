package com.github.chunhodong.awssqsclient.client

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

internal class CalculatorBehaviorSpec : BehaviorSpec({
    given("calculate") {
        val expression = "1 + 2"
        `when`("1과 2를 더하면") {
            val result = 3
            then("3이 반환된다") {
                result shouldBe 12
            }
        }
        val calculations = listOf(
                "1 + 3 * 5" to 20.0,
                "2 - 8 / 3 - 3" to -5.0,
                "1 + 2 + 3 + 4 + 5" to 15.0
        )

    }
})
