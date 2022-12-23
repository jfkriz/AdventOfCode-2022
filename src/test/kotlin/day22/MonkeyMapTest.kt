package day22

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Direction
import util.Point
import util.extensions.chunked

@DisplayName("Day 22 - Monkey Map")
@TestMethodOrder(OrderAnnotation::class)
class MonkeyMapTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput(), sampleCube)
    }
    private val solver by lazy {
        Solver(loadInput(), cube)
    }

    private val sampleCube = Cube(
        listOf(
            CubeFace(
                1, 1 to 1, 4,
                mapOf(
                    // 9,10,11,12 -> 4,3,2,1
                    Direction.Up to { currentPoint -> Point(4 - (currentPoint.x - 4 * 2) + 1, 5, Tile.Open) to Direction.Down },
                    // 1,2,3,4 -> 5,6,7,8
                    Direction.Left to { currentPoint -> Point(currentPoint.y + 4, 5, Tile.Open) to Direction.Down },
                    // 1,2,3,4 -> 12,11,10,9
                    Direction.Right to { currentPoint -> Point(16, 4 - (currentPoint.y - 4 * 2) + 1, Tile.Open) to Direction.Left }
                )
            ),
            CubeFace(
                2, 1 to 5, 4,
                mapOf(
                    Direction.Up to { currentPoint -> Point(4 - (currentPoint.x - 4 * 2) + 1, 1, Tile.Open) to Direction.Down },

                    Direction.Left to { currentPoint -> Point(4 * 3 - (currentPoint.y - 4 * 2) + 1, 12, Tile.Open) to Direction.Up },
                    // 1,2,3,4 -> 12,11,10,9
                    Direction.Down to { currentPoint -> Point(4 - (currentPoint.x - 4 * 2) + 1, 12, Tile.Open) to Direction.Up }
                )
            ),
            CubeFace(
                3, 5 to 5, 4,
                mapOf(
                    // 5,6,7,8 -> 1,2,3,4
                    Direction.Up to { currentPoint -> Point(9, currentPoint.x - 4, Tile.Open) to Direction.Right },
                    // 5,6,7,8 -> 12,11,10,9
                    Direction.Down to { currentPoint -> Point(9, 4 * 2 - (currentPoint.x - 4 * 2) + 1, Tile.Open) to Direction.Right }
                )
            ),
            CubeFace(
                4, 9 to 5, 4,
                mapOf(
                    // 5,6,7,8 -> 16,15,14,13
                    Direction.Right to { currentPoint -> Point(4 * 3 - (currentPoint.y - 4 * 2) + 1, 9, Tile.Open) to Direction.Down }
                )
            ),
            CubeFace(
                5, 9 to 9, 4,
                mapOf(
                    // 9,10,11,12 -> 8,7,6,5
                    Direction.Left to { currentPoint -> Point(4 * 2 - (currentPoint.x - 4 * 2) + 1, 8, Tile.Open) to Direction.Up },
                    // 9,10,11,12 -> 4,3,2,1
                    Direction.Down to { currentPoint -> Point(4 - (currentPoint.x - 4 * 2) + 1, 8, Tile.Open) to Direction.Up }
                )
            ),
            CubeFace(
                6, 13 to 9, 4,
                mapOf(
                    // 13,14,15,16 -> 8,7,6,5
                    Direction.Up to { currentPoint -> Point(12, 4 * 3 - (currentPoint.x - 4 * 2) + 1, Tile.Open) to Direction.Left },
                    // 13,14,15,16 -> 8,7,6,5
                    Direction.Down to { currentPoint -> Point(1, 4 * 3 - (currentPoint.x - 4 * 2) + 1, Tile.Open) to Direction.Right },
                    // 9,10,11,12 -> 4,3,2,1
                    Direction.Right to { currentPoint -> Point(4, 4 - (currentPoint.y - 4 * 2) + 1, Tile.Open) to Direction.Left },
                )
            )
        )
    )

    private val cube = Cube(
        listOf(
            CubeFace(
                1, 51 to 1, 50,
                mapOf(
                    // x 51,52,53,54 -> y 151,152,153,154
                    Direction.Up to { currentPoint -> Point(1, 100 + currentPoint.x, Tile.Open) to Direction.Right },
                    // y 1,2,3,4 -> y 150,149,148,147
                    Direction.Left to { currentPoint -> Point(1, 50 - (currentPoint.y - 50 * 2) + 1, Tile.Open) to Direction.Right },
                )
            ),
            CubeFace(
                2, 101 to 1, 50,
                mapOf(
                    // x 101-150 -> x 1-50
                    Direction.Up to { currentPoint -> Point(currentPoint.x - 100, 200, Tile.Open) to Direction.Up },
                    // y 1-50 -> y 150,101
                    Direction.Right to { currentPoint -> Point(100, 50 - (currentPoint.y - 50 * 2) + 1, Tile.Open) to Direction.Left },
                    // x 101-150 -> y 51-100
                    Direction.Down to { currentPoint -> Point(100, currentPoint.x - 50, Tile.Open) to Direction.Left },
                )
            ),
            CubeFace(
                3, 51 to 51, 50,
                mapOf(
                    // y 51-100 -> x 1-50
                    Direction.Left to { currentPoint -> Point(currentPoint.y - 50, 101, Tile.Open) to Direction.Down },
                    // y 51-100 -> x 101-150
                    Direction.Right to { currentPoint -> Point(currentPoint.y + 50, 50, Tile.Open) to Direction.Up },
                )
            ),
            CubeFace(
                4, 1 to 101, 50,
                mapOf(
                    // x 1-50 -> y 51-100
                    Direction.Up to { currentPoint -> Point(51, currentPoint.x + 50, Tile.Open) to Direction.Right },
                    // y 101-150 -> y 50-1
                    Direction.Left to { currentPoint -> Point(51, 50 - (currentPoint.y - 50 * 2) + 1, Tile.Open) to Direction.Right },
                )
            ),
            CubeFace(
                5, 51 to 101, 50,
                mapOf(
                    // y 101-150 -> y 50-1
                    Direction.Right to { currentPoint -> Point(150, 50 - (currentPoint.y - 50 * 2) + 1, Tile.Open) to Direction.Left },
                    // x 51-100 -> y 151-200
                    Direction.Down to { currentPoint -> Point(50, currentPoint.x + 100, Tile.Open) to Direction.Left },
                )
            ),
            CubeFace(
                6, 1 to 151, 50,
                mapOf(
                    // y 151-200 -> x 51-100
                    Direction.Left to { currentPoint -> Point(currentPoint.y - 100, 1, Tile.Open) to Direction.Down },
                    // y 151-200 -> x 51-100
                    Direction.Right to { currentPoint -> Point(currentPoint.y - 100, 150, Tile.Open) to Direction.Up },
                    // x 1-50 -> x 101-150
                    Direction.Down to { currentPoint -> Point(currentPoint.x + 100, 1, Tile.Open) to Direction.Down },
                )
            ),
        )
    )

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 6032`() {
        assertEquals(6032, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 5031`() {
        assertEquals(5031, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 95358`() {
        assertEquals(95358, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 144361`() {
        assertEquals(144361, solver.solvePartTwo())
    }
}

class Solver(data: List<String>, private val cube: Cube) {
    private val monkeyMap = data.chunked().let {
        MonkeyMap(it[0], it[1][0])
    }

    fun solvePartOne() = solve()

    fun solvePartTwo() = solve(cube)

    private fun solve(cube: Cube? = null): Int {
        val moved = monkeyMap.processInstructions(cube)
        val facingValue = when (moved.second) {
            Direction.Right -> 0
            Direction.Down -> 1
            Direction.Left -> 2
            else -> 3
        }
        return (1000 * (moved.first.y)) +
            (4 * (moved.first.x)) +
            facingValue
    }
}

class MonkeyMap(mapData: List<String>, movementInstructions: String) {
    private val drawing = mapData.mapIndexed { y, row ->
        row.mapIndexedNotNull { x, ch ->
            with(Tile.fromCode(ch)) {
                if (this != null) {
                    Point(x + 1, y + 1, this)
                } else {
                    null
                }
            }
        }
    }.flatten()

    private val startingPoint = drawing.minBy { if (it.y == 1) it.x else Integer.MAX_VALUE }

    private val instructions = movementInstructions.split(Regex("(?<=[LR])|(?=[LR])"))

    fun processInstructions(cube: Cube? = null): Pair<Point<Tile>, Direction> {
        var currentPoint = startingPoint
        var facing = Direction.Right
        instructions.forEach { i ->
            if (i == "L" || i == "R") {
                facing = getDirection(i, facing)
            } else {
                val moved = move(Integer.parseInt(i), currentPoint, facing, cube)
                currentPoint = moved.first
                facing = moved.second
            }
            println("Move $i, $currentPoint, $facing")
        }
        return currentPoint to facing
    }

    private fun move(spaces: Int, startingPoint: Point<Tile>, startingFacing: Direction, cube: Cube? = null): Pair<Point<Tile>, Direction> {
        var newPoint = startingPoint
        var newFacing = startingFacing

        var dirMin: Int
        var dirMax: Int
        var points = if (newFacing == Direction.Right || newFacing == Direction.Left) {
            val s = drawing.filter { it.y == startingPoint.y }
            dirMin = s.minOf { it.x }
            dirMax = s.maxOf { it.x }
            s
        } else {
            val s = drawing.filter { it.x == startingPoint.x }
            dirMin = s.minOf { it.y }
            dirMax = s.maxOf { it.y }
            s
        }

        for (i in 0 until spaces) {
            val next = newPoint.copy().move(newFacing).let { n ->
                var p = points.find { it.x == n.x && it.y == n.y }
                if (p == null) {
                    p = if (cube == null) {
                        when (newFacing) {
                            Direction.Right -> points.find { it.x == dirMin && it.y == n.y }
                            Direction.Left -> points.find { it.x == dirMax && it.y == n.y }
                            Direction.Down -> points.find { it.x == n.x && it.y == dirMin }
                            else -> points.find { it.x == n.x && it.y == dirMax }
                        }
                    } else {
                        val face = cube.getFace(newPoint)
                        val xlate = face?.getTransitionedPoint(newPoint, newFacing)
                        if (xlate != null) {
                            val nextPoint = drawing.find { it.x == xlate.first.x && it.y == xlate.first.y }
                            if (nextPoint != null) {
                                if (nextPoint.value != Tile.Wall) {
                                    newFacing = xlate.second
                                    points = if (newFacing == Direction.Right || newFacing == Direction.Left) {
                                        val s = drawing.filter { it.y == nextPoint.y }
                                        dirMin = s.minOf { it.x }
                                        dirMax = s.maxOf { it.x }
                                        s
                                    } else {
                                        val s = drawing.filter { it.x == nextPoint.x }
                                        dirMin = s.minOf { it.y }
                                        dirMax = s.maxOf { it.y }
                                        s
                                    }
                                }
                                nextPoint
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
                }
                p
            }

            if (next == null) {
                throw IllegalStateException("Hmmm, should not get here - tried to navigate off board? Point: $newPoint, Direction: $newFacing")
            } else if (next.value == Tile.Wall) {
                break
            } else {
                newPoint = next
                continue
            }
        }

        return newPoint to newFacing
    }

    private fun getDirection(dir: String, currentDir: Direction): Direction =
        when (currentDir) {
            Direction.Up -> if (dir == "R") Direction.Right else Direction.Left
            Direction.Right -> if (dir == "R") Direction.Down else Direction.Up
            Direction.Down -> if (dir == "R") Direction.Left else Direction.Right
            else -> if (dir == "R") Direction.Up else Direction.Down
        }
}

enum class Tile(val code: Char) {
    Open('.'),
    Wall('#');

    companion object {
        fun fromCode(code: Char): Tile? = Tile.values().firstOrNull { it.code == code }
    }
}

data class Cube(val faces: List<CubeFace>) {
    fun getFace(currentPoint: Point<Tile>): CubeFace? =
        faces.find { currentPoint.x in it.rangeX && currentPoint.y in it.rangeY }
}

data class CubeFace(val name: Int, val upperLeft: Pair<Int, Int>, val length: Int, val transitions: Map<Direction, (current: Point<Tile>) -> Pair<Point<Tile>, Direction>>) {
    val rangeX: IntRange = (upperLeft.first..(upperLeft.first + length))
    val rangeY: IntRange = (upperLeft.second..(upperLeft.second + length))

    fun getTransitionedPoint(currentPoint: Point<Tile>, direction: Direction): Pair<Point<Tile>, Direction> =
        transitions[direction]!!(currentPoint)
}
