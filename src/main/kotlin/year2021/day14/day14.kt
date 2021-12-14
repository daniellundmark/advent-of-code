package year2021.day14

import util.measureTime
import util.readInput
import util.rest
import java.math.BigInteger

typealias Rules = Map<Pair<Char, Char>, Char>

fun String.second() = this[1]
operator fun String.component1() = this[0]
operator fun String.component2() = this[1]
operator fun Rules.get(first: Char, second: Char) = this[Pair(first, second)] ?: error("No such rule: $first $second")

fun <K, V> Map<K, V>.mergeReduce(other: Map<K, V>, reduce: (V, V) -> V = { a, b -> b }): Map<K, V> {
   val result = LinkedHashMap<K, V>(this.size + other.size)
   result.putAll(this)
   other.forEach { e -> result[e.key] = result[e.key]?.let { reduce(e.value, it) } ?: e.value }
   return result
}

fun parseRules(lines: List<String>): Rules {
   val re = Regex("(\\w)(\\w) -> (\\w)")
   return lines.mapNotNull { re.matchEntire(it)?.destructured?.let { (a,b,c) -> Pair(a.first(),b.first()) to c.first() } }.toMap()
}

// Perform one step of expansion of the entire string
// This only worked for part 1, obviously
fun expandPart1(polymer: String, rules: Rules): String {
   val windows = polymer.windowed(2, 1, true)
   //println("Windows of $polymer: $windows")
   return windows.map { w ->
      if(w.length == 1) {
         return@map w
      }
      val (a,b) = w
      val new = rules[a,b]
      val exp = "$a${new}"
      //println("Expanding $w: ${a}${b} -> $exp")
      exp
   }.joinToString("")
}

fun part1(rules: Rules, template: String, steps: Int) {
   var polymer = template

   //println("Template:    \t$polymer")
   repeat(steps){
      //measureTime({dur -> println("Expansion $it took $dur")}) {
      polymer = expandPart1(polymer, rules)
      //println("After step ${it+1}:\t$polymer")
      //}
   }

   val counts = polymer.fold(mutableMapOf<Char, Long>()){acc, c -> acc[c] = (acc[c] ?: 0L) + 1; acc }
   println("Part 1 counts: $counts")
   println("Part 1: ${counts.maxOf { it.value } - counts.minOf { it.value }}")
}

// This recursive function only returns the count of each character in the expanded string
// This is to avoid heap space overflow when trying to store the string
data class Input(val a: Char, val b: Char, val steps: Int)
val memo = mutableMapOf<Input, CharacterCount>()
typealias CharacterCount=Map<Char, BigInteger>

fun expand(rules: Rules, a: Char, b: Char, steps: Int): CharacterCount {

   // First see if we have already performed this calculation
   val cached = memo[Input(a, b, steps)]
   if(cached != null) return cached

   // A single expansion is calculated using rules and stored
   val middle = rules[a, b]
   if(steps == 1) {
      val count = listOf(a, middle, b).fold(mutableMapOf<Char, BigInteger>()){acc, c -> acc[c] = (acc[c] ?: BigInteger.ZERO) + BigInteger.ONE; acc }
      memo[Input(a, b, 1)] = count
      return count
   }

   // Otherwise, and recurse this twice with steps-1 on the pairs with the middle expansion
   // (This calculation will be cached above)
   val left = expand(rules, a, middle, steps-1)
   val right = expand(rules, middle, b, steps-1)

   // Merge the counts (minus one for middle, which is counted twice)
   val merged = left.mergeReduce(right, BigInteger::add).toMutableMap()
   merged[middle] = merged[middle]!! - BigInteger.ONE

   //println("Merged counts for steps=$steps $a, $b is $merged")
   memo[Input(a, b, steps)] = merged

   return merged

}

// TODO: This could probably be refactored with the above
fun expand(rules: Rules, polymer: String, steps: Int): CharacterCount {
   // Expanding a string is done by creating pairs of characters, expanding them and merging the results
   val windows = polymer.windowed(2, 1)
   val counts = windows.map { (aa, bb) ->
      Pair(aa,bb) to expand(rules, aa, bb, steps)
   }.fold(mutableMapOf<Char, BigInteger>()){acc, (pair, counts) ->
      val merged = acc.mergeReduce(counts, BigInteger::add).toMutableMap()
      merged[pair.second] = merged[pair.second]!! - BigInteger.ONE
      merged
   }
   // This removes a count for the final character, add it back
   counts[polymer.last()] = counts[polymer.last()]!! + BigInteger.ONE

   return counts
}

fun part2(rules: Rules, template: String, steps: Int) {
   val counts = expand(rules, template, steps)
   println("Part 2 counts: $counts")
   println("Part 2: ${counts.maxOf { it.value } - counts.minOf { it.value }}")
}

fun main() {
   val input = readInput("year2021/small.input")
   var template = input.first()
   val rules = parseRules(input.rest().rest())


   part1(rules, template, 10)
   measureTime {
      part2(rules, template, 40)
   }



}