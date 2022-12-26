package day24

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Direction
import java.util.LinkedList

@DisplayName("Day 24 - Blizzard Basin")
@TestMethodOrder(OrderAnnotation::class)
class BlizzardBasinTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 18`() {
        assertEquals(18, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 54`() {
        assertEquals(54, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 334`() {
        assertEquals(334, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 934`() {
        assertEquals(934, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val valleyMap: ValleyMap
    private val startPoint: Point
    private val endPoint: Point

    init {
        val (m, p) = parseInput(data)
        valleyMap = m
        startPoint = p.first
        endPoint = p.second
    }

    fun solvePartOne(): Int {
        val (count, _) = findShortestPath(startPoint, endPoint, valleyMap)

        return count
    }

    fun solvePartTwo(): Int {
        val (firstCount, firstResult) = findShortestPath(startPoint, endPoint, valleyMap)

        val (secondCount, secondResult) = findShortestPath(endPoint, startPoint, firstResult)

        val (thirdCount, _) = findShortestPath(startPoint, endPoint, secondResult)

        return firstCount + secondCount + thirdCount
    }

    private fun parseInput(
        lines: List<String>,
    ): Pair<ValleyMap, Pair<Point, Point>> {
        val tiles = mutableMapOf<String, Tile>()

        var maxY = 0
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                val point = Point(x, y)

                val blizzards = mutableListOf<Blizzard>()
                if (BlizzardDirection.values().map { it.code }.contains(c)) {
                    blizzards.add(Blizzard(point, BlizzardDirection.fromCode(c)))
                }
                tiles[point.key] = Tile(c == '#', blizzards)
            }
            maxY = y
        }

        val start = Point.fromKey(tiles.keys.filter { it.endsWith(";0") }.first { !tiles[it]!!.isWall })
        val end = Point.fromKey(tiles.keys.filter { it.endsWith(";$maxY") }.first { !tiles[it]!!.isWall })

        val border = ValleyMap.Border(start.move(Direction.Left), end.move(Direction.Right))

        return Pair(ValleyMap(tiles, border), Pair(start, end))
    }

    private fun findShortestPath(
        start: Point,
        end: Point,
        startValleyMap: ValleyMap
    ): Pair<Int, ValleyMap> {
        val endKey = end.key
        val queue: LinkedList<MutableList<String>> = LinkedList()
        val startPath = mutableListOf<String>()
        startPath.add(start.key)
        queue.add(startPath)

        val seenState = mutableSetOf<String>()
        seenState.add(startValleyMap.state(start))

        val valleyMapMinutes = mutableListOf<ValleyMap>()

        var size = 0

        while (queue.size > 0) {
            val path = queue.pop()
            if (valleyMapMinutes.size < path.size) {
                valleyMapMinutes.add((valleyMapMinutes.lastOrNull() ?: startValleyMap).copy().moveBlizzards())
            }

            val grid = valleyMapMinutes[path.size - 1]

            val currentPosKey = path.last()
            val currentPos = Point.fromKey(currentPosKey)

            val movements: List<Point> = listOf(Direction.Up, Direction.Down, Direction.Left, Direction.Right)
                .map { dir -> currentPos.move(dir) }.union(listOf(currentPos))
                .filter { move -> grid.canMoveTo(move) }

            movements.forEach { nextPos ->
                val nextKey = nextPos.key
                val nextState = grid.state(nextPos)
                val stateSeen = seenState.contains(nextState)
                val isEnd = nextKey == endKey
                if (isEnd) {
                    return Pair(path.size, grid)
                }

                if (!stateSeen) {
                    val newPath = path.toMutableList()
                    newPath.add(nextKey)
                    queue.add(newPath)
                    seenState.add(nextState)

                    if (size != newPath.size) {
                        size = newPath.size
                    }
                }
            }
        }
        return Pair(-1, startValleyMap)
    }
}

data class Point(val x: Int, val y: Int) {
    val key: String
        get() = "$x$delim$y"

    companion object {
        const val delim = ";"
        fun fromKey(key: String): Point {
            val parts = key.split(delim)
            return Point(parts[0].toInt(), parts[1].toInt())
        }
    }

    fun move(dir: Direction) = Point(x + dir.xOffset, y + dir.yOffset)
}

data class ValleyMap(val tiles: MutableMap<String, Tile> = mutableMapOf(), val border: Border) {
    fun copy(): ValleyMap {
        val newMap = tiles.entries.associate { it.key to it.value.copy() }.toMutableMap()

        return ValleyMap(newMap, border)
    }

    fun canMoveTo(point: Point): Boolean {
        val space = this.tiles[point.key]
        if (space == null || space.isWall || space.blizzards.isNotEmpty()) {
            return false
        }
        return true
    }

    fun moveBlizzards(): ValleyMap {
        this.tiles.values.flatMap { it.blizzards }.forEach {
            it.move(this)
        }
        return this
    }

    fun state(point: Point): String {
        val sb = StringBuilder("${point.key}--")
        this.tiles.values.filter { !it.isWall }.forEach {
            sb.append(it.toString())
        }
        return sb.toString()
    }

    data class Border(val topLeft: Point, val bottomRight: Point)
}

data class Tile(val isWall: Boolean, val blizzards: MutableList<Blizzard> = mutableListOf()) {

    fun copy(): Tile {
        return Tile(isWall, blizzards.map { it.copy() }.toMutableList())
    }

    fun remove(blizzard: Blizzard) {
        this.blizzards.remove(blizzard)
    }

    fun add(blizzard: Blizzard) {
        this.blizzards.add(blizzard)
    }

    override fun toString(): String {
        return if (isWall) {
            "#"
        } else {
            when (blizzards.size) {
                0 -> "."
                1 -> blizzards[0].dir.code.toString()
                else -> blizzards.size.toString()
            }
        }
    }
}

data class Blizzard(val point: Point, val dir: BlizzardDirection) {
    fun copy(): Blizzard {
        return Blizzard(point, dir)
    }

    fun move(valleyMap: ValleyMap) {
        valleyMap.tiles[point.key]!!.remove(this)

        val newPoint = when (dir) {
            BlizzardDirection.Right -> if (valleyMap.border.bottomRight.x - 1 == point.x) Point(valleyMap.border.topLeft.x + 1, point.y) else point.move(Direction.Right)
            BlizzardDirection.Left -> if (valleyMap.border.topLeft.x + 1 == point.x) Point(valleyMap.border.bottomRight.x - 1, point.y) else point.move(Direction.Left)
            BlizzardDirection.Down -> if (valleyMap.border.bottomRight.y - 1 == point.y) Point(point.x, valleyMap.border.topLeft.y + 1) else point.move(Direction.Down)
            BlizzardDirection.Up -> if (valleyMap.border.topLeft.y + 1 == point.y) Point(point.x, valleyMap.border.bottomRight.y - 1) else point.move(Direction.Up)
        }

        valleyMap.tiles[newPoint.key]!!.add(Blizzard(newPoint, this.dir))
    }
}

enum class BlizzardDirection(val code: Char, val direction: Direction) {
    Up('^', Direction.Up),
    Down('v', Direction.Down),
    Left('<', Direction.Left),
    Right('>', Direction.Right);

    companion object {
        fun fromCode(code: Char) =
            BlizzardDirection.values().firstOrNull { it.code == code } ?: throw IllegalArgumentException("Invalid blizzard direction $code")
    }
}
