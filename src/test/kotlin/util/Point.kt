package util

data class Point<T>(var x: Int, var y: Int, var value: T) {
    fun move(direction: Direction): Point<T> =
        this.apply {
            when (direction) {
                Direction.Up -> y--
                Direction.Down -> y++
                Direction.Left -> x--
                Direction.Right -> x++
            }
        }

    fun move(xCount: Int, yCount: Int): Point<T> =
        this.apply {
            x += xCount
            y += yCount
        }

    fun isSameLocation(other: Point<*>) = this.x == other.x && this.y == other.y

    fun isNeighboringLocation(other: Point<*>) = listOf(
        listOf(x, y - 1), // Up
        listOf(x, y + 1), // Down
        listOf(x - 1, y), // Left
        listOf(x + 1, y), // Right
        listOf(x - 1, y - 1), // Up+Left
        listOf(x - 1, y + 1), // Down+Left
        listOf(x + 1, y - 1), // Up+Right
        listOf(x + 1, y + 1), // Down+Right
    ).any {
        it[0] == other.x && it[1] == other.y
    }

    fun differenceWith(other: Point<*>) = (this.x - other.x) to (this.y - other.y)
}

enum class Direction(val xOffset: Int, val yOffset: Int) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0)
}
