package day01

import Challenge

class Part1 : Challenge {
    override fun processInput(data: List<String>) {
        val solver = PuzzleSolver(data)
        val result = solver.solve()
        println("Day ${dayNumber()} Part ${partNumber()}, Answer: $result")
    }
}