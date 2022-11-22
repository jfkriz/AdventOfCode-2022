interface Challenge {
    fun processInput(data: List<String>)

    fun dayNumber(): String =
        javaClass.name.split('.')[0].replace(Regex("[A-Za-z]"), "")

    fun partNumber(): String =
        javaClass.name.split('.')[1].replace(Regex("[A-Za-z]"), "")
}