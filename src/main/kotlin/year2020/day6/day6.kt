package year2020.day6

import util.measureTime
import util.readInput
import util.split

fun combineSets(groups: List<List<String>>, op: (Set<Char>, Set<Char>) -> Set<Char>)
    = groups.map { it.map(String::toSet).reduce(op)}


fun main() {
    measureTime {
        val groups = readInput("year2020/day6.input").split(String::isBlank)

        println(combineSets(groups, Set<Char>::union).sumBy(Set<Char>::size))
        println(combineSets(groups, Set<Char>::intersect).sumBy(Set<Char>::size))
    }
}