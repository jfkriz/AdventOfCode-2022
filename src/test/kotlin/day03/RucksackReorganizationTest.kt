package day03

import util.DataFiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@DisplayName("Day 03 - Rucksack Reorganization")
@TestMethodOrder(OrderAnnotation::class)
class RucksackReorganizationTest : DataFiles() {
    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 157`() {
        assertEquals(157, RucksackReorg(loadSampleInput()).sumCommonItemPriorities())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 70`() {
        assertEquals(70, RucksackReorg(loadSampleInput()).sumGroupBadgePriorities())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 7845`() {
        assertEquals(7845, RucksackReorg(loadInput()).sumCommonItemPriorities())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 2790`() {
        assertEquals(2790, RucksackReorg(loadInput()).sumGroupBadgePriorities())
    }

    @Test
    @Order(99)
    @Disabled
    fun `Test item priorities`() {
        assertEquals(1, Item('a').priority)
        assertEquals(26, Item('z').priority)
        assertEquals(27, Item('A').priority)
        assertEquals(52, Item('Z').priority)
    }
}

class RucksackReorg(data: List<String>) {
    private val rucksacks = data.map { Rucksack(it) }

    private val rucksackGroups = data.chunked(3).map { RucksackGroup(it) }

    fun sumCommonItemPriorities(): Int = rucksacks.map { it.commonItem }.sumOf { it.priority }

    fun sumGroupBadgePriorities(): Int = rucksackGroups.map { it.commonItem }.sumOf { it.priority }
}

data class Rucksack(private val contents: String) {
    private val compartments =
        contents.substring(0, contents.length / 2).toCharArray() to contents.substring(contents.length / 2)
            .toCharArray()

    val commonItem: Item
        get() = Item(compartments.first.toSet().intersect(compartments.second.toSet()).first())
}

data class RucksackGroup(private val contents: List<String>) {
    val commonItem: Item
        get() = Item(contents.map { it.toCharArray().toSet() }.reduce { acc, chars -> acc.intersect(chars) }.first())
}

data class Item(val value: Char) {
    val priority: Int
        get() = when (value.category) {
            CharCategory.UPPERCASE_LETTER -> value.code - 38 // Uppercase letters should be 27..52, ASCII 'A' is 65
            CharCategory.LOWERCASE_LETTER -> value.code - 96 // Lowercase letters should be 1..26, ASCII 'a' is 97
            else -> {
                throw IllegalArgumentException("Item value '$value' must be a letter")
            }
        }
}

