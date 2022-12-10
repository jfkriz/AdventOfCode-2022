package day09

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.math.abs
import kotlin.math.sqrt

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

    fun solvePartOne(): Int {
        val head = Position(0, 0)
        val tail = head.copy()
        val tailVisited = mutableSetOf<Position>()

        instructions.forEach {
            val result = it.move(head, tail)
            tailVisited.addAll(result)
        }

        return tailVisited.size
    }

    fun solvePartTwo(): Int {
        val tailVisited = mutableSetOf<Position>()
        val head = Position(0, 0)
        val knots = listOf(
            Position(0, 0),
            Position(0, 0),
            Position(0, 0),
            Position(0, 0),
            Position(0, 0),
            Position(0, 0),
            Position(0, 0),
            Position(0, 0),
            Position(0, 0)
        )

        instructions.forEach {
            tailVisited.addAll(it.move(head, knots))
        }

        return tailVisited.size
    }
}

class Instruction(line: String) {
    private val direction = Direction.fromCode(line.split(" ")[0])
    private val count = Integer.parseInt(line.split(" ")[1])

    fun move(head: Position, tail: Position): Set<Position> {
        val tailPositions = mutableSetOf(tail.copy())
        (0 until count).forEach { _ ->
            when (direction) {
                Direction.Up -> head.y--
                Direction.Down -> head.y++
                Direction.Left -> head.x--
                Direction.Right -> head.x++
            }

            val newTail = tail.moveAdjacentTo(head)
            tail.x = newTail.x
            tail.y = newTail.y
            tailPositions.add(newTail)
        }
        return tailPositions
    }

    fun move(head: Position, knots: List<Position>): Set<Position> {
        val lastKnotPositions = mutableSetOf(knots.last().copy())
        (0 until count).forEach { _ ->
            when (direction) {
                Direction.Up -> head.y--
                Direction.Down -> head.y++
                Direction.Left -> head.x--
                Direction.Right -> head.x++
            }

            var prevKnot = head.copy()
            knots.forEach {
                val newKnot = it.moveKnotAdjacentTo(prevKnot)
                it.x = newKnot.x
                it.y = newKnot.y
                prevKnot = newKnot.copy()
            }
            lastKnotPositions.add(knots.last().copy())
        }
        return lastKnotPositions
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
    // Been a long time, had to search for the formula to find the distance between the two points...
    // https://stackoverflow.com/questions/15179481/how-to-calculate-distance-between-2-points-in-a-2d-matrix#:~:text=This%20length%20can%20be%20computed,Euclidian%20distance%20between%20the%20points.
    private fun calculateDistance(other: Position) = Distance(x - other.x, y - other.y)

    fun moveAdjacentTo(other: Position): Position {
        val distance = other.calculateDistance(this)
        return if (!distance.isAdjacent) {
            val newX = if (distance.xDiff > 0) {
                x + 1
            } else if (distance.xDiff < 0) {
                x - 1
            } else {
                x
            }
            val newY = if (distance.yDiff > 0) {
                y + 1
            } else if (distance.yDiff < 0) {
                y - 1
            } else {
                y
            }

            Position(newX, newY)
        } else {
            copy()
        }
    }

    fun moveKnotAdjacentTo(other: Position): Position {
        val distance = other.calculateDistance(this)
        return if (!distance.shiftedOneInEachDirection && distance.value > 1.0) {
            val newX = if (distance.xDiff > 0) {
                x + 1
            } else if (distance.xDiff < 0) {
                x - 1
            } else {
                x
            }
            val newY = if (distance.yDiff > 0) {
                y + 1
            } else if (distance.yDiff < 0) {
                y - 1
            } else {
                y
            }
            Position(newX, newY)
        } else {
            copy()
        }
    }
}

data class Distance(val xDiff: Int, val yDiff: Int) {
    val value: Double
        get() = sqrt((xDiff * xDiff).toDouble() + (yDiff * yDiff).toDouble())

    val isAdjacent: Boolean
        get() = (abs(xDiff) == abs(yDiff)) || (value <= 1.0)

    val shiftedOneInEachDirection: Boolean
        get() = abs(xDiff) == 1 && abs(yDiff) == 1
}
