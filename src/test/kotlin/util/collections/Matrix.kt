package util.collections

open class Matrix<T>(initialContents: List<List<T>>) : Iterable<List<T>> {
    private var grid = validate(initialContents).map { it }

    /**
     * Turn this matrix on it's side, and return the new representation. By transposing, the first row becomes the first column,
     * the second row becomes the second column, and so on.
     *
     * For example, given the following data:
     * ```
     * 10 11 12 13
     * 20 21 22 23
     * 30 31 32 33
     * ```
     * After the [transpose] operation, the data will look like this:
     * ```
     * 10 20 30
     * 11 21 31
     * 12 22 32
     * 13 23 33
     * ```
     */
    fun transpose(): Matrix<T> {
        val rows = grid.size
        val cols = grid[0].size

        val transposed = Array<Array<Any>>(cols) {
            Array(rows) { }
        }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                transposed[j][i] = grid[i][j] as Any
            }
        }

        grid = transposed.map {
            @Suppress("UNCHECKED_CAST")
            it.toList() as List<T>
        }.toList()
        return this
    }

    private fun validate(contents: List<List<T>>): List<List<T>> {
        val columns = contents[0].size
        contents.all { row -> row.size == columns } || throw IllegalArgumentException("All rows in a matrix must have $columns columns")

        return contents
    }

    val width: Int
        get() = grid[0].size

    val height: Int
        get() = grid.size

    fun row(rowNum: Int): List<T> = grid[rowNum]

    fun column(colNum: Int): List<T> =
        grid.map {
            it[colNum]
        }

    override fun iterator() = grid.iterator()

    override fun equals(other: Any?): Boolean {
        return other != null && this::class == other::class && this.grid == (other as Matrix<*>).grid
    }

    override fun hashCode(): Int {
        return grid.hashCode()
    }
}
