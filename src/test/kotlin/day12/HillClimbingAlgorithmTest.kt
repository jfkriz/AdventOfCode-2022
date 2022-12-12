package day12

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Point
import util.collections.Matrix

@DisplayName("Day 12 - Hill Climbing Algorithm")
@TestMethodOrder(OrderAnnotation::class)
class HillClimbingAlgorithmTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 31`() {
        assertEquals(31, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 29`() {
        assertEquals(29, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 447`() {
        assertEquals(447, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 446`() {
        assertEquals(446, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val heightMap: Matrix<Int>
    private val start: Point<Int>
    private val end: Point<Int>
    init {
        var s = Point(0, 0, 'a'.code)
        var e = Point(0, 0, 'z'.code)
        data.forEachIndexed { x, row ->
            row.forEachIndexed { y, ch ->
                if (ch == 'S') {
                    s = Point(x, y, 'a'.code)
                } else if (ch == 'E') {
                    e = Point(x, y, 'z'.code)
                }
            }
        }

        start = s
        end = e
        heightMap = Matrix(
            data.map {
                it.map { ch ->
                    when (ch) {
                        'S' -> 'a'
                        'E' -> 'z'
                        else -> ch
                    }
                }.map { ch -> ch.code }
            }
        )
    }

    fun solvePartOne() =
        heightMap.findPointDistances(end, allowDiagonal = false) { currentPoint, neighboringPoint ->
            currentPoint.value - neighboringPoint.value <= 1
        }[start]!!

    fun solvePartTwo() =
        heightMap.findPointDistances(end, allowDiagonal = false) { currentPoint, neighboringPoint ->
            currentPoint.value - neighboringPoint.value <= 1
        }.filter { it.key.value == 'a'.code }.minBy { it.value }.value
}
