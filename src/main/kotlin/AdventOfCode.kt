import java.util.stream.Collectors
import kotlin.system.exitProcess

class AdventOfCode {
    fun runMain(args: Array<String>): Int {
        if (args.size < 2) {
            System.err.println(
                """
            Usage: AdventOfCode DayNumber ChallengeNumber [InputFileName]
            Where DayNumber is the day number, like "1" for day01, and ChallengeNumber is the challenge number, typically "1" or "2".
            You can optionally specify an InputFileName (just the file name, it will look for the file in the dayNN directory), but it will default 
            to a file named "input.txt" in the dayNN solution directory.
        """.trimIndent()
            )

            return 1
        }

        val day = "day${args[0].padStart(2, '0')}"
        val challenge = args[1]
        val processor = javaClass.classLoader.loadClass("${day}.Part${challenge}").getDeclaredConstructor()
            .newInstance() as Challenge

        return try {
            val fileName = if (args.size > 2) {
                args[2]
            } else {
                "input.txt"
            }

            val data = javaClass.classLoader.getResourceAsStream("$day/$fileName")?.bufferedReader()?.lines()?.collect(Collectors.toList())
                ?: throw IllegalStateException("Can't load data file $day/$fileName")
            processor.processInput(data)
            0
        } catch (e: Exception) {
            System.err.println("Error running challenge: ${e.message}")
            e.printStackTrace(System.err)
            2
        }
    }
}

fun main(args: Array<String>): Unit = exitProcess(AdventOfCode().runMain(args))
