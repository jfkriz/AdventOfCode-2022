import java.util.stream.Collectors

open class Helpers {
    fun loadInput(): List<String> = loadFile("input.txt")

    fun loadSampleInput(): List<String> = loadFile("test-input.txt")

    private fun loadFile(fileName: String) =
        javaClass.classLoader.getResourceAsStream("${javaClass.name.split('.')[0]}/$fileName")?.bufferedReader()?.lines()?.collect(
            Collectors.toList()
        )
            ?: throw IllegalStateException("Can't load data file $fileName")
}

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
