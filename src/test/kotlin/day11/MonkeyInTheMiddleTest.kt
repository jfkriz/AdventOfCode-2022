package day11

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.extensions.chunked

@DisplayName("Day 11 - Monkey in the Middle")
@TestMethodOrder(OrderAnnotation::class)
class MonkeyInTheMiddleTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 10605`() {
        assertEquals(10605, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 2713310158`() {
        assertEquals(2713310158, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 78678`() {
        assertEquals(78678, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 15333249714`() {
        assertEquals(15333249714, solver.solvePartTwo())
    }
}

class Solver(private val data: List<String>) {
    fun solvePartOne() = solve(20) {
        it.floorDiv(3L)
    }

    fun solvePartTwo(): Long {
        val monkeys = data.chunked().mapIndexed { index, monkeyData -> Monkey(index, monkeyData) }

        // Since all the monkeys will be using their own testDivisor to determine where to send next, if we
        // calculate the product of all their individual divisors, we can use that as a common "translation"
        // when testing the values. So get the product, then below, we'll mod the calculated item value by this
        // product, preventing it from growing too large to exceed the Long data type.
        val valueTranslation = monkeys.fold(1L) { acc, monkey -> acc * monkey.testDivisor }

        return solve(10000) {
            it.mod(valueTranslation)
        }
    }

    private fun solve(rounds: Int, worryTranslation: ((Long) -> Long)): Long {
        val monkeys = data.chunked().mapIndexed { index, monkeyData -> Monkey(index, monkeyData) }

        (0 until rounds).forEach { _ ->
            monkeys.forEach {
                it.throwItems(monkeys, worryTranslation)
            }
        }

        return monkeys.sortedByDescending { it.totalItemsInspected }.take(2).map { it.totalItemsInspected }.reduce { acc, i -> acc * i }.toLong()
    }
}

data class Monkey(val number: Int, private val data: List<String>) {
    @Suppress("JoinDeclarationAndAssignment")
    private val items: MutableList<Long>
    private val operation: Operation
    internal val testDivisor: Int
    private val throwToWhenTrue: Int
    private val throwToWhenFalse: Int
    var totalItemsInspected: ULong = 0u

    init {
        items = data[1].split(":")[1].split(",").map {
            Integer.parseInt(it.trim()).toLong()
        }.toMutableList()
        operation = Operation(data[2].split(":")[1].split("=")[1].trim())
        testDivisor = Integer.parseInt(data[3].split(" ").last())
        throwToWhenTrue = Integer.parseInt(data[4].split(" ").last())
        throwToWhenFalse = Integer.parseInt(data[5].split(" ").last())
    }

    fun throwItems(monkeys: List<Monkey>, valueTranslation: (Long) -> Long) {
        items.forEach {
            totalItemsInspected++
            val newItem = valueTranslation(operation.calculate(it))
            if (newItem % testDivisor == 0L) {
                monkeys[throwToWhenTrue].items.add(newItem)
            } else {
                monkeys[throwToWhenFalse].items.add(newItem)
            }
        }
        items.clear()
    }
}

class Operation(input: String) {
    private val opCode: Char
    private val operand1: String
    private val operand2: String

    init {
        val parts = input.split(" ").map(String::trim)
        operand1 = parts[0]
        opCode = parts[1][0]
        operand2 = parts[2]
    }

    fun calculate(value: Long): Long {
        val first = determineValue(operand1, value)
        val second = determineValue(operand2, value)
        return when (opCode) {
            '+' -> first.plus(second)
            '*' -> first.times(second)
            else -> throw IllegalArgumentException("Invalid operation code $opCode")
        }
    }

    private fun determineValue(code: String, value: Long): Long =
        if (code == "old") {
            value
        } else {
            Integer.parseInt(code).toLong()
        }
}
