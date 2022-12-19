package day18

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 18 - Boiling Boulders")
@TestMethodOrder(OrderAnnotation::class)
class BoilingBouldersTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(0)
    fun `Part 1 Small Sample Input should return 10`() {
        assertEquals(10, Solver(listOf("1,1,1", "2,1,1")).solvePartOne())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 64`() {
        assertEquals(64, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 58`() {
        assertEquals(58, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 3412`() {
        assertEquals(3412, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 2018`() {
        // 3220 - too high
        assertEquals(2018, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val cubes = data.map(SmallCube::fromString).toSet()

    init {
        cubes.forEach { it.computeExposedSides(cubes) }
    }

    fun solvePartOne(): Int {
        return cubes.sumOf { it.exposedSides }
    }

    fun solvePartTwo(): Int {
        return findAirPockets(cubes)
    }

    private fun findAirPockets(cubes: Set<SmallCube>): Int {
        val (minX, maxX) = cubes.map { it.x }.sorted().let { it.first() to it.last() }
        val (minY, maxY) = cubes.map { it.y }.sorted().let { it.first() to it.last() }
        val (minZ, maxZ) = cubes.map { it.z }.sorted().let { it.first() to it.last() }
        val minAll = listOf(minX, minY, minZ).min() - 1
        val maxAll = listOf(maxX, maxY, maxZ).max() + 1

        val trappedAir = mutableSetOf(SmallCube(minAll, minAll, minAll))

        while (true) {
            val water = mutableSetOf<SmallCube>()
            for (c in trappedAir) {
                for (n in c.neighbors()) {
                    if (cubes.contains(n)) {
                        continue
                    }

                    if ((minAll..maxAll).contains(n.x) && (minAll..maxAll).contains(n.y) && (minAll..maxAll).contains(n.z)) {
                        water.add(n)
                    }
                }
            }
            if (trappedAir.containsAll(water)) {
                break
            }
            trappedAir.addAll(water)
        }

        var exposed = 0
        for (c in cubes) {
            for (n in c.neighbors()) {
                if (trappedAir.contains(n)) {
                    exposed++
                }
            }
        }

        return exposed
    }
}

data class SmallCube(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun fromString(coords: String): SmallCube =
            with(coords.split(",").map(Integer::parseInt)) {
                SmallCube(this[0], this[1], this[2])
            }
    }

    var exposedSides: Int = 6

    fun computeExposedSides(others: Set<SmallCube>): Int {
        exposedSides = 6 - neighbors().filter {
            others.contains(it)
        }.size

        return exposedSides
    }

    fun neighbors(): Set<SmallCube> = setOf(
        SmallCube(x + 1, y, z), SmallCube(x - 1, y, z),
        SmallCube(x, y + 1, z), SmallCube(x, y - 1, z),
        SmallCube(x, y, z + 1), SmallCube(x, y, z - 1)
    )
}
