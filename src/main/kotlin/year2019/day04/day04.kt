package year2019.day04

import util.rest

tailrec fun nonDecreasing(string: String): Boolean {
    if(string.length <= 1) return true
    if(string.first() > string.rest().first()) return false
    return nonDecreasing(string.rest())
}

tailrec fun adjacentPart1(string: String): Boolean {
    if(string.length <= 1) return false
    if(string.first() == string.rest().first()) return true
    return adjacentPart1(string.rest())
}

fun hasSubString(string: String, sub: String): Boolean {
    return string.indexOf(sub) > 0
}

fun repeated(string: String): List<String> {
    val acc = mutableListOf<String>()
    var rest = string
    while(rest.isNotEmpty()) {
        val next = rest.indexOfFirst { it != rest[0] }
        if(next < 0) {
            acc += rest
            rest = ""
        } else {
            acc += rest.substring(0, next)
            rest = rest.substring(next)
        }
    }

    return acc

}

fun adjacent(string: String): Boolean {
    // Check if there are any repetitions of length 2
    return repeated(string).any { it.length == 2 }
}


fun main() {

    val start = 246515
    val end = 739105

    //val start = 111111
    //val end = 112244

    println(repeated("1122233"))

    val sequence = sequence<Int> {
        for(cur in start..end) {
            val list = cur.toString()
            if(nonDecreasing(list) && adjacent(list)) {
                yield(cur)
            }
        }
    }

    println(sequence.toList().count())

}