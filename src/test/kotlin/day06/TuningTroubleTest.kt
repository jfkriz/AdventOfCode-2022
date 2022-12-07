package day06

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 06 - Tuning Trouble")
@TestMethodOrder(OrderAnnotation::class)
class TuningTroubleTest : DataFiles() {
    private val sampleTuner = Tuner(loadSampleInput())
    private val tuner by lazy { Tuner(loadInput()) }

    @Test
    @Order(1)
    fun `Part 1 Sample Input #1 should return 7`() {
        assertEquals(7, sampleTuner.findStartOfPacketMarker(0))
    }

    @Test
    @Order(2)
    fun `Part 1 Sample Input #2 should return 5`() {
        assertEquals(5, sampleTuner.findStartOfPacketMarker(1))
    }

    @Test
    @Order(3)
    fun `Part 1 Sample Input #3 should return 6`() {
        assertEquals(6, sampleTuner.findStartOfPacketMarker(2))
    }

    @Test
    @Order(4)
    fun `Part 1 Sample Input #4 should return 10`() {
        assertEquals(10, sampleTuner.findStartOfPacketMarker(3))
    }

    @Test
    @Order(5)
    fun `Part 1 Sample Input #5 should return 11`() {
        assertEquals(11, sampleTuner.findStartOfPacketMarker(4))
    }

    @Test
    @Order(7)
    fun `Part 2 Sample Input #1 should return 19`() {
        assertEquals(19, sampleTuner.findStartOfMessageMarker(0))
    }

    @Test
    @Order(8)
    fun `Part 2 Sample Input #2 should return 23`() {
        assertEquals(23, sampleTuner.findStartOfMessageMarker(1))
    }

    @Test
    @Order(9)
    fun `Part 2 Sample Input #3 should return 23`() {
        assertEquals(23, sampleTuner.findStartOfMessageMarker(2))
    }

    @Test
    @Order(10)
    fun `Part 2 Sample Input #4 should return 29`() {
        assertEquals(29, sampleTuner.findStartOfMessageMarker(3))
    }

    @Test
    @Order(11)
    fun `Part 2 Sample Input #5 should return 26`() {
        assertEquals(26, sampleTuner.findStartOfMessageMarker(4))
    }

    @Test
    @Order(6)
    fun `Part 1 Real Input should return 1262`() {
        assertEquals(1262, tuner.findStartOfPacketMarker())
    }

    @Test
    @Order(12)
    fun `Part 2 Real Input should return 3444`() {
        assertEquals(3444, tuner.findStartOfMessageMarker())
    }
}

data class Tuner(val inputData: List<String>) {
    fun findStartOfPacketMarker(iterationNumber: Int = 0) = findMarker(inputData[iterationNumber], 4)

    fun findStartOfMessageMarker(iterationNumber: Int = 0) = findMarker(inputData[iterationNumber], 14)

    private fun findMarker(stream: String, numberOfCharacters: Int) =
        stream.windowed(numberOfCharacters, 1, false) {
            it.toSet()
        }.indexOfFirst {
            it.size == numberOfCharacters
        } + numberOfCharacters
}
