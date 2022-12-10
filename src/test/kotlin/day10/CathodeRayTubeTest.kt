package day10

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.test.assertFalse

@DisplayName("Day 10 - Cathode-Ray Tube")
@TestMethodOrder(OrderAnnotation::class)
class CathodeRayTubeTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 13140`() {
        assertEquals(13140, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return expected output`() {
        val expectedOutput = """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....
        """.trimIndent()
        assertEquals(expectedOutput, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 17940`() {
        assertEquals(17940, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return expected output`() {
        // ZCBAJFJZ
        val expectedOutput = """
            ####..##..###...##....##.####...##.####.
            ...#.#..#.#..#.#..#....#.#.......#....#.
            ..#..#....###..#..#....#.###.....#...#..
            .#...#....#..#.####....#.#.......#..#...
            #....#..#.#..#.#..#.#..#.#....#..#.#....
            ####..##..###..#..#..##..#.....##..####.
        """.trimIndent()
        assertEquals(expectedOutput, solver.solvePartTwo())
    }

    @Test
    @Order(99)
    @Disabled
    fun `calculateCycles should return proper values for given cycles with sample input`() {
        val cycles = sampleSolver.crt.computeCycles().filter {
            (20..220 step 40).contains(it.number)
        }
        assertEquals(6, cycles.size)

        assertEquals(20, cycles[0].number)
        assertEquals(21, cycles[0].registerX)
        assertEquals(420, cycles[0].signalStrength)

        assertEquals(60, cycles[1].number)
        assertEquals(19, cycles[1].registerX)
        assertEquals(1140, cycles[1].signalStrength)

        assertEquals(100, cycles[2].number)
        assertEquals(18, cycles[2].registerX)
        assertEquals(1800, cycles[2].signalStrength)

        assertEquals(140, cycles[3].number)
        assertEquals(21, cycles[3].registerX)
        assertEquals(2940, cycles[3].signalStrength)

        assertEquals(180, cycles[4].number)
        assertEquals(16, cycles[4].registerX)
        assertEquals(2880, cycles[4].signalStrength)

        assertEquals(220, cycles[5].number)
        assertEquals(133, cycles[5].lineNumber)
        assertEquals(18, cycles[5].registerX)
        assertEquals(3960, cycles[5].signalStrength)
    }

    @Test
    @Order(99)
    @Disabled
    fun `Sprite position should include the cycle position, and 1 pixel immediately before and after`() {
        val cycle = Cycle(0, 0, 3)
        assertFalse(cycle.spritePosition.contains(1))
        assertTrue(cycle.spritePosition.contains(2))
        assertTrue(cycle.spritePosition.contains(3))
        assertTrue(cycle.spritePosition.contains(4))
        assertFalse(cycle.spritePosition.contains(5))
    }
}

class Solver(data: List<String>) {
    val crt = CathodeRayTube(data.map { Instruction(it) })

    fun solvePartOne(): Int =
        crt.computeCycles().filter {
            (20..220 step 40).contains(it.number)
        }.sumOf { it.signalStrength }

    fun solvePartTwo(): String = crt.drawImage()
}

data class CathodeRayTube(private val instructions: List<Instruction>) {
    fun computeCycles(): List<Cycle> {
        var registerX = 1
        var cycleNumber = 1

        return instructions.mapIndexed { lineNumber, instruction ->
            if (instruction.isNoOp) {
                listOf(Cycle(lineNumber + 1, cycleNumber++, registerX))
            } else {
                val prevRegisterX = registerX
                registerX += instruction.value
                listOf(
                    Cycle(lineNumber + 1, cycleNumber++, prevRegisterX),
                    Cycle(lineNumber + 1, cycleNumber++, prevRegisterX)
                )
            }
        }.flatten()
    }

    fun drawImage() = computeCycles().chunked(40).joinToString("\n") { cycleChunk ->
        cycleChunk.mapIndexed { index, cycle ->
            if (cycle.spritePosition.contains(index)) {
                "#"
            } else {
                "."
            }
        }.joinToString("")
    }
}

data class Cycle(val lineNumber: Int, val number: Int, val registerX: Int) {
    val signalStrength: Int
        get() = number * registerX

    val spritePosition: IntRange
        get() = registerX - 1..registerX + 1
}

class Instruction(line: String) {
    private val operation: String
    val value: Int

    init {
        val s = line.split(" ")
        operation = s[0]
        value = if (s.size > 1) {
            Integer.parseInt(s[1])
        } else {
            0
        }
    }

    val isNoOp: Boolean
        get() = operation == "noop"
}
