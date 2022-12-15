package day14

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Direction
import util.collections.Matrix

@Suppress("SpellCheckingInspection")
@DisplayName("Day 14 - Regolith Reservoir")
@TestMethodOrder(OrderAnnotation::class)
class RegolithReservoirTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 24`() {
        assertEquals(24, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 93`() {
        assertEquals(93, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 638`() {
        assertEquals(638, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 31722`() {
        assertEquals(31722, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val reservoir = Reservoir(data)
    private val reservoirPartTwo = Reservoir(data, true)
    fun solvePartOne(): Int {
        return reservoir.dropSandGrains(500 to 0)
    }

    fun solvePartTwo(): Int {
        return reservoirPartTwo.dropSandGrains(500 to 0)
    }
}

class Reservoir(input: List<String>, private val infiniteFloor: Boolean = false) {
    private var grid: Matrix<Tile>
    private var minX: Int
    private var maxX: Int
    private var minY: Int
    private var maxY: Int

    init {
        val instructions = input.map { line ->
            line.split(Regex(" -> ")).map { point ->
                val (x, y) = point.split(",").map(Integer::parseInt)
                x to y
            }
        }

        minX = instructions.flatten().minOf { it.first }
        maxX = instructions.flatten().maxOf { it.first }
        minY = instructions.flatten().minOf { it.second }
        maxY = instructions.flatten().maxOf { it.second }
        val width = maxX + 2
        val height = maxY + 2

        grid = Matrix(width, height, Tile.Open)

        if (infiniteFloor) {
            // HACK ALERT!!! I could not get the auto-expansion to work, so I just used an arbitrarily
            // large number for expansion on the right of the grid to hopefully get it working. It
            // did, but ugh... I may come back to this sometime after the holidays.
            grid = grid.expand(right = 1000, fill = Tile.Open).expand(down = 1, fill = Tile.Rock)
            maxY = grid.height
            minX = 0
            maxX = grid.width
        }

        instructions.forEach { rock ->
            createRockFormation(rock)
        }
    }

    fun dropSandGrains(firstGrain: Pair<Int, Int>): Int {
        var dropped = 0
        var currentGrain: Pair<Int, Int>?
        var dropFrom = firstGrain
        do {
            try {
                currentGrain = dropSandGrain(dropFrom)
                if (currentGrain != null) {
                    grid.setPoint(currentGrain.second, currentGrain.first, Tile.Sand)
                    dropped++
                } else {
                    if (infiniteFloor) {
                        grid = grid.expand(left = 10, right = 10, fill = Tile.Open)
                        grid.drawLine(grid.height - 1 to 0, grid.height - 1 to grid.width - 1, Tile.Rock)
                        dropFrom = dropFrom.first to dropFrom.second + 10
                        maxX = grid.width
                        currentGrain = dropFrom
                    }
                }
            } catch (e: HoleIsPluggedException) {
                dropped++
                currentGrain = null
            }
        } while (currentGrain != null)

        println()
        println(grid.toString())
        return dropped
    }

    private fun createRockFormation(rock: List<Pair<Int, Int>>) {
        rock.windowed(2, 1).forEach {
            val (start, end) = it
            grid.drawLine(start, end, Tile.Rock)
        }
    }

    private fun dropSandGrain(currentXY: Pair<Int, Int>): Pair<Int, Int>? {
        var found = true
        var nextXY: Pair<Int, Int> = currentXY

        while (found) {
            val possibleTiles =
                grid.getNeighboringPoints(nextXY.second, nextXY.first, includeDiagonal = true) { _, neighbor ->
                    neighbor.value == Tile.Open &&
                        neighbor.y <= maxY &&
                        neighbor.x >= minX &&
                        neighbor.x <= maxX
                }.filter {
                    it.key == Direction.Down || it.key == Direction.DownLeft || it.key == Direction.DownRight
                }

            if (possibleTiles.isNotEmpty()) {
                for (dir in listOf(Direction.Down, Direction.DownLeft, Direction.DownRight)) {
                    if (possibleTiles.containsKey(dir)) {
                        found = true
                        nextXY = possibleTiles[dir]!!.x to possibleTiles[dir]!!.y
                        break
                    }
                }
            } else {
                found = false
            }
        }

        return if (nextXY.first == currentXY.first && nextXY.second == currentXY.second) {
            if (infiniteFloor) {
                throw HoleIsPluggedException(currentXY)
            }
            null
        } else if (nextXY.second >= maxY || nextXY.first <= minX || nextXY.first >= maxX) {
            null
        } else {
            nextXY
        }
    }
}

class HoleIsPluggedException(startingPoint: Pair<Int, Int>) : Exception("Hole is plugged up: $startingPoint")

enum class Tile(private val symbol: Char) {
    Rock('#'),
    Sand('o'),
    Open('.');

    override fun toString() = symbol.toString()
}
