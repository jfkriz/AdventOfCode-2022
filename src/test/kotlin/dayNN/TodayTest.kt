package dayNN

import Helpers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@DisplayName("Day NN - Description")
@TestMethodOrder(OrderAnnotation::class)
class TodayTest : Helpers() {
    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 42`() {
        assertEquals(42, Solver(loadSampleInput()).solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 90210`() {
        assertEquals(90210, Solver(loadSampleInput()).solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 42`() {
        assertEquals(42, Solver(loadInput()).solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 90210`() {
        assertEquals(90210, Solver(loadInput()).solvePartTwo())
    }
}

class Solver(@Suppress("UNUSED_PARAMETER") data: List<String>) {
    fun solvePartOne(): Int {
        return 42
    }

    fun solvePartTwo(): Int {
        return 90210
    }
}

