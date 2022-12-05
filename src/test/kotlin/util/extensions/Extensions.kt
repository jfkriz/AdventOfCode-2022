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
