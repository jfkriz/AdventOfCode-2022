package day04

import util.DataFiles
import util.extensions.encloses
import util.extensions.overlaps
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@DisplayName("Day 04 - Camp Cleanup")
@TestMethodOrder(OrderAnnotation::class)
class CampCleanupTest : DataFiles() {
    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 2`() {
        assertEquals(2, CampCleanup(loadSampleInput()).countFullyOverlappingRanges())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 4`() {
        assertEquals(4, CampCleanup(loadSampleInput()).countPartiallyOverlappingRanges())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 562`() {
        assertEquals(562, CampCleanup(loadInput()).countFullyOverlappingRanges())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 924`() {
        assertEquals(924, CampCleanup(loadInput()).countPartiallyOverlappingRanges())
    }
}

class CampCleanup(data: List<String>) {
    private val sectionAssignments = data.map {
        it.split(",")
    }.map {
        SectionAssignment(it[0], it[1])
    }

    fun countFullyOverlappingRanges() = sectionAssignments.count { it.assignmentsAreFullyRedundant() }

    fun countPartiallyOverlappingRanges() = sectionAssignments.count { it.assignmentsArePartiallyRedundant() }
}

class SectionAssignment(first: String, second: String) {
    private val first: IntRange = Integer.parseInt(first.split("-")[0]) .. Integer.parseInt(first.split("-")[1])
    private val second: IntRange = Integer.parseInt(second.split("-")[0]) .. Integer.parseInt(second.split("-")[1])

    fun assignmentsAreFullyRedundant(): Boolean =
        first.encloses(second) || second.encloses(first)

    fun assignmentsArePartiallyRedundant(): Boolean =
        first.overlaps(second)
}

