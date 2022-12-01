package dayNN

import Challenge

@Suppress("unused")
class Part1 : Challenge {
    override fun processInput(data: List<String>) {
        val solver = PuzzleSolver(data)
        val result = solver.solvePartOne()
        println("Day ${dayNumber()} Part ${partNumber()}, Answer: $result")
    }
}