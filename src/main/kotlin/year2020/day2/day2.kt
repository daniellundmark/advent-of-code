package year2020.day2

import util.readInput

fun main() {

    val lines = readInput("year2020/day2.input")
    val valid = lines
        .mapNotNull { Password.parse(it) }
        .count { it.validPositions() }

    println(valid)
}

data class Password ( val min: Int, val max: Int, val char: Char, val pw: String ){
    companion object {
        // 1-3 a: abcde
        private val regex = Regex("(\\d+)-(\\d+) (\\w): (\\w+)")
        fun parse(line: String): Password?  =
            regex.matchEntire(line)?.destructured
                ?.let { (min, max, char, pw) ->
                    Password(min.toInt(), max.toInt(), char.first(), pw)
                }
    }
    private fun count() = pw.count{ it == char }
    fun validCount() = count() in min..max
    fun validPositions() = (pw[min-1] == char) xor (pw[max-1] == char)
}