package day19

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import util.extensions.chunked
import kotlin.random.Random

@DisplayName("Day 19 - Not Enough Minerals")
@TestMethodOrder(OrderAnnotation::class)
class NotEnoughMineralsTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput().chunked().map { it.joinToString(" ") { line -> line.trim() } })
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 33`() {
        assertEquals(33, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 3472`() {
        assertEquals(56 * 62, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 1613`() {
        assertEquals(1613, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 46816`() {
        assertEquals(46816, solver.solvePartTwo())
    }
    @Test
    @Order(1)
    @Disabled("Random solutions take too long to run, and occasionally fail...")
    fun `Part 1 Sample Input should return 33 using randomized solution`() {
        assertEquals(33, sampleSolver.solvePartOneRandomly())
    }

    @Test
    @Order(3)
    @Disabled("Random solutions take too long to run, and occasionally fail...")
    fun `Part 2 Sample Input should return 3472 using randomized solution`() {
        assertEquals(56 * 62, sampleSolver.solvePartTwoRandomly())
    }

    @Test
    @Order(2)
    @Disabled("Random solutions take too long to run, and occasionally fail...")
    fun `Part 1 Real Input should return 1613 using randomized solution`() {
        assertEquals(1613, solver.solvePartOneRandomly())
    }

    @Test
    @Order(4)
    @Disabled("Random solutions take too long to run, and occasionally fail...")
    fun `Part 2 Real Input should return 46816 using randomized solution`() {
        assertEquals(46816, solver.solvePartTwoRandomly())
    }
}

class Solver(data: List<String>) {
    private val robotFactory = RobotFactory(data)
    fun solvePartOne(): Int {
        val results = robotFactory.buildGeodesRecursive(24)
        return results.map {
            it.key * it.value
        }.sum()
    }

    fun solvePartTwo(): Int {
        val results = robotFactory.buildGeodesRecursive(32, 3)
        return results.map {
            it.value
        }.reduce { acc, i -> acc * i }
    }

    fun solvePartOneRandomly(): Int {
        val results = robotFactory.buildGeodes(24, 1000000)
        return results.map {
            it.key * it.value
        }.sum()
    }

    fun solvePartTwoRandomly(): Int {
        val results = robotFactory.buildGeodes(32, 200000000, 3)
        return results.map {
            it.value
        }.reduce { acc, i -> acc * i }
    }
}

class RobotFactory(data: List<String>) {
    private val blueprints: List<Blueprint>
    init {
        val lineRegex = Regex("(\\d+)")
        blueprints = data.map { line ->
            val matches = lineRegex.findAll(line)
            val numbers = mutableListOf<Int>()
            for (m in matches) {
                numbers.add(Integer.parseInt(m.value))
            }
            Blueprint(
                id = numbers[0],
                oreRobotCost = numbers[1],
                clayRobotCost = numbers[2],
                obsidianRobotOreCost = numbers[3],
                obsidianRobotClayCost = numbers[4],
                geodeRobotOreCost = numbers[5],
                geodeRobotObsidianCost = numbers[6]
            )
        }
    }

    fun buildGeodes(minutes: Int, numberOfSimulations: Long, maxRules: Int = blueprints.size): Map<Int, Int> =
        blueprints.take(maxRules).associate { blueprint ->
            blueprint.id to (0..numberOfSimulations).maxOf {
                randomizeBlueprintProcessing(blueprint, minutes)
            }
        }

    fun buildGeodesRecursive(minutes: Int, maxRules: Int = blueprints.size): Map<Int, Int> {
        return blueprints.take(maxRules).associate {
            it.id to recursiveBlueprintProcessing(it, minutes)
        }
    }

    private fun randomizeBlueprintProcessing(blueprint: Blueprint, minutes: Int): Int {
        val state = State(blueprint)
        for (minute in 0 until minutes) {
            val robotChoice = RobotType.randomize()
            if (state.canBuildGeodeRobot) {
                state.buildGeodeRobot()
            } else if (robotChoice == RobotType.Obsidian && state.canBuildObsidianRobot && state.shouldBuildObsidianRobot) {
                state.buildObsidianRobot()
            } else if (robotChoice == RobotType.Ore && state.canBuildOreRobot) {
                state.buildOreRobot()
            } else if (robotChoice == RobotType.Clay && state.canBuildClayRobot) {
                state.buildClayRobot()
            }

            state.doWork().completeWork()
        }

        return state.geodeCount
    }

    private fun recursiveBlueprintProcessing(blueprint: Blueprint, minutes: Int): Int {
        val state = State(blueprint)
        val score = GeodeScore()
        return processRecursive(minutes, state, score).geodeCount
    }

    private fun processRecursive(ticksRemaining: Int, state: State, geodeScore: GeodeScore): State {
        if (ticksRemaining == 0) {
            geodeScore.bestScore = state.geodeCount
            return state
        }

        // Trying to exit from recursion early. This will calculate the number of additional geodes that this state
        // could produce if it could also produce one more geode robot each time. If that is less than the current
        // best count, we can exit.
        var possibleGeodes = state.geodeCount
        for (i in 0 until ticksRemaining) {
            possibleGeodes += i + state.geodeRobots
        }
        if (possibleGeodes < geodeScore.bestScore) {
            return state
        }

        // We can shortcut the other calls if we know we should build a geode bot (always build one if we have the resources)
        // or an obsidian bot (if we have enough resources, and we don't have enough obsidian bots but have an excess of clay robots)
        if (state.canBuildGeodeRobot) {
            return processRecursive(ticksRemaining - 1, state.copy().buildGeodeRobot().doWork().completeWork(), geodeScore)
        }

        if (state.canBuildObsidianRobot && state.shouldBuildObsidianRobot) {
            return processRecursive(ticksRemaining - 1, state.copy().buildObsidianRobot().doWork().completeWork(), geodeScore)
        }

        val newStates = mutableListOf<State>()
        if (state.canBuildObsidianRobot) {
            newStates.add(state.copy().buildObsidianRobot())
        }

        if (state.shouldBuildOreRobot) {

            newStates.add(state.copy().buildOreRobot())
        }

        if (state.shouldBuildClayRobot) {
            newStates.add(state.copy().buildClayRobot())
        }

        if (state.oreCount <= state.blueprint.maxRobotOreCost) {
            newStates.add(state.copy())
        }

        return newStates.map {
            processRecursive(ticksRemaining - 1, it.doWork().completeWork(), geodeScore)
        }.ifEmpty {
            listOf(state)
        }.maxBy { it.geodeCount }
    }
}

data class GeodeScore(private var score: Int = 0) {
    var bestScore: Int
        get() = score
        // Only sets the score if the given value is greater than the current
        set(value) {
            if (value > score) {
                score = value
            }
        }
}

enum class RobotType {
    @Suppress("unused")
    Geode,
    Ore,
    Obsidian,
    Clay,
    None;

    companion object {
        fun randomize() = Random.nextFloat().let {
            if (it <= .3) {
                Ore
            } else if (it <= .6) {
                Obsidian
            } else if (it <= .9) {
                Clay
            } else {
                None
            }
        }
    }
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: Int,
    val clayRobotCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
) {
    val maxRobotOreCost: Int
        get() = listOf(oreRobotCost, geodeRobotOreCost, obsidianRobotOreCost).max()
}

data class State(
    val blueprint: Blueprint,
    var oreCount: Int = 0,
    var clayCount: Int = 0,
    var obsidianCount: Int = 0,
    var geodeCount: Int = 0,
    var oreRobots: Int = 1,
    var clayRobots: Int = 0,
    var obsidianRobots: Int = 0,
    var geodeRobots: Int = 0,
    var oreBuilding: Boolean = false,
    var clayBuilding: Boolean = false,
    var obsidianBuilding: Boolean = false,
    var geodeBuilding: Boolean = false
) {

    val canBuildGeodeRobot: Boolean
        get() = oreCount >= blueprint.geodeRobotOreCost && obsidianCount >= blueprint.geodeRobotObsidianCost

    val canBuildOreRobot: Boolean
        get() = oreCount >= blueprint.oreRobotCost

    val shouldBuildOreRobot: Boolean
        get() = canBuildOreRobot && oreRobots < blueprint.maxRobotOreCost + 1

    val canBuildObsidianRobot: Boolean
        get() = oreCount >= blueprint.obsidianRobotOreCost && clayCount >= blueprint.obsidianRobotClayCost

    val shouldBuildObsidianRobot: Boolean
        get() = canBuildObsidianRobot && obsidianRobots < blueprint.geodeRobotObsidianCost && clayRobots >= blueprint.obsidianRobotClayCost

    val canBuildClayRobot: Boolean
        get() = oreCount >= blueprint.clayRobotCost

    val shouldBuildClayRobot: Boolean
        get() = canBuildClayRobot && clayRobots < blueprint.obsidianRobotClayCost

    fun buildGeodeRobot(): State {
        oreCount -= blueprint.geodeRobotOreCost
        obsidianCount -= blueprint.geodeRobotObsidianCost
        geodeBuilding = true
        return this
    }

    fun buildOreRobot(): State {
        oreCount -= blueprint.oreRobotCost
        oreBuilding = true
        return this
    }

    fun buildObsidianRobot(): State {
        oreCount -= blueprint.obsidianRobotOreCost
        clayCount -= blueprint.obsidianRobotClayCost
        obsidianBuilding = true
        return this
    }

    fun buildClayRobot(): State {
        oreCount -= blueprint.clayRobotCost
        clayBuilding = true
        return this
    }

    fun doWork(): State {
        oreCount += oreRobots
        clayCount += clayRobots
        obsidianCount += obsidianRobots
        geodeCount += geodeRobots
        return this
    }

    fun completeWork(): State {
        when {
            geodeBuilding -> {
                geodeBuilding = false
                geodeRobots++
            }
            obsidianBuilding -> {
                obsidianBuilding = false
                obsidianRobots++
            }
            clayBuilding -> {
                clayBuilding = false
                clayRobots++
            }
            oreBuilding -> {
                oreBuilding = false
                oreRobots++
            }
        }

        return this
    }
}
