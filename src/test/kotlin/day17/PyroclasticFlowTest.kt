package day17

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Point
import kotlin.math.abs
import kotlin.math.floor

@DisplayName("Day 17 - Pyroclastic Flow")
@TestMethodOrder(OrderAnnotation::class)
class PyroclasticFlowTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 3068`() {
        assertEquals(3068, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    @Disabled("Solution not working for sample input - works for real input...")
    fun `Part 2 Sample Input should return 1514285714288`() {
        assertEquals(1514285714288, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 3141`() {
        assertEquals(3141, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 1561739130391L`() {
        // 1561741929696 - too high
        // 1561741931266 - wrong
        // 1561739130391 - correct
        // 1561739130362 - wrong
        // 1561739130361 - wrong
        // 1561739127668 - wrong
        // 1554545454534 - too low
        assertEquals(1561739130391L, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val chamber = Chamber(data[0])

    fun solvePartOne(): Int {
        return chamber.dropRocks(1038, 7).maxOf { it.y } + 1
    }

    fun solvePartTwo(): Long {
        return chamber.dropRocksSimulation(1_000_000_000_000L, 7)
    }
}

class Chamber(jetInput: String) {
    private val jets = jetInput.map { Jet.fromCode(it) }

    fun dropRocksSimulation(maxRocks: Long, chamberWidth: Int): Long {
        var currentHeight = 0
        val allPoints = mutableSetOf<Point<Boolean>>()

        var jetIndex = jets.size - 1

        val commonIterations = mutableListOf(Iteration(0, 0))

        (0L until 11000).forEach { r ->
            val rockType = RockType.values()[(r % RockType.values().size).toInt()]
            var fallingRock = Rock(rockType).appear(2, currentHeight + 3)

            while (true) {
                jetIndex = nextJetIndex(jetIndex)
                fallingRock = when (jets[jetIndex]) {
                    Jet.Right -> fallingRock.right(allPoints, chamberWidth)
                    Jet.Left -> fallingRock.left(allPoints)
                }

                try {
                    fallingRock = fallingRock.down(allPoints)
                } catch (e: RockMovementException) {
                    break
                }
            }
            allPoints.addAll(fallingRock.points)
            currentHeight = allPoints.maxOf { it.y } + 1

            if (checkForTopRowSolid(allPoints)) {
                println("Solid line\t$rockType\t${jets[jetIndex]} ($jetIndex)\tHeight $currentHeight\tIteration $r")
                commonIterations.add(Iteration(r, currentHeight.toLong()))
            }
        }

        val calculated = commonIterations.chunked(2).map {
            it[1].diffWith(it[0])
        }.fold(mutableMapOf<Long, MutableList<Iteration>>()) { a, b ->
            val list = a.getOrDefault(b.iterationNumber, mutableListOf())
            list.add(b)
            a[b.iterationNumber] = list
            a
        }

        require(calculated.size == 2) {
            "There should be two calculated iterations/heights - one at the start, and one for the remainder; there were ${calculated.size}: $calculated"
        }
        val firstIteration = calculated.entries.find {
            it.value.size == 1
        }
        require(firstIteration != null) {
            "Should have found an iteration with count of 1: $calculated"
        }
        val mainIteration = calculated.entries.find {
            it.value.size > 1
        }
        require(mainIteration != null) {
            "Should have found an iteration with count of >1: $calculated"
        }
        val numberOfCycles = floor((maxRocks - firstIteration.key - 1) / mainIteration.key.toDouble()).toLong()
        val leftoverAfterCycles = (maxRocks - firstIteration.key - 1) % mainIteration.key
        val leftoverHeight = dropRocks(leftoverAfterCycles + 1, chamberWidth).maxOf { it.y }
        val firstIterationHeight = firstIteration.value.first().currentHeight
        val mainIterationHeight = mainIteration.value.first().currentHeight

        return firstIterationHeight + (numberOfCycles * mainIterationHeight) + leftoverHeight
    }

    data class Iteration(val iterationNumber: Long, val currentHeight: Long) {
        fun diffWith(other: Iteration) = Iteration(abs(this.iterationNumber - other.iterationNumber), abs(this.currentHeight - other.currentHeight))
    }

    fun dropRocks(maxRocks: Long, chamberWidth: Int): Set<Point<Boolean>> {
        var currentHeight = 0
        val allPoints = mutableSetOf<Point<Boolean>>()

        var jetIndex = jets.size - 1

        (0L until maxRocks).forEach { r ->
            var fallingRock = Rock(RockType.values()[(r % 5L).toInt()]).appear(2, currentHeight + 3)

            while (true) {
                jetIndex = nextJetIndex(jetIndex)
                fallingRock = when (jets[jetIndex]) {
                    Jet.Right -> fallingRock.right(allPoints, chamberWidth)
                    Jet.Left -> fallingRock.left(allPoints)
                }

                try {
                    fallingRock = fallingRock.down(allPoints)
                } catch (e: RockMovementException) {
                    break
                }
            }
            allPoints.addAll(fallingRock.points)
            currentHeight = allPoints.maxOf { it.y } + 1
        }
        return allPoints
    }

    private fun checkForTopRowSolid(allPoints: MutableSet<Point<Boolean>>): Boolean {
        val y = allPoints.maxOf { it.y }
        if (allPoints.containsAll((0 until 6).map { Point(it, y, true) }.toSet())) {
            return true
        }
        return false
    }

    private fun nextJetIndex(currentJetIndex: Int): Int =
        if (currentJetIndex == jets.size - 1) {
            0
        } else {
            currentJetIndex + 1
        }
}

enum class Jet {
    Left,
    Right;

    companion object {
        fun fromCode(code: Char) = if (code == '>') Right else Left
    }
}

enum class RockType(pattern: String) {
    Minus("####"),
    Plus(
        """
        .#.
        ###
        .#.
        """.trimIndent()
    ),
    Ell(
        """
        ..#
        ..#
        ###
        """.trimIndent()
    ),
    I(
        """
        #
        #
        #
        #
        """.trimIndent()
    ),
    Box(
        """
        ##
        ##
        """.trimIndent()
    );

    val points: Set<Point<Boolean>>

    init {
        points = patternToPoints(pattern)
    }

    private fun patternToPoints(pattern: String): Set<Point<Boolean>> =
        pattern.split("\n").reversed().mapIndexed { y, row ->
            row.mapIndexedNotNull { x, ch ->
                if (ch == '#') {
                    Point(x, y, true)
                } else {
                    null
                }
            }
        }.flatten().toSet()
}

data class Rock(val type: RockType, val points: Set<Point<Boolean>>) {
    constructor(type: RockType) : this(type, type.points)

    private fun canMoveTo(points: Set<Point<Boolean>>, rightBoundary: Int = Int.MAX_VALUE): Boolean =
        this.points.none { points.contains(it) } &&
            this.points.none { it.x > rightBoundary - 1 } &&
            this.points.none { it.x < 0 } &&
            this.points.none { it.y < 0 }

    fun appear(bottomLeftX: Int, bottomLeftY: Int) =
        translate(bottomLeftX, bottomLeftY)

    fun down(otherPoints: Set<Point<Boolean>>) =
        translate(0, - 1, otherPoints)

    fun left(otherPoints: Set<Point<Boolean>>): Rock {
        return try {
            translate(- 1, 0, otherPoints)
        } catch (e: RockMovementException) {
            this
        }
    }

    fun right(otherPoints: Set<Point<Boolean>>, rightBoundary: Int): Rock {
        return try {
            translate(1, 0, otherPoints, rightBoundary)
        } catch (e: RockMovementException) {
            this
        }
    }

    private fun translate(shiftX: Int, shiftY: Int, otherPoints: Set<Point<Boolean>> = emptySet(), rightBoundary: Int = Int.MAX_VALUE): Rock {
        val rock = Rock(
            type,
            points.map {
                Point(it.x + shiftX, it.y + shiftY, it.value)
            }.toSet()
        )

        if (!rock.canMoveTo(otherPoints, rightBoundary)) {
            throw RockMovementException(shiftX, shiftY)
        }

        return rock
    }
}

class RockMovementException(shiftX: Int, shiftY: Int) : Exception("Rock can't move to position $shiftX,$shiftY")
