package util.collections

import util.Direction
import util.Point
import java.util.LinkedList
import java.util.Queue

open class Matrix<T>(initialContents: List<List<T>>) : Iterable<List<T>> {
    private var grid = validate(initialContents).map { it }

    /**
     * Get all neighboring points for the given row and column coordinates.
     * @param row the row coordinate of the starting point
     * @param col the column coordinate of the starting point
     * @param includeDiagonal a flag indicating if diagonal neighboring points should be returned. This is `false` by default, indicating that only vertical and horizontal neighbors are returned.
     * @param pointFilter an optional function to further inspect the value of a potential neighboring point. This is useful if you have a weighted graph, or otherwise want to only move in a direction under certain criteria.
     */
    fun getNeighboringPoints(row: Int, col: Int, includeDiagonal: Boolean = false, pointFilter: (currentPoint: Point<T>, neighboringPoint: Point<T>) -> Boolean = { _, _ -> true }): Map<Direction, Point<T>> =
        Direction.values().filter { includeDiagonal || !it.diagonal }.filter {
            (row + it.yOffset < height) && (row + it.yOffset >= 0) &&
                (col + it.xOffset < width) && (col + it.xOffset >= 0)
        }.associateWith {
            pointAt(row + it.yOffset, col + it.xOffset)
        }.filter {
            pointFilter(pointAt(row, col), it.value)
        }

    /**
     * A Breadth-first search to find distances from any point in the matrix to the given end point. This will return a map with the key
     * being a starting point that is able to reach the end, and the value being the number of points in the path to the end (including the
     * start and end points).
     * @param end the end point where we want to traverse from to find valid starting points and distances
     * @param allowDiagonal indicates whether diagonal movement is allowed. By default, this is `false`, only allowing movement up, down, left, and right
     * @param pointFilter an optional function to further inspect the value of a potential neighboring point. This is useful if you have a weighted graph, or otherwise want to only move in a direction under certain criteria.
     * @return a Map of Point to Int values, indicating the distance from the end for each starting point
     */
    fun findPointDistances(end: Point<T>, allowDiagonal: Boolean = false, pointFilter: (currentPoint: Point<T>, neighboringPoint: Point<T>) -> Boolean = { _, _ -> true }): Map<Point<T>, Int> {
        val queue = LinkedList(listOf(end to 0)) as Queue<Pair<Point<T>, Int>>

        val pointDistances = mutableMapOf(end to 0)

        while (queue.any()) {
            val (point, distance) = queue.remove()
            queue.addAll(
                getNeighboringPoints(point.x, point.y, allowDiagonal) { current, neighbor ->
                    pointFilter(current, neighbor) && !pointDistances.containsKey(neighbor)
                }.map {
                    pointDistances[it.value] = distance + 1
                    it.value to distance + 1
                }
            )
        }

        return pointDistances
    }

    /**
     * Turn this matrix on it's side, and return the new representation. By transposing, the first row becomes the first column,
     * the second row becomes the second column, and so on.
     *
     * For example, given the following data:
     * ```
     * 10 11 12 13
     * 20 21 22 23
     * 30 31 32 33
     * ```
     * After the [transpose] operation, the data will look like this:
     * ```
     * 10 20 30
     * 11 21 31
     * 12 22 32
     * 13 23 33
     * ```
     */
    fun transpose(): Matrix<T> {
        val rows = grid.size
        val cols = grid[0].size

        val transposed = Array<Array<Any>>(cols) {
            Array(rows) { }
        }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                transposed[j][i] = grid[i][j] as Any
            }
        }

        grid = transposed.map {
            @Suppress("UNCHECKED_CAST")
            it.toList() as List<T>
        }.toList()
        return this
    }

    private fun validate(contents: List<List<T>>): List<List<T>> {
        val columns = contents[0].size
        contents.all { row -> row.size == columns } || throw IllegalArgumentException("All rows in a matrix must have $columns columns")

        return contents
    }

    val width: Int
        get() = grid[0].size

    val height: Int
        get() = grid.size

    fun row(rowNum: Int): List<T> = grid[rowNum]

    fun column(colNum: Int): List<T> =
        grid.map {
            it[colNum]
        }

    fun pointAt(row: Int, col: Int): Point<T> = Point(row, col, grid[row][col])

    override fun iterator() = grid.iterator()

    override fun equals(other: Any?): Boolean {
        return other != null && this::class == other::class && this.grid == (other as Matrix<*>).grid
    }

    override fun hashCode(): Int {
        return grid.hashCode()
    }
}
