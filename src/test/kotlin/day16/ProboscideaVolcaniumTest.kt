package day16

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles
import kotlin.math.max

@Suppress("SpellCheckingInspection")
@DisplayName("Day 16 - Proboscidea Volcanium")
@TestMethodOrder(OrderAnnotation::class)
class ProboscideaVolcaniumTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 1651`() {
        assertEquals(1651, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 1707`() {
        assertEquals(1707, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 1673`() {
        assertEquals(1673, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 2343`() {
        assertEquals(2343, solver.solvePartTwo())
    }
}

class Solver(data: List<String>) {
    private val valveMap = ValveMap(data)

    fun solvePartOne() = valveMap.findMaxFlowRateV2("AA", 30)

    fun solvePartTwo() = valveMap.findMaxFlowRateV2("AA", 26, true)
}

class ValveMap(data: List<String>) {
    private val valves = data.map { Valve.fromInput(it) }.associateBy { it.name }

    init {
        calculateShortestPaths()
    }

    fun findMaxFlowRateV2(start: String, minutes: Int, withElephant: Boolean = false): Int {
        return depthFirstSearch(
            originalStart = start,
            currentFlow = 0,
            maxFlow = 0,
            currentValve = start,
            visited = mutableSetOf(),
            tick = 0,
            minutes = minutes,
            runTwice = withElephant
        )
    }

    /**
     * This is an implementation of the [Floyd-Warshall algorithm](https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm#Pseudocode_[11]) for calculating shortest path. This algorithm
     * is good for situations like today's problem, where we need to know shortest paths from any node to any
     * other node.
     */
    private fun calculateShortestPaths() {
        for (k in valves.keys) {
            for (i in valves.keys) {
                for (j in valves.keys) {
                    val ik = valves[i]!!.shortestPaths[k] ?: Short.MAX_VALUE.toInt()
                    val kj = valves[k]!!.shortestPaths[j] ?: Short.MAX_VALUE.toInt()
                    val ij = valves[i]!!.shortestPaths[j] ?: Short.MAX_VALUE.toInt()
                    if (ij > ik + kj)
                        valves[i]!!.shortestPaths[j] = ik + kj
                }
            }
        }

        valves.values.filter { v -> v.flowRate == 0 }.map(Valve::name).forEach { zeroValueValve ->
            for (valve in valves) {
                valve.value.shortestPaths.remove(zeroValueValve)
            }
        }
    }

    /**
     * A [Depth-first Search](https://en.wikipedia.org/wiki/Depth-first_search#Pseudocode) implementation, making sure to not traverse
     * to nodes that would go past the allotted time ([minutes]).
     *
     * To solve the second part, we'll repeat the DFS, but we'll start the clock [tick] over at zero, and we'll
     * retain the currently visited nodes, so we don't repeat them.
     */
    private fun depthFirstSearch(originalStart: String, currentFlow: Int, maxFlow: Int, currentValve: String, visited: Set<String>, tick: Int, minutes: Int, runTwice: Boolean): Int {
        var newMaxFlow = max(maxFlow, currentFlow)
        for ((valve, distance) in valves[currentValve]?.shortestPaths!!) {
            if (!visited.contains(valve) && tick + distance + 1 < minutes) {
                newMaxFlow = depthFirstSearch(
                    originalStart = originalStart,
                    currentFlow = currentFlow + (minutes - tick - distance - 1) * valves[valve]?.flowRate!!,
                    maxFlow = newMaxFlow,
                    currentValve = valve,
                    visited = visited.union(listOf(valve)),
                    tick = tick + distance + 1,
                    minutes = minutes,
                    runTwice = runTwice
                )
            }
        }

        return if (runTwice) {
            depthFirstSearch(
                originalStart,
                currentFlow,
                newMaxFlow,
                originalStart,
                visited,
                tick = 0,
                minutes,
                runTwice = false // Make sure we don't runTwice again, otherwise infinite recursion...
            )
        } else {
            newMaxFlow
        }
    }
}

@Serializable
data class Valve(val name: String, val flowRate: Int, val reachableValves: List<String>) {
    val shortestPaths: MutableMap<String, Int> = reachableValves.associateWith { 1 }.toMutableMap()

    companion object {
        fun fromInput(inputLine: String): Valve {
            // Valve DB has flow rate=0; tunnels lead to valves AC, UN
            val lineRegex = Regex("Valve (.*) has flow.*=(.*);.*valve[s]? (.*)")
            val parts = lineRegex.find(inputLine)?.groupValues?.drop(1)
                ?: throw IllegalArgumentException("Can't parse input line [$inputLine]")
            return Valve(parts[0], Integer.parseInt(parts[1]), parts[2].split(",").map { it.trim() })
        }
    }

    override fun toString() = Json.encodeToString(this)
}
