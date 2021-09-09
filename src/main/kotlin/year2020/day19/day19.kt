package year2020.day19

import util.readInput
import util.split

/*
sealed class Rule {

    companion object {
        val rulebook: MutableMap<Int, Rule> = mutableMapOf()
        fun rule(num: Int) = rulebook[num] ?: error("Could not find rule number $num")
    }

    // Match the beginning of the input and return what can be matched
    abstract fun match(input: String): String

    // Check whether the entire input can be matched
    fun matchEntire(input: String) = match(input) == input

    // Matches a single character in the beginning of the string
    class Literal : Rule {
        private val char: Char
        constructor(input: String) {
            val regex = " *\"(\\w)\" *".toRegex()
            val (char) = regex.matchEntire(input.trim())?.destructured ?: error("Could not parse $input as literal rule")
            this.char = char.first()
        }
        override fun match(input: String) = if(input.isEmpty()) "" else if(input.first() == char) input.first().toString() else ""
        override fun toString() = "$char"
    }

    // Must match several rules after each other
    class Sequence:  Rule {
        private val rules: List<Int>
        constructor(input: String) {
            val numbers = input.trim().split(" ").map(String::toInt)
            if(numbers.isEmpty()) error("Could not parse $input as sequence")
            rules = numbers
        }

        //override fun toString() = "${rules.map { Rule.rulebook[it] }.joinToString ("")}"
        override fun toString() = "$rules"

        override fun match(input: String): String {

            // How far in the string the match has been done
            var matchedLength = 0

            // All rules must match for a sequence match to be successful
            for(rule in rules) {
                val match = Rule.rule(rule).match(input.substring(matchedLength))

                // a step in the sequence failed to match
                if(match.isEmpty()) {
                    return ""
                }

                println("Sequence match: input:$input this:${input.substring(matchedLength)} rule:$rule $match")

                matchedLength += match.length
            }

            return input.substring(0, matchedLength)
        }
    }

    // Match either of two rules
    class Choice: Rule {

        private val left: Rule
        private val right: Rule

        override fun toString() = "[$left|$right]"

        constructor(input: String) {
            val (left, right) = input.split("|", limit = 2)
            this.left = Sequence(left.trim())
            this.right = Sequence(right.trim())
        }

        override fun match(input: String): String {
            val leftMatch = left.match(input)
            val rightMatch = right.match(input)

            println("Choice: $this input:$input left:$leftMatch right:$rightMatch")

            return if(rightMatch.length >= leftMatch.length) {
                rightMatch
            } else {
                leftMatch
            }

        }

    }

}

fun parseRule(line: String): Rule {
    //println("Parsing rule from $line")
    return when {
        line.contains('"') -> Rule.Literal(line)
        line.contains("|") -> Rule.Choice(line)
        else -> Rule.Sequence(line)
    }
}

fun parseRuleLine(line: String): Pair<Int, Rule> {
    val (num, str) = line.split(": ")
    Rule.rulebook[num.toInt()] = parseRule(str)
    return Pair(num.toInt(), Rule.rulebook[num.toInt()]!!)
}

*/

val rulebook: MutableMap<Int, String> = mutableMapOf()

// Does not support recursion, changed the input to skip it
fun toRegex(input: String): String {

    var rule = input.trim()

    if(rule == "\"a\"") return "(a)"
    if(rule == "\"b\"") return "(b)"

    // Choice, generate regexps for each part and join them
    if(rule.contains('|')) {
        val options = input.split("|")
        return "(" + options.map { toRegex(it) }.joinToString ( "|" ) + ")"
    }

    // Else, must be a sequence, resolve the rules and get their regexs
    val numbers = input.trim().split(" ").map(String::toInt)
    val res = numbers.map { toRegex(rulebook[it]!!) }
    return res.joinToString ( "" )
}

fun main() {
    val (rules, lines) = readInput("year2020/day19.input").split(String::isBlank)
    rules.forEach{line ->
        val (num, str) = line.split(": ")
        rulebook[num.toInt()] = str
    }

    println("Rulebook: $rulebook")

    val rule = toRegex(rulebook[0]!!)
    val re = rule.toRegex()
    println("$re")

    lines.forEach{ println("$it <-- ${re.matches(it)}")}

    println(lines.count{ re.matches(it)})

}