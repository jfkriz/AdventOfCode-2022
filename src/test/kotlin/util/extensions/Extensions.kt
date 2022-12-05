package util.extensions

fun IntRange.encloses(other: IntRange) =
    this.contains(other.first) && this.contains(other.last)

fun IntRange.overlaps(other: IntRange) =
    (this.contains(other.first) || this.contains(other.last)) ||
        (other.contains(this.first) || other.contains(this.last))

/**
 * Groups the lines of input into lists, where groups of input lines are separated by a blank line.
 * @return a List of Lists of Strings - List<List<String>> - with each sublist representing the groups of
 * lines from the input.
 */
fun List<String>.groupInputLines(): List<List<String>> =
    fold<String, MutableList<MutableList<String>>>(mutableListOf(mutableListOf())) { groups, next ->
        if (next.isBlank()) {
            groups.add(mutableListOf())
        } else {
            groups.last().add(next)
        }
        groups
    }
