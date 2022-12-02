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