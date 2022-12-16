package util.extensions

fun IntRange.encloses(other: IntRange) =
    this.contains(other.first) && this.contains(other.last)

fun IntRange.overlaps(other: IntRange) =
    (this.contains(other.first) || this.contains(other.last)) ||
        (other.contains(this.first) || other.contains(this.last))

/**
 * Chunks the List<String> into a List<List<String>>, where chunks are broken up by the delimiter indicated (default is blank).
 * @return a List of Lists of Strings - List<List<String>> - with each sublist representing the groups of
 * lines from the input.
 */
fun List<String>.chunked(delimiter: String = ""): List<List<String>> =
    fold<String, MutableList<MutableList<String>>>(mutableListOf(mutableListOf())) { groups, next ->
        if (next.trim() == delimiter) {
            groups.add(mutableListOf())
        } else {
            groups.last().add(next)
        }
        groups
    }

/**
 * Given a list of Strings, make sure all strings are the same length, and pad them with the given [padChar] if not.
 * This is mainly here because IntelliJ will trim trailing spaces from lines in files by default, so input lines
 * that would otherwise be the same length end up not being the proper length. This is important if you expect to
 * split an input line on a certain delimiter or at a certain chunk size, and then turn that into a [util.collections.Matrix],
 * since the Matrix requires all input lines to be equal length.
 */
fun List<String>.padToMaxLength(padChar: Char): List<String> = with(this.maxOf { it.length }) {
    map {
        it.padEnd(this, padChar)
    }
}

/**
 * A convenience so that you can make a range without having to worry about positive or negative
 * step. If the starting position were greater than the ending position, you'd normally need to
 * add a -1 for the step - this takes care of that.
 */
infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

/**
 * Take a list of int ranges and reduce them so that any overlapping segments are removed. This would be the
 * same as creating a [Set] for each range, and then doing a [Set.union] on them. But for really large
 * ranges, like those found on [Day 15 of the 2022 Advent of Code](https://adventofcode.com/2022/day/15). With
 * the resulting list of ranges, you can easily count the number of unique positions represented by each.
 */
fun List<IntRange>.reduce(): List<IntRange> =
    if (this.size <= 1) {
        this
    } else {
        val sorted = this.sortedBy { it.first }
        sorted.drop(1).fold(mutableListOf(sorted.first())) { reduced, range ->
            val lastRange = reduced.last()
            if (range.first <= lastRange.last) {
                reduced[reduced.lastIndex] = (lastRange.first..maxOf(lastRange.last, range.last))
            } else {
                reduced.add(range)
            }
            reduced
        }
    }
