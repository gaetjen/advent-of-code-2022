import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

typealias PosL = Pair<Long, Long>


/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun <T> transpose(matrix: List<List<T>>): List<List<T>> {
    return List(matrix.first().size) { rowIdx ->
        List(matrix.size) { colIdx ->
            matrix[colIdx][rowIdx]
        }
    }
}

operator fun <T> List<List<T>>.get(p: Pos) = this[p.first][p.second]

fun <T> getRow(grid: List<List<T>>, rowIdx: Int) = grid[rowIdx]
fun <T> getCol(grid: List<List<T>>, colIdx: Int) = grid.map { it[colIdx] }

fun <T> List<T>.split(matchInPost: Boolean = false, matchInPre: Boolean = false, predicate: (T) -> Boolean): List<List<T>> {
    val idx = this.indexOfFirst(predicate)
    return if (idx == -1) {
        listOf(this)
    } else {
        val preSplit = this.slice(0 until idx + if (matchInPre) 1 else 0)
        val tail = this.slice((idx + 1) until this.size).split(matchInPost, matchInPre, predicate).toMutableList()
        if (matchInPost) {
            tail[0] = listOf(this[idx]) + tail[0]
        }
        return listOf(preSplit) + tail
    }
}

fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val idx = this.indexOfFirst(predicate)
    return if (idx == -1) {
        listOf(this)
    } else {
        return listOf(this.take(idx)) + this.drop(idx + 1).split(predicate)
    }
}

private operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean = { input -> !this(input) }

enum class Cardinal {
    NORTH, SOUTH, WEST, EAST;
    companion object {
        val diagonals = listOf(NORTH to WEST, NORTH to EAST, SOUTH to WEST, SOUTH to EAST)
    }
    fun of(pos: Pair<Int, Int>): Pair<Int, Int> {
        val (fromRow, fromCol) = pos
        return when (this) {
            NORTH -> fromRow - 1 to fromCol
            SOUTH -> fromRow + 1 to fromCol
            WEST -> fromRow to fromCol - 1
            EAST -> fromRow to fromCol + 1
        }
    }
}

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        fun fromChar(c: Char): Direction {
            return when (c) {
                'R' -> RIGHT
                'U' -> UP
                'D' -> DOWN
                'L' -> LEFT
                else -> error("$c is not a direction")
            }
        }
    }

    fun move(pos: Pair<Int, Int>): Pair<Int, Int> {
        val (fromX, fromY) = pos
        return when (this) {
            UP -> fromX to fromY + 1
            DOWN -> fromX to fromY - 1
            LEFT -> fromX - 1 to fromY
            RIGHT -> fromX + 1 to fromY
        }
    }

    fun moveL(pos: Pair<Long, Long>): Pair<Long, Long> {
        val (fromX, fromY) = pos
        return when (this) {
            UP -> fromX to fromY + 1
            DOWN -> fromX to fromY - 1
            LEFT -> fromX - 1 to fromY
            RIGHT -> fromX + 1 to fromY
        }
    }
}

fun minMax(gridPositions: Set<Pos>): Pair<Pos, Pos> {
    val minRow = gridPositions.minOf { it.first }
    val maxRow = gridPositions.maxOf { it.first }
    val minCol = gridPositions.minOf { it.second }
    val maxCol = gridPositions.maxOf { it.second }
    return (minRow to minCol) to (maxRow to maxCol)
}

fun printGrid(positions: Map<Pos, String>, width: Int = 1) {
    println("number positions in grid: ${positions.size}")
    val (min, max) = minMax(positions.keys)
    val (minRow, minCol) = min
    val (maxRow, maxCol) = max
    val result = List(maxRow - minRow + 1) { rowIdx ->
        List(maxCol - minCol + 1) { colIdx ->
            if (rowIdx + minRow to colIdx + minCol in positions) {
                positions[rowIdx + minRow to colIdx + minCol]
            } else {
                " ".repeat(width)
            }
        }.joinToString("")
    }
    println(result.joinToString("\n"))
    println("------------------------------------------------------------")
}