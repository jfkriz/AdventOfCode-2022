package util

open class Point<T>(val x: Int, val y: Int, val value: T) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point<*>

        if (x != other.x) return false
        if (y != other.y) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}
