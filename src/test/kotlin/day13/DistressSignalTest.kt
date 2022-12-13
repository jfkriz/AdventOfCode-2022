package day13

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.int
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.math.max

@DisplayName("Day 13 - Distress Signal")
@TestMethodOrder(OrderAnnotation::class)
class DistressSignalTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 13`() {
        assertEquals(13, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 140`() {
        assertEquals(140, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should result in properly sorted packets`() {
        val expectedPackets = """
            []
            [[]]
            [[[]]]
            [1,1,3,1,1]
            [1,1,5,1,1]
            [[1],[2,3,4]]
            [1,[2,[3,[4,[5,6,0]]]],8,9]
            [1,[2,[3,[4,[5,6,7]]]],8,9]
            [[1],4]
            [[2]]
            [3]
            [[4,4],4,4]
            [[4,4],4,4,4]
            [[6]]
            [7,7,7]
            [7,7,7,7]
            [[8,7,6]]
            [9]
        """.trimIndent()

        val sorted = sampleSolver.sortPacketsForPartTwo()

        assertEquals(expectedPackets, sorted.joinToString("\n"))
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 5720`() {
        assertEquals(5720, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 23504`() {
        assertEquals(23504, solver.solvePartTwo())
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 1 - correct order`() {
        val data = """
            [1,1,3,1,1]
            [1,1,5,1,1]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] <= p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 2 - correct order`() {
        val data = """
            [[1],[2,3,4]]
            [[1],4]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] <= p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 3 - incorrect order`() {
        val data = """
            [9]
            [[8,7,6]]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] > p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 4 - correct order`() {
        val data = """
            [[4,4],4,4]
            [[4,4],4,4,4]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] <= p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 5 - incorrect order`() {
        val data = """
            [7,7,7,7]
            [7,7,7]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] > p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 6 - correct order`() {
        val data = """
            []
            [3]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] <= p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 7 - incorrect order`() {
        val data = """
            [[[]]]
            [[]]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] > p[1])
    }

    @Test
    @Order(99)
    fun `Packet pair should successfully parse sample 8 - incorrect order`() {
        val data = """
            [1,[2,[3,[4,[5,6,7]]]],8,9]
            [1,[2,[3,[4,[5,6,0]]]],8,9]
        """.trimIndent().split("\n")
        val p = data.map { Packet(it) }
        assertTrue(p[0] > p[1])
    }
}

class Solver(data: List<String>) {
    private val dividerPacketOne = Packet("[[2]]")
    private val dividerPacketTwo = Packet("[[6]]")
    private val packets = data.filter { it.isNotBlank() }.map { Packet(it) }

    fun solvePartOne(): Int {
        return packets.chunked(2).mapIndexed { i, pair ->
            if (pair[0] <= pair[1]) {
                i + 1
            } else {
                0
            }
        }.sum()
    }

    fun solvePartTwo() =
        sortPacketsForPartTwo().mapIndexed { i, packet ->
            if (packet == dividerPacketOne || packet == dividerPacketTwo) {
                i + 1
            } else {
                0
            }
        }.filter { it > 0 }.reduce { acc, i -> acc * i }

    fun sortPacketsForPartTwo(): List<Packet> {
        return packets.union(
            listOf(
                dividerPacketOne,
                dividerPacketTwo
            )
        ).sorted()
    }
}

data class Packet(val value: JsonArray) : Comparable<Packet> {
    val size: Int
        get() = value.size

    constructor(input: String) : this(Json.parseToJsonElement(input) as JsonArray)

    override fun compareTo(other: Packet): Int = comparePacketValues(this.value, other.value)

    private fun comparePacketValues(left: JsonArray, right: JsonArray): Int {
        val len = max(left.size, right.size)

        for (i in 0 until len) {
            val l = left.getOrNull(i)
            val r = right.getOrNull(i)

            // If the left item is null, it means we've exhausted items on the left, and there are still items
            // on the right, which means this pair of arrays is properly ordered
            if (l == null) {
                return -1
            }

            // If the right item is null, it means we've exhausted items on the right, and there are still items
            // on the left, which means this pair of arrays is not properly ordered
            if (r == null) {
                return 1
            }

            // Compare numbers
            if (l is JsonPrimitive && r is JsonPrimitive) {
                if (l.int > r.int) {
                    return 1
                }

                if (r.int > l.int) {
                    return -1
                }

                continue
            }

            // Need to make the single number on the right into an array and then compare to left
            if (l is JsonArray && r !is JsonArray) {
                val compare = comparePacketValues(l, buildJsonArray { add(r) })
                if (compare != 0) {
                    return compare
                }
                continue
            }

            // Need to make the single number on the left into an array and then compare to right
            if (r is JsonArray && l !is JsonArray) {
                val compare = comparePacketValues(buildJsonArray { add(l) }, r)
                if (compare != 0) {
                    return compare
                }
                continue
            }

            // At this point, both should be arrays
            val compare = comparePacketValues(l as JsonArray, r as JsonArray)

            // If they are not equal at this point, no need to continue the loop - but if they
            // are equal, it means we need to continue evaluating the array until we find inequality,
            // or we've exhausted both arrays (which would mean they are equal)
            if (compare != 0) {
                return compare
            }
        }

        return 0
    }

    override fun toString(): String {
        return Json.encodeToString(value)
    }
}
