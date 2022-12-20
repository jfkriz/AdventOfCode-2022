package day20

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 20 - Grove Positioning System")
@TestMethodOrder(OrderAnnotation::class)
class GrovePositioningSystemTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 3`() {
        assertEquals(3, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 1623178306`() {
        assertEquals(1623178306L, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 10763`() {
        assertEquals(10763, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 4979911042808`() {
        assertEquals(4979911042808L, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val gps = GPS(data)

    fun solvePartOne(): Int {
        return gps.decrypt()
    }

    fun solvePartTwo(): Long {
        return gps.decryptWithKey(811589153L, 10)
    }
}

class GPS(input: List<String>) {
    private val originalList = input.map(Integer::parseInt).mapIndexed { index, n -> index to n }

    fun decrypt(coordinatePoints: List<Int> = listOf(1000, 2000, 3000)): Int {
        val newList = originalList.map { it }.toMutableList()
        for (original in originalList) {
            newList.indexOf(original).also { oldPosition ->
                newList.removeAt(oldPosition)
                newList.add((oldPosition + original.second).mod(newList.size), original)
            }
        }

        val zeroPosition = newList.indexOfFirst { it.second == 0 }
        return coordinatePoints.sumOf { c -> newList[(zeroPosition + c).mod(newList.size)].second }
    }

    fun decryptWithKey(key: Long, iterations: Int, coordinatePoints: List<Int> = listOf(1000, 2000, 3000)): Long {
        val originalListLong = originalList.map { it.first to it.second.toLong() * key }
        val newList = originalListLong.map { it }.toMutableList()
        (0 until iterations).forEach { _ ->
            for (original in originalListLong) {
                newList.indexOf(original).also { oldPosition ->
                    newList.removeAt(oldPosition)
                    newList.add((oldPosition + original.second).mod(newList.size), original)
                }
            }
        }

        val zeroPosition = newList.indexOfFirst { it.second == 0L }
        return coordinatePoints.sumOf { c -> newList[(zeroPosition + c).mod(newList.size)].second }
    }
}
