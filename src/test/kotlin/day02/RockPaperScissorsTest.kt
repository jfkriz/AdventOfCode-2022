package day02

import util.DataFiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@DisplayName("Day 02 - Rock, Paper, Scissors")
@TestMethodOrder(OrderAnnotation::class)
class RockPaperScissorsTest : DataFiles() {
    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 15`() {
        assertEquals(15, RockPaperScissors(loadSampleInput()).scorePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 12`() {
        assertEquals(12, RockPaperScissors(loadSampleInput()).scorePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 14069`() {
        assertEquals(14069, RockPaperScissors(loadInput()).scorePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 12411`() {
        assertEquals(12411, RockPaperScissors(loadInput()).scorePartTwo())
    }
}

class RockPaperScissors(data: List<String>) {
    private val rounds = data.map {
        val (opponent, you) = it.split(" ")
        GameRound(opponent, you)
    }

    private val partTwoRounds = data.map {
        val (opponent, you) = it.split(" ")
        GameRound(opponent, you, true)
    }

    fun scorePartOne(): Int {
        return rounds.sumOf { it.score() }
    }

    fun scorePartTwo(): Int {
        return partTwoRounds.sumOf { it.score() }
    }
}

class GameRound(opponentCode: String, code: String, deterministicScoring: Boolean = false) {
    private val opponentShape = Shape.fromOpponentCode(opponentCode)
    private val shape = if (deterministicScoring) {
        Shape.fromOutcomeCode(code, opponentShape)
    } else {
        Shape.fromCode(code)
    }

    fun score() = shape.score + outcomeScore()

    private fun outcomeScore() =
        if (shape.beats(opponentShape)) {
            6
        } else if (opponentShape.beats(shape)) {
            0
        } else {
            3
        }
}

enum class Shape(val score: Int, val code: String, val opponentCode: String) {
    Rock(1, "X", "A"),
    Paper(2, "Y", "B"),
    Scissors(3, "Z", "C");

    companion object {
        fun fromOpponentCode(c: String) = Shape.values().find { it.opponentCode == c }
            ?: throw IllegalArgumentException("Invalid value '${c}' for Shape")

        fun fromCode(c: String) =
            Shape.values().find { it.code == c } ?: throw IllegalArgumentException("Invalid value '${c}' for Shape")

        fun fromOutcomeCode(outcome: String, opponent: Shape) =
            when(outcome) {
                "X" -> opponent.loser
                "Y" -> opponent.draw
                "Z" -> opponent.winner
                else -> throw IllegalArgumentException("Invalid outcome code '$outcome'")
            }
    }

    fun beats(other: Shape): Boolean {
        return this == Rock && other == Scissors
                || this == Scissors && other == Paper
                || this == Paper && other == Rock
    }

    val winner: Shape
        get() =
            when (this) {
                Rock -> Paper
                Paper -> Scissors
                Scissors -> Rock
            }

    val loser: Shape
        get() =
            when (this) {
                Rock -> Scissors
                Paper -> Rock
                Scissors -> Paper
            }

    val draw: Shape
        get() = this

}
