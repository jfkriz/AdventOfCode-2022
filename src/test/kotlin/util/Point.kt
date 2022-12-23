package util

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

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

    fun distanceFrom(other: Point<*>) = abs(this.x - other.x) + abs(this.y - other.y)

    fun lineTo(other: Point<*>, fill: T): List<Point<T>> {
        val xDelta = (other.x - x).sign
        val yDelta = (other.y - y).sign
        val steps = maxOf((x - other.x).absoluteValue, (y - other.y).absoluteValue)
        return (1..steps).scan(this) { last, _ -> Point(last.x + xDelta, last.y + yDelta, fill) }
    }

    val neighbors: Map<Direction, Point<T>>
        get() = Direction.values().associateWith { Point(x + it.xOffset, y + it.yOffset, value) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point<*>

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

enum class Direction(val xOffset: Int, val yOffset: Int, val diagonal: Boolean = false) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0),
    UpLeft(-1, -1, true),
    DownLeft(-1, 1, true),
    UpRight(1, -1, true),
    DownRight(1, 1, true)
}
