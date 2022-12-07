package day05

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.collections.Matrix
import util.collections.popN
import util.collections.pushN
import util.extensions.chunked
import util.extensions.padToMaxLength
import java.util.Stack

@DisplayName("Day 05 - Supply Stacks")
@TestMethodOrder(OrderAnnotation::class)
class SupplyStacksTest : DataFiles() {
    @Test
    @Order(1)
    fun `Part 1 Sample Input should return CMZ`() {
        assertEquals("CMZ", SupplyStacks(loadSampleInput()).solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return MCD`() {
        assertEquals("MCD", SupplyStacks(loadSampleInput()).solvePartTwo())
    }

    @Suppress("SpellCheckingInspection")
    @Test
    @Order(2)
    fun `Part 1 Real Input should return BSDMQFLSP`() {
        assertEquals("BSDMQFLSP", SupplyStacks(loadInput()).solvePartOne())
    }

    @Suppress("SpellCheckingInspection")
    @Test
    @Order(4)
    fun `Part 2 Real Input should return PGSQBFLDP`() {
        assertEquals("PGSQBFLDP", SupplyStacks(loadInput()).solvePartTwo())
    }

    @Disabled
    @Test
    @Order(99)
    fun `Stack Initialization should produce expected result for sample data`() {
        val stacks = SupplyStacks(loadSampleInput()).initializeStacks(loadSampleInput().chunked()[0])
        assertEquals(3, stacks.size)
        assertEquals(2, stacks[0].size)
        assertEquals('N', stacks[0].pop())
        assertEquals('Z', stacks[0].pop())
        assertEquals('D', stacks[1].pop())
        assertEquals('C', stacks[1].pop())
        assertEquals('M', stacks[1].pop())
        assertEquals('P', stacks[2].pop())
    }
}

class SupplyStacks(data: List<String>) {
    private val initialStacks = initializeStacks(data.chunked()[0])
    private val instructions = loadInstructions(data.chunked()[1])

    fun solvePartOne(): String {
        val stacks = copyStacks()

        instructions.forEach {
            (0 until it.num).forEach { _ ->
                stacks[it.to].push(stacks[it.from].pop())
            }
        }

        return stacks.map { it.pop() }.joinToString("")
    }

    fun solvePartTwo(): String {
        val stacks = copyStacks()

        instructions.forEach {
            stacks[it.to].pushN(stacks[it.from].popN(it.num))
        }

        return stacks.map { it.pop() }.joinToString("")
    }

    internal fun initializeStacks(input: List<String>): List<Stack<Char>> = Matrix(
        input.filter {
            it.contains("[")
        }.padToMaxLength(' ').map {
            it.toCharArray().asList().chunked(4).map { crateColumnChars -> crateColumnChars[1] }
        }.reversed()
    ).transpose().map {
        Stack<Char>().apply { addAll(it.filter { ch -> ch.isLetter() }) }
    }

    private fun loadInstructions(input: List<String>) = input.map { Instruction(it) }

    private fun copyStacks() = initialStacks.map { initial ->
        Stack<Char>().apply {
            addAll(initial)
        }
    }
}

class Instruction(line: String) {
    val num: Int
    val from: Int
    val to: Int

    init {
        // Example line: move 1 from 2 to 1
        val words = line.split(" ")
        num = Integer.parseInt(words[1])
        from = Integer.parseInt(words[3]) - 1 // Subtract one, so we can use it as 0-based index for stack numbers
        to = Integer.parseInt(words[5]) - 1 // Subtract one, so we can use it as 0-based index for stack numbers
    }
}
