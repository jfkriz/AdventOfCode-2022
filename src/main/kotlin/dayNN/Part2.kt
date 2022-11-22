package dayNN

import Challenge

class Part2 : Challenge {
    override fun processInput(data: List<String>) {
        val solver = PuzzleSolver(data)
        val result = solver.solve()
        println("Day ${dayNumber()} Part ${partNumber()}, Answer: $result")
    }
}