package day08

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Point
import util.collections.Matrix

@DisplayName("Day 08 - Treetop Tree House")
@TestMethodOrder(OrderAnnotation::class)
class TreetopTreeHouseTest : DataFiles() {
    private val sampleForestMap by lazy {
        ForestMap(loadSampleInput())
    }
    private val forestMap by lazy {
        ForestMap(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 21`() {
        assertEquals(21, sampleForestMap.countVisibleTrees())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 8`() {
        assertEquals(8, sampleForestMap.findHighestScenicScore())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 1711`() {
        assertEquals(1711, forestMap.countVisibleTrees())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 301392`() {
        assertEquals(301392, forestMap.findHighestScenicScore())
    }
}

class ForestMap(data: List<String>) {
    private val grid = Matrix(data.map { row -> row.chunked(1).map(Integer::parseInt) })

    // region Part One - Count number of trees visible from outside the forest
    fun countVisibleTrees() = grid.mapIndexed { row, trees ->
        trees.mapIndexed { col, tree ->
            Point(row, col, tree)
        }
    }.flatten().filter { isVisible(it) }.toSet().size

    private fun isVisible(tree: Point<Int>): Boolean {
        if (tree.isOnEdge(grid)) {
            return true
        }

        val visibleFromLeft = grid.row(tree.x).subList(0, tree.y).all { it < tree.value }
        val visibleFromRight = grid.row(tree.x).subList(tree.y + 1, grid.width).all { it < tree.value }
        val visibleFromTop = grid.column(tree.y).subList(0, tree.x).all { it < tree.value }
        val visibleFromBottom = grid.column(tree.y).subList(tree.x + 1, grid.height).all { it < tree.value }

        return visibleFromLeft || visibleFromRight || visibleFromTop || visibleFromBottom
    }
    // endregion

    // region Part Two - Calculate each tree's "scenic score" (the product of how many trees it can see looking outward in each direction), and return the best one
    fun findHighestScenicScore() = grid.mapIndexed { row, trees ->
        trees.mapIndexed { col, tree ->
            Point(row, col, tree)
        }
    }.flatten().maxOfOrNull { calculateScenicScore(it) }

    private fun calculateScenicScore(tree: Point<Int>): Int {
        if (tree.isOnEdge(grid)) {
            return 0
        }

        val visibleLeft = countVisibleFromTree(tree.value, grid.row(tree.x).subList(0, tree.y).reversed())
        val visibleRight = countVisibleFromTree(tree.value, grid.row(tree.x).subList(tree.y + 1, grid.width))
        val visibleUp = countVisibleFromTree(tree.value, grid.column(tree.y).subList(0, tree.x).reversed())
        val visibleDown = countVisibleFromTree(tree.value, grid.column(tree.y).subList(tree.x + 1, grid.height))

        return visibleLeft * visibleRight * visibleUp * visibleDown
    }

    private fun countVisibleFromTree(tree: Int, treesInLineOfSight: List<Int>): Int {
        var visible = 0
        for (i in treesInLineOfSight.indices) {
            visible++
            if (treesInLineOfSight[i] >= tree) {
                break
            }
        }

        return visible
    }
    // endregion

    private fun <T> Point<T>.isOnEdge(matrix: Matrix<*>) =
        this.x == 0 || this.y == 0 || this.x == matrix.height - 1 || this.y == matrix.width - 1
}
