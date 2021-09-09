package year2020.day4

import util.readInput
import util.split

fun main() {

    val lines = readInput("year2020/day4.input")

    val credentials = lines.split { it.isBlank() }
            .map { list -> list.joinToString(separator = " ") } // list with all strings on one line

    val checked = credentials.map { check(it) }

    println(checked.count { it })

}

fun check(line: String): Boolean {
    val tokens = line.split(" ").filterNot { it.isBlank() }
    val fields = tokens.map {val f = it.split(":"); Pair(f[0], f[1]) }
    val hasAllFields = hasRequired(fields.map { it.first })
    val values = fields.map { validateField(it.first, it.second) }

    return hasAllFields && values.all { it }
}

fun validateField(field: String, value: String) =
        when(field) {
            "ecl" -> listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value)
            "pid" -> Regex("\\d{9}").matches(value)
            "eyr" -> value.toInt() in 2020..2030
            "hcl" -> Regex("#[0-9a-f]{6}").matches(value)
            "byr" -> value.toInt() in 1920..2002
            "iyr" -> value.toInt() in 2010..2020
            "hgt" -> validateHeight(value)
            "cid" -> true
            else -> false
        }

fun hasRequired(fields: List<String>) = listOf("ecl", "pid", "eyr", "hcl", "byr", "iyr", "hgt").map { fields.contains(it) }.all { it }

fun validateHeight(value: String): Boolean {
    val match = Regex("(\\d+)(cm|in)").matchEntire(value) ?: return false

    val (height, scale) = match.destructured

    return if(scale == "cm")
        height.toInt() in 150 .. 193
    else height.toInt() in 59 .. 76
}