package day23

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Direction
import kotlin.math.abs

@DisplayName("Day 23 - Unstable Diffusion")
@TestMethodOrder(OrderAnnotation::class)
class UnstableDiffusionTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 110`() {
        assertEquals(110, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 20`() {
        assertEquals(20, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 4082`() {
        assertEquals(4082, solver.solvePartOne())
    }

    @Test
    @Order(4)
    @Disabled("This test runs for 5 1/2 minutes for my input...")
    fun `Part 2 Real Input should return 1065`() {
        assertEquals(1065, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val elfMap = ElfMap(data)

    fun solvePartOne(): Long {
        val moves = elfMap.moveElves(10)
        val minX = moves.minOf { it.position.first }
        val maxX = moves.maxOf { it.position.first }
        val minY = moves.minOf { it.position.second }
        val maxY = moves.maxOf { it.position.second }

        val width = abs(maxX - minX) + 1
        val height = abs(maxY - minY) + 1
        return (width * height) - moves.size
    }

    fun solvePartTwo(): Int {
        return elfMap.findRoundWithNoMoves()
    }
}

class ElfMap(data: List<String>) {
    private val originalMap = data.mapIndexed { y, row ->
        row.mapIndexedNotNull { x, t ->
            if (t != '.') {
                Elf(x.toLong() to y.toLong())
            } else {
                null
            }
        }
    }.flatten().toSet()

    private val movementProposals = listOf(
        listOf(Direction.Up, Direction.UpRight, Direction.UpLeft),
        listOf(Direction.Down, Direction.DownRight, Direction.DownLeft),
        listOf(Direction.Left, Direction.UpLeft, Direction.DownLeft),
        listOf(Direction.Right, Direction.UpRight, Direction.DownRight)
    )

    fun moveElves(rounds: Int): Set<Elf> {
        var newMap = originalMap.map { it.copy() }
        (0 until rounds).map { round ->
            val proposed = proposeMoves(newMap.toSet(), getMovementProposalOrder(round % 4))
            newMap = proposed.map {
                if (it.proposedPosition == null) {
                    it.copy()
                } else if (proposed.count { p -> p.proposedPosition != null && p.proposedPosition == it.proposedPosition } > 1) {
                    it.copy()
                } else {
                    val xy = it.proposedPosition!!
                    Elf(xy.first to xy.second)
                }.apply {
                    proposedPosition = null
                }
            }
        }
        return newMap.toSet()
    }

    fun findRoundWithNoMoves(): Int {
        var newMap = originalMap.map { it.copy() }
        var round = 0
        while (true) {
            val proposed = proposeMoves(newMap.toSet(), getMovementProposalOrder(round % 4))
            if (proposed.all { it.proposedPosition == null }) {
                break
            }

            newMap = proposed.map {
                if (it.proposedPosition == null) {
                    it.copy()
                } else if (proposed.count { p -> p.proposedPosition != null && p.proposedPosition == it.proposedPosition } > 1) {
                    it.copy()
                } else {
                    val xy = it.proposedPosition!!
                    Elf(xy.first to xy.second)
                }.apply {
                    proposedPosition = null
                }
            }
            round++
        }
        return round + 1
    }

    private fun proposeMoves(currentMap: Set<Elf>, currentMovementProposals: List<List<Direction>>): Set<Elf> {
        val proposedMap = currentMap.map { elfPosition ->
            val neighbors = elfPosition.neighbors
            elfPosition.proposedPosition = null
            if (currentMap.intersect(neighbors.values.toSet()).isNotEmpty()) {
                for (dir in currentMovementProposals) {
                    val canMove = dir.map {
                        val p = elfPosition.move(it)
                        currentMap.contains(p)
                    }.none { it }
                    if (canMove) {
                        val moveTo = elfPosition.move(dir.first())
                        elfPosition.proposedPosition = moveTo.position.first to moveTo.position.second
                        break
                    }
                }
            }
            elfPosition
        }.toSet()

        return proposedMap
    }

    private fun getMovementProposalOrder(currentMovementProposalNumber: Int): List<List<Direction>> =
        movementProposals.subList(currentMovementProposalNumber, movementProposals.size).union(movementProposals.subList(0, currentMovementProposalNumber)).toList()
}

data class Elf(var position: Pair<Long, Long>, var proposedPosition: Pair<Long, Long>? = null) {
    fun move(direction: Direction): Elf =
        this.copy().apply {
            position = position.first + direction.xOffset to position.second + direction.yOffset
        }

    val neighbors: Map<Direction, Elf>
        get() = Direction.values().associateWith { Elf(position.first + it.xOffset to position.second + it.yOffset) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Elf

        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}

enum class Tile(val code: Char) {
    Open('.'),
    Elf('#');

    companion object {
        fun fromCode(code: Char): Tile? = Tile.values().firstOrNull { it.code == code }
    }
}
