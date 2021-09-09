package year2020.day7

import util.readInput

// Examples:
// shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
// dotted black bags contain no other bags.
//
// LINE : LEFTSIDE contain RIGHTSIDE.
// LEFTSIDE: BAG
// BAG: WORD WORD bag[s]
// WORD: [a-z]+
// RIGHTSIDE: no other bags | LIST-OF-NUM-BAGS
// LIST-OF-NUM-BAGS: NUM-BAGS[, LIST-OF-NUM-BAGS]
// NUM-BAGS: [1-9] BAG

data class Bag(val type: String, var bags: Map<Bag, Int> = mapOf()) {
    override fun equals(other: Any?): Boolean {
        if (other?.javaClass != javaClass) return false; other as Bag; return this.type == other.type
    }

    override fun hashCode() = type.hashCode()

    fun bag(bag: Bag) = allBags[bag.type] ?: error("Could not find bag $bag in global table")


    companion object {
        val allBags = mutableMapOf<String, Bag>()
        fun bag(type: String) = allBags[type]
    }

}

fun parseBag(line: String): Bag {
    val regex = "(\\w+ \\w+) bags? *".toRegex()
    val (type) = (regex.matchEntire(line.trim()) ?: error("Could not day8.parse $line as day7.Bag")).destructured
    //println("Parsed bag of type: $type")

    val maybeBag = Bag.bag(type)
    if(maybeBag != null) {
        return maybeBag
    }

    // Add a reference of this day7.Bag to the "global day7.Bag table", if there was not already one
    val bag = Bag(type)
    Bag.allBags[type] = bag
    return bag
}

fun parseLeftSide(line: String): Bag = parseBag(line)

fun parseNumBags(line: String): Pair<Bag, Int> {
    val regex = "(\\d+) (.*?)".toRegex()
    val (num, rest) = (regex.matchEntire(line.trim()) ?: error("Could not day8.parse $line as NumBags")).destructured
    val numBags = parseBag(rest) to num.toInt()
    //println("Parsed num bags: $numBags")
    return numBags
}

fun parseRightSide(line: String): Map<Bag, Int>
    = when (line.trim()) {
        "no other bags" -> emptyMap()
        else -> line.split(",").map { parseNumBags(it) }.toMap()
    }.also {
        // println("Parsed contained bags from right side: $it")
    }

fun parseLine(line: String): Bag {
    val (leftSide, rightSide) = line.dropLast(1) // remove the dot
        .split("contain")

    val bag = parseLeftSide(leftSide)
    bag.bags = parseRightSide(rightSide)

    return bag
}

fun canContain(bag: Bag, type: String): Boolean =
    when {
        bag.bags.isEmpty() -> false
        bag.bags.any { b -> b.key.type == type } -> true
        else -> bag.bags.keys.any { b -> canContain( bag.bag(b), type) }
    }

fun countBagsInside(bag: Bag): Int {
    val directBags = bag.bags.values.sum()
    //println("$bag directly contains $directBags")
    return directBags + bag.bags.map { b -> b.value * countBagsInside(bag.bag(b.key)) }.sum()
}

fun main() {
    val lines = readInput("year2020/day7.input")

    val listOfBags = lines.map(::parseLine)

    val count = listOfBags.map { bag -> canContain(bag, "shiny gold") }.filter { it }.count()
    println(count)
    val count2 = countBagsInside(Bag.allBags["shiny gold"]!!)
    println(count2)

}

