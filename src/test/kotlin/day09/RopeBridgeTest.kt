package day09

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 09 - Rope Bridge")
@TestMethodOrder(OrderAnnotation::class)
class RopeBridgeTest : DataFiles() {
    private val sampleRopeBridge by lazy {
        RopeBridge(loadSampleInput())
    }
    private val largerSampleRopeBridge by lazy {
        RopeBridge(loadOtherInput("test-input-2.txt"))
    }
    private val ropeBridge by lazy {
        RopeBridge(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 13`() {
        assertEquals(13, sampleRopeBridge.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input #2 should return 36`() {
        assertEquals(36, largerSampleRopeBridge.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 6284`() {
        assertEquals(6284, ropeBridge.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 2661`() {
        // 2639 - too low
        assertEquals(2661, ropeBridge.solvePartTwo())
    }
}

class RopeBridge(data: List<String>) {
    private val instructions = data.map { Instruction(it) }

    fun solvePartOne() = calculateTailPositions(instructions, 2).size

    fun solvePartTwo() = calculateTailPositions(instructions, 10).size

    private fun calculateTailPositions(instructions: List<Instruction>, ropeLength: Int): Set<Position> {
        val tailVisited = mutableSetOf<Position>()
        val rope = Array(ropeLength) {
            Position(0, 0)
        }.asList()

        instructions.forEach {
            it.execute { _, direction ->
                rope[0].move(direction)
                rope.windowed(2).forEachIndexed { index, segment ->
                    val head = segment[0]
                    val tail = segment[1]
                    if (!(tail.isSame(head) || tail.isNeighboring(head))) {
                        val xMove = (head.x - tail.x).calculateMove()
                        val yMove = (head.y - tail.y).calculateMove()
                        tail.move(xMove, yMove)
                    }

                    if (index == ropeLength - 2) {
                        tailVisited.add(rope.last().copy())
                    }
                }
            }
        }
        return tailVisited
    }

    private fun Int.calculateMove() =
        when {
            this > 0 -> 1
            this < 0 -> -1
            else -> 0
        }
}

class Instruction(line: String) {
    val direction = Direction.fromCode(line.split(" ")[0])
    val count = Integer.parseInt(line.split(" ")[1])

    fun execute(func: (index: Int, direction: Direction) -> Unit) {
        (0 until count).forEach {
            func(it, direction)
        }
    }
}

enum class Direction(val code: String) {
    Up("U"),
    Down("D"),
    Left("L"),
    Right("R");

    companion object {
        fun fromCode(c: String): Direction =
            Direction.values().find { it.code == c } ?: throw IllegalArgumentException("Invalid direction code '$c'")
    }
}

data class Position(var x: Int, var y: Int) {
    fun move(direction: Direction): Position =
        when (direction) {
            Direction.Up -> y--
            Direction.Down -> y++
            Direction.Left -> x--
            Direction.Right -> x++
        }.let { this }

    fun move(x: Int, y: Int): Position =
        this.apply {
            this.x += x
            this.y += y
        }

    fun isSame(other: Position) = equals(other)

    fun isNeighboring(other: Position) = listOf(
        Position(x, y - 1),
        Position(x + 1, y),
        Position(x, y + 1),
        Position(x - 1, y),
        Position(x - 1, y - 1),
        Position(x + 1, y - 1),
        Position(x + 1, y + 1),
        Position(x - 1, y + 1)
    ).any {
        it == other
    }
}
