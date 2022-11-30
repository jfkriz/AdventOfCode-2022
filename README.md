# Advent Of Code 2022
My solutions for the [Advent of Code - 2022](https://adventofcode.com/2022)

Create each Day's solutions in a structure like this under [src/main/kotlin]:
```
./day01 +
        |
        +- Part1.kt
        +- Part2.kt
        +- input.txt
        +- test-input.txt
        +- README.md
./day02 +
        |
        +- Part1.kt
        +- Part2.kt
        +- input.txt
        +- test-input.txt
        +- README.md
./dayNN +
        |
        +- Part1.kt
        +- Part2.kt
        +- input.txt
        +- test-input.txt
        +- README.md
```

Assuming each day's solutions follow the above directory structure and naming conventions, you will be able to run each solution with a command like:
```
./gradlew run --args="DayNumber ChallengeNumber [OptionalInputFileName]"
```
Where DayNumber is the day number, like "1" for Day01, and ChallengeNumber is the challenge number, typically "1" or "2".  You can optionally specify an InputFileName (just the file name, it will look for the file in the Day directory), but it will default to a file named "input.txt" in the Day solution directory.

For each new Day, you can simply copy the "template" [dayNN](./src/main/kotlin/dayNN) package, and rename as the appropriate day, or follow these destructions...
- [ ] Create a dayNN directory
- [ ] In the dayNN directory, create a Part1.kt for the first challenge. The file should implement the `Challenge` interface, with implementation for the processInput function. You may add other functions as needed, but only the processInput, accepting an array of lines read from the input file, is required.
```kotlin
package day01

import Challenge

class Part1 : Challenge {
    override fun processInput(data: List<String>) {
        // data is a List of Strings, representing each line in the input file, in the order read from the file

        // iterate over data, doing whatever is needed to calculate the result, then write the result to the console, so it can be entered on the AoC page
        println("Day ${dayNumber()} Part ${partNumber()}, Answer: 42")
    }
}
```
- [ ] In the dayNN directory, create an input.txt file with the input for the challenge; optionally create a test-input.txt with the sample input, for easier testing
- [ ] Since the second challenge builds on the first, copy the completed Part1.kt to Part2.kt, and modify for the second
- [ ] Copy the source of the final HTML page after solving both challenges, and paste into [CodeBeautify](https://codebeautify.org/html-to-markdown) to get a markdown version of the page
- [ ] Add a README each day, with the markdown from CodeBeautify
- [ ] Commit the day's solution