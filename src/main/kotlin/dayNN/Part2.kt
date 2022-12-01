package dayNN

import Challenge

@Suppress("unused")
class Part2 : Challenge {
    override fun processInput(data: List<String>) {
        val solver = PuzzleSolver(data)
        val result = solver.solvePartTwo()
        println("Day ${dayNumber()} Part ${partNumber()}, Answer: $result")
    }
}