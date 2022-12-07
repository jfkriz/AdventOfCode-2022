package day07

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import util.DataFiles

@DisplayName("Day 07 - No Space Left on Device")
@TestMethodOrder(OrderAnnotation::class)
class NoSpaceLeftOnDeviceTest : DataFiles() {
    private val sampleSolver by lazy {
        Solver(loadSampleInput())
    }
    private val solver by lazy {
        Solver(loadInput())
    }

    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 95437`() {
        assertEquals(95437, sampleSolver.solvePartOne())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 24933642`() {
        assertEquals(24933642, sampleSolver.solvePartTwo())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 1307902`() {
        assertEquals(1307902, solver.solvePartOne())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 7068748`() {
        assertEquals(7068748, solver.solvePartTwo())
    }

    @Test
    @Order(99)
    fun `Part 2 Free Space required should be 8381165 for Sample Input`() {
        assertEquals(8381165, sampleSolver.partTwoFreeSpaceNeeded)
    }

    @Test
    @Order(99)
    fun `Part 2 Free Space required should be 7048086 for Real Input`() {
        assertEquals(7048086, solver.partTwoFreeSpaceNeeded)
    }
}

class Solver(input: List<String>) {
    private val fileSystem = FileSystem(input)

    fun solvePartOne(): Int {
        return fileSystem.processInput().sumDirectoriesWithTotalSizeAtMost(100000)
    }

    fun solvePartTwo(): Int {
        return fileSystem.processInput().findSmallestDirectoryWithTotalSizeAtLeast(partTwoFreeSpaceNeeded)?.totalFileSize ?: -1
    }

    /**
     * This part tripped me up!!! I didn't read the part 2 destructions closely, and thought I had to free up
     * 8381165 for both sample and real input for part 2. Oh no! Part of the solution for part 2
     * required calculating the amount of space needed to free up based on the actual filesystem
     * usage. So even though the example given specified the number (8381165) to free up, I decided to
     * calculate it here even for the example input, to make sure this calculation was working properly for both
     * the sample and the real input.
     */
    internal val partTwoFreeSpaceNeeded: Int
        get() = fileSystem.processInput().let { root ->
            val totalSpace = 70000000 // Same for both sample and real input
            val freeSpace = totalSpace - root.totalFileSize // Current free space on device
            val spaceNeededForUpdate = 30000000 // Same for both sample and real input
            spaceNeededForUpdate - freeSpace // Need to free up this much space - should be 8381165 for sample
        }
}

data class FileSystem(private val input: List<String>) {
    fun processInput(): Directory {
        val rootDirectory = Directory("/", null)
        var currentDirectory = rootDirectory

        input.map { it.split(" ") }.forEach {
            when {
                it[0] == "$" && it[1] == "cd" -> {
                    val newDirectory = it[2]
                    currentDirectory = if (newDirectory == "/") {
                        rootDirectory
                    } else {
                        currentDirectory.navigateTo(newDirectory)
                    }
                }
                it[0] == "$" && it[1] == "ls" -> {
                    // Nothing to do here, but added for clarity
                }
                it[0] == "dir" -> {
                    currentDirectory.addDirectory(it[1])
                }
                it[0].matches(Regex("^[0-9].*")) -> {
                    currentDirectory.addFile(it[1], it[0])
                }
                else -> {
                    throw IllegalArgumentException("Cannot process input [${it.joinToString(" ")}]")
                }
            }
        }

        return rootDirectory
    }
}

data class Directory(val name: String, val parent: Directory?) {
    private val directories: MutableMap<String, Directory> = mutableMapOf()
    private val files: MutableMap<String, Int> = mutableMapOf()

    val totalFileSize: Int
        get() = files.values.sum() + directories.values.sumOf { it.totalFileSize }

    fun sumDirectoriesWithTotalSizeAtMost(max: Int): Int {
        var total = if (totalFileSize <= max) {
            totalFileSize
        } else {
            0
        }

        total += directories.values.sumOf { it.sumDirectoriesWithTotalSizeAtMost(max) }

        return total
    }

    fun findSmallestDirectoryWithTotalSizeAtLeast(min: Int): Directory? {
        var dir = if (totalFileSize >= min) {
            this
        } else {
            null
        }

        directories.values.forEach {
            val d = it.findSmallestDirectoryWithTotalSizeAtLeast(min)

            if (d != null && d.totalFileSize >= min && (dir == null || d.totalFileSize < dir!!.totalFileSize)) {
                dir = d
            }
        }

        return dir
    }

    fun addFile(fileName: String, size: String) {
        files[fileName] = Integer.parseInt(size)
    }

    fun addDirectory(dirName: String): Directory {
        directories[dirName] = Directory(dirName, this)
        return directories[dirName]!!
    }

    fun navigateTo(dirName: String): Directory =
        if (dirName == "..") {
            parent!!
        } else {
            directories[dirName] ?: addDirectory(dirName)
        }
}
