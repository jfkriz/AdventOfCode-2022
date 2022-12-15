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

infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}
