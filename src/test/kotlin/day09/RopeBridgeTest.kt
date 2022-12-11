package day09

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Direction
import util.Point

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
        assertEquals(2661, ropeBridge.solvePartTwo())
    }
}

class RopeBridge(data: List<String>) {
    private val instructions = data.map { Instruction(it) }

    fun solvePartOne() = calculateTailPositions(instructions, 2).size

    fun solvePartTwo() = calculateTailPositions(instructions, 10).size

    private fun calculateTailPositions(instructions: List<Instruction>, ropeLength: Int): Set<Point<Int>> {
        val tailVisited = mutableSetOf<Point<Int>>()
        val rope = Array(ropeLength) {
            Point(0, 0, it)
        }.asList()

        instructions.forEach {
            it.execute { _, direction ->
                rope[0].move(direction)
                rope.windowed(2).forEachIndexed { index, segment ->
                    val head = segment[0]
                    val tail = segment[1]
                    if (!tail.isSameLocation(head) && !tail.isNeighboringLocation(head)) {
                        val (xMove, yMove) = head.differenceWith(tail).toList().map { n -> n.calculateMove() }
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
    private val direction = line.split(" ")[0].toDirection()
    private val count = Integer.parseInt(line.split(" ")[1])

    fun execute(func: (index: Int, direction: Direction) -> Unit) {
        (0 until count).forEach {
            func(it, direction)
        }
    }

    private fun String.toDirection(): Direction =
        when (this) {
            "U" -> Direction.Up
            "D" -> Direction.Down
            "L" -> Direction.Left
            "R" -> Direction.Right
            else -> throw IllegalArgumentException("Invalid code '$this' for Direction")
        }
}
