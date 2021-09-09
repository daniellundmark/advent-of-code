package year2020.day5

import util.pow
import util.readInput
import util.rest

fun seat(string: String): Int {
    val row = zipping(string.substring(0..6)) { it == 'B' }
    val column = zipping(string.substring(7..9)) { it == 'R' }
    return row * 8 + column
}

tailrec fun recursive(list: List<Char>, isOne: (Char) -> Boolean): Int
  = when (list.isEmpty()) {
    true -> 0
    false -> when (isOne(list.first())) {
        true -> 2.pow(list.size - 1)
        false -> 0
    } + recursive(list.rest(), isOne)
}

fun powersOfTwo(): Sequence<Int> = generateSequence(1) { it * 2 }
fun powersOfTwo(init: Int) = generateSequence(2.pow(init)) { it / 2 }

fun zipping(string: String, isOne: (Char) -> Boolean)
        = string.map(isOne).asSequence().zip(powersOfTwo(string.length-1)) { a, b -> if (a) b else 0 }.sum()

fun parsing(string: String, isOne: (Char) -> Boolean) =
        Integer.parseInt(string.map { if (isOne(it)) 1 else 0 }.joinToString(""), 2)

fun main() {

    val isOne: (Char) -> Boolean = { it == 'B' }
    println(recursive("BFFFBBF".toList(), isOne))
    println(zipping("BFFFBBF", isOne))
    println(parsing("BFFFBBF", isOne))


    val list = readInput("year2020/day5.input").map { seat(it) }.sorted().toList()
    println(list)
    println("***")
    var iterator = list.first()
    for (i in 0 .. list.size) {
        if(list[i] !== iterator++) {
            println("${list[i]-1}")
            return
        }
    }


}
