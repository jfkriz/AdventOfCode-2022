package day25

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.math.floor
import kotlin.math.pow

@DisplayName("Day 25 - Full of Hot Air")
@TestMethodOrder(OrderAnnotation::class)
class FullOfHotAirTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 2=-1=0`() {
        assertEquals("2=-1=0", sampleSolver.solvePartOne())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 2-00=12=21-0=01--000`() {
        assertEquals("2-00=12=21-0=01--000", solver.solvePartOne())
    }

    @Test
    @Order(99)
    fun `SnafuNumber should convert values appropriately from SNAFU to Long`() {
        val expectedConversions = mapOf(
            "1=-0-2" to 1747L,
            "12111" to 906L,
            "2=0=" to 198L,
            "21" to 11L,
            "2=01" to 201L,
            "111" to 31L,
            "20012" to 1257L,
            "112" to 32L,
            "1=-1=" to 353L,
            "1-12" to 107L,
            "12" to 7L,
            "1=" to 3L,
            "122" to 37L,
        )
        expectedConversions.forEach {
            assertEquals(it.value, SnafuNumber(it.key).toLong(), "SNAFU number ${it.key} should be ${it.value}")
        }
    }

    @Test
    @Order(99)
    fun `SnafuNumber should convert values appropriately from Long to SNAFU`() {
        val expectedConversions = mapOf(
            "1" to 1L,
            "1=-0-2" to 1747L,
            "12111" to 906L,
            "2=0=" to 198L,
            "21" to 11L,
            "2=01" to 201L,
            "111" to 31L,
            "20012" to 1257L,
            "112" to 32L,
            "1=-1=" to 353L,
            "1-12" to 107L,
            "12" to 7L,
            "1=" to 3L,
            "122" to 37L,
        )
        expectedConversions.forEach {
            assertEquals(it.key, it.value.toSnafu().toString(), "Long number ${it.value} should be SNAFU ${it.key}")
        }
    }
}

class Solver(data: List<String>) {
    private val snafuNumbers = data.map(::SnafuNumber)

    fun solvePartOne(): String {
        return snafuNumbers.sum().toString()
    }

    fun solvePartTwo(): Int {
        return 90210
    }
}

data class SnafuNumber(val value: String) : Number() {
    override fun toByte() = toInt().toByte()

    override fun toChar(): Char = toInt().toChar()

    override fun toDouble(): Double {
        var finalNumber: Double = 0.0

        value.reversed().forEachIndexed { position, digit ->
            val multiplier = if (position == 0) {
                1.0
            } else {
                5.0.pow(position)
            }
            finalNumber += (SnafuDigit.fromDigit(digit).intValue * multiplier)
        }
        return finalNumber
    }

    override fun toFloat() = toDouble().toFloat()

    override fun toInt() = toDouble().toInt()

    override fun toLong() = toDouble().toLong()

    override fun toShort() = toInt().toShort()

    override fun toString() = value

    companion object {
        fun fromDouble(num: Double): SnafuNumber {
            val sb = StringBuilder()
            var remainder = num
            while (remainder != 0.0) {
                val position = (remainder + 2).rem(5).toInt()
                remainder = floor((remainder + 2) / 5)
                sb.append(SnafuDigit.values()[position].digit)
            }
            return SnafuNumber(sb.reverse().toString())
        }
    }
}

enum class SnafuDigit(val digit: Char, val intValue: Int) {
    DoubleMinus('=', -2),
    Minus('-', -1),
    Zero('0', 0),
    One('1', 1),
    Two('2', 2);

    companion object {
        fun fromDigit(digit: Char) = SnafuDigit.values().firstOrNull { it.digit == digit } ?: throw IllegalArgumentException("Invalid SNAFU digit $digit")
    }
}

private fun Double.toSnafu() = SnafuNumber.fromDouble(this)

private fun Long.toSnafu(): SnafuNumber = this.toDouble().toSnafu()

private fun Iterable<SnafuNumber>.sum(): SnafuNumber {
    var sum = 0.0
    for (element in this) {
        sum += element.toDouble()
    }

    return sum.toSnafu()
}
