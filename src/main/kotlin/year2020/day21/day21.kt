package year2020.day21

import util.readInput

fun main() {
    val lines = readInput("year2020/day21.input")
    val regex = "([\\w ]+) \\(contains ([\\w, ]+)\\)".toRegex()

    val ingredients = mutableSetOf<String>()
    val allergens = mutableMapOf<String, MutableSet<String>>()
    val counts = mutableMapOf<String, Int>().withDefault { 0 }

    lines.forEach { line -> regex.matchEntire(line)?.destructured?.let { (ing, alg) ->
        val newIng = ing.split(" ").toMutableSet()
        ingredients.addAll(newIng)
        newIng.forEach { counts[it] = counts.getValue(it) + 1}
        alg.split(",").forEach { it ->
            val a = it.trim()
            //println("Adding $a <-- $newIng")
            if (allergens.containsKey(a)) {
                allergens[a] = allergens[a]!!.intersect(newIng).toMutableSet()
            } else {
                allergens[a] = newIng
            }
        }
    } }

    println("Ingredients: $ingredients")
    println("Allergens: $allergens")
    println("Counts: $counts")

    // Ingredients not associated with any allergen:
    println(ingredients.filter { ing -> allergens.values.none { a -> a.contains(ing) } }.mapNotNull { ing -> counts[ing] }.sum())

    // Go through all sets, find one with only 1 ingredient in it, remove that one from all other sets, repeat
    while (allergens.values.any { it.size > 1 }) {
        allergens.filter { it.value.size == 1 }.forEach { e ->
            val uniqueIngredient = e.value.first()
            allergens.filter { it.value.size > 1 }.forEach {e2 -> allergens[e2.key]?.remove(uniqueIngredient)}
        }
    }

    val result = allergens.keys.sorted().mapNotNull { alg -> allergens[alg]?.first() }.joinToString(",")

    println(result)


}