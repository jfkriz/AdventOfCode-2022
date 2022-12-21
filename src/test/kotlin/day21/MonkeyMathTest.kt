package day21

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@DisplayName("Day 21 - Monkey Math")
@TestMethodOrder(OrderAnnotation::class)
class MonkeyMathTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 152`() {
        assertEquals(152.0, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 301`() {
        assertEquals(301.0, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 54703080378102`() {
        assertEquals(54703080378102.0, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 3952673930912`() {
        assertEquals(3952673930912.0, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val calculator = MonkeyCalculator(data)
    fun solvePartOne(): Double {
        return calculator.getMonkeyAnswer("root")
    }

    fun solvePartTwo(): Double {
        return calculator.getHumanAnswer("root", "humn")
    }
}

class MonkeyCalculator(data: List<String>) {
    private val monkeys = data.map { Monkey.fromString(it) }.associateBy { it.name }

    fun getMonkeyAnswer(name: String): Double {
        return monkeys[name]!!.yellAnswer(monkeys)
    }

    fun getHumanAnswer(rootName: String, humanName: String): Double {
        val part2Monkeys = monkeys.filter { it.key != humanName }.toMutableMap()
        val rootMonkey = part2Monkeys[rootName]!!
        val rootFirst = part2Monkeys[rootMonkey.first]!!
        val rootSecond = part2Monkeys[rootMonkey.second]!!

        // Originally just started counting up from 0, which worked great for the sample input,
        // but seemed like it was never going to finish with the real input. So do kind of a binary search,
        // start with the full range of a Long, then keep splitting the difference based on how close we are to
        // 0 for the two monkey answers. Also had to switch everything to use Double instead of Long. I think some
        // of the calculations were overflowing the max Long value, so then the binary search could never narrow down
        // the range.
        var min: Long = 0
        var max: Long = Long.MAX_VALUE
        var mid: Long

        while (true) {
            mid = (max + min) / 2
            val iteration = listOf(min, mid, max).map { i ->
                part2Monkeys[humanName] = Monkey(humanName, i.toDouble())
                val v1 = rootFirst.yellAnswer(part2Monkeys)
                val v2 = rootSecond.yellAnswer(part2Monkeys)
                val diff = abs(v1 - v2)
                if (diff == 0.0) {
                    return i.toDouble()
                }
                i to diff
            }.sortedBy { it.second }

            min = min(iteration[0].first, iteration[1].first)
            max = max(iteration[0].first, iteration[1].first)
        }
    }
}

class Monkey(val name: String) {
    private var number: Double? = null
    var first: String? = null
    var second: String? = null
    private var opCode: Char? = null

    constructor(name: String, value: Double) : this(name) {
        this.number = value
    }

    private constructor(name: String, first: String, opCode: Char, second: String) : this(name) {
        this.first = first
        this.second = second
        this.opCode = opCode
    }

    fun yellAnswer(allMonkeys: Map<String, Monkey>): Double =
        if (number != null) {
            number!!
        } else {
            val v1 = allMonkeys[first]!!.yellAnswer(allMonkeys)
            val v2 = allMonkeys[second]!!.yellAnswer(allMonkeys)
            when (opCode) {
                '+' -> v1 + v2
                '-' -> v1 - v2
                '*' -> v1 * v2
                '/' -> v1 / v2
                else -> throw IllegalArgumentException("Invalid operator '$opCode'")
            }
        }

    companion object {
        fun fromString(line: String): Monkey {
            val parts = line.split(" ")
            val name = parts[0].trim(':')
            return if (parts.size == 2) {
                Monkey(name, parts[1].toDouble())
            } else {
                Monkey(name, parts[1], parts[2][0], parts[3])
            }
        }
    }
}
