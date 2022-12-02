package day01

import Helpers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@DisplayName("Day 01 - Calorie Counter")
@TestMethodOrder(OrderAnnotation::class)
class CalorieCounterTest : Helpers() {
    @Test
    @Order(1)
    fun `Part 1 Sample Input should return 24000`() {
        assertEquals(24000, Elves(loadSampleInput()).findGreatestTotalCalories())
    }

    @Test
    @Order(3)
    fun `Part 2 Sample Input should return 45000`() {
        assertEquals(45000, Elves(loadSampleInput()).findTopThreeTotalCalories())
    }

    @Test
    @Order(2)
    fun `Part 1 Real Input should return 75501`() {
        assertEquals(75501, Elves(loadInput()).findGreatestTotalCalories())
    }

    @Test
    @Order(4)
    fun `Part 2 Real Input should return 215594`() {
        assertEquals(215594, Elves(loadInput()).findTopThreeTotalCalories())
    }
}

class Elves(data: List<String>) {
    private var elfCalories = data.fold(mutableListOf(Elf())) { elves, e ->
        if(e.isBlank()) {
            elves.add(Elf())
        } else {
            elves.last().foodItems.add(Integer.parseInt(e))
        }
        elves
    }

    fun findGreatestTotalCalories(): Int {
        return elfCalories.maxByOrNull { it.totalCalories }!!.totalCalories
    }

    fun findTopThreeTotalCalories(): Int {
        return elfCalories.sortedByDescending { it.totalCalories }.take(3).sumOf { it.totalCalories }
    }
}

data class Elf(val foodItems: MutableList<Int> = mutableListOf()) {
    val totalCalories: Int
        get() = this.foodItems.sum()
}

