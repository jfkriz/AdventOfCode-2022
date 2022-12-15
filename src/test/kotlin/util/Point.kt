package util

data class Point<T>(var x: Int, var y: Int, var value: T) {
    fun move(direction: Direction): Point<T> =
        this.apply {
            x += direction.xOffset
            y += direction.yOffset
        }

    fun move(xCount: Int, yCount: Int): Point<T> =
        this.apply {
            x += xCount
            y += yCount
        }

    fun isSameLocation(other: Point<*>) = this.x == other.x && this.y == other.y

    fun isNeighboringLocation(other: Point<*>, includeDiagonal: Boolean = true) =
        Direction.values().filter {
            if (includeDiagonal) {
                true
            } else {
                !it.diagonal
            }
        }.map {
            (x + it.xOffset) to (y + it.yOffset)
        }.any {
            it.first == other.x && it.second == other.y
        }

    fun differenceWith(other: Point<*>) = (this.x - other.x) to (this.y - other.y)
}

enum class Direction(val xOffset: Int, val yOffset: Int, val diagonal: Boolean = false) {
    Up(-1, 0),
    Down(1, 0),
    Left(0, -1),
    Right(0, 1),
    UpLeft(-1, -1, true),
    DownLeft(1, -1, true),
    UpRight(-1, 1, true),
    DownRight(1, 1, true)
}
