package day15

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.Point
import util.extensions.reduce
import kotlin.math.absoluteValue

@DisplayName("Day 15 - Beacon Exclusion Zone")
@TestMethodOrder(OrderAnnotation::class)
class BeaconExclusionZoneTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 26`() {
        assertEquals(26, sampleSolver.solvePartOne(10))
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 56000011`() {
        assertEquals(56000011, sampleSolver.solvePartTwo(0, 20))
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 5125700`() {
        assertEquals(5125700, solver.solvePartOne(2000000))
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 11379394658764`() {
        assertEquals(11379394658764, solver.solvePartTwo(0, 4000000))
    }
}

class Solver(data: List<String>) {
    private val sensorMap = SensorMap(data)
    fun solvePartOne(row: Int) = sensorMap.findNoCoverageOnRow(row)

    fun solvePartTwo(min: Int, max: Int) = sensorMap.findTuningFrequency(min, max)
}

class SensorMap(data: List<String>) {
    private val sensors: Set<Sensor>

    init {
        sensors = data.map { Sensor(it) }.toSet()
    }

    fun findNoCoverageOnRow(row: Int): Int =
        sensors.mapNotNull { it.noCoverageOnRow(row) }.reduce().sumOf { it.last - it.first }

    fun findTuningFrequency(min: Int, max: Int): Long {
        val range = min..max
        val beacon = sensors.firstNotNullOf { sensor ->
            val up = Point(sensor.location.x, sensor.location.y - sensor.beaconDistance - 1, 0)
            val down = Point(sensor.location.x, sensor.location.y + sensor.beaconDistance + 1, 0)
            val left = Point(sensor.location.x - sensor.beaconDistance - 1, sensor.location.y, 0)
            val right = Point(sensor.location.x + sensor.beaconDistance + 1, sensor.location.y, 0)

            (up.lineTo(right, 0) + right.lineTo(down, 0) + down.lineTo(left, 0) + left.lineTo(up, 0))
                .filter { it.x in range && it.y in range }
                .firstOrNull { possibleBeacon -> sensors.none { sensor -> sensor.isWithinRangeOf(possibleBeacon) } }
        }

        return (beacon.x.toLong() * 4000000L) + beacon.y.toLong()
    }
}

data class Sensor(val location: Point<Int>, val closestBeacon: Point<Int>) {
    constructor(line: String) : this(parseLine(line)[0], parseLine(line)[1])

    companion object {
        // Lines look like this:
        // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        private val lineRegex = Regex("^.* x=(.*), y=(.*):.*x=(.*), y=(.*)")
        fun parseLine(line: String): List<Point<Int>> =
            lineRegex.find(line)?.groupValues?.drop(1)?.map(Integer::parseInt)?.chunked(2)?.map {
                Point(it[0], it[1], 0)
            } ?: throw IllegalArgumentException("Input line [$line] does not match expected format")
    }

    val beaconDistance: Int
        get() = location.distanceFrom(closestBeacon)

    fun isWithinRangeOf(other: Point<*>) = location.distanceFrom(other) <= beaconDistance

    fun noCoverageOnRow(row: Int): IntRange? =
        with(beaconDistance - (location.y - row).absoluteValue) {
            (location.x - this..location.x + this).takeIf { it.first <= it.last }
        }
}
