package year2019.day14

import util.readInput
import kotlin.math.ceil

fun roundUp(num: Long, divisor: Long): Long {
    return (num + divisor - 1) / divisor
}

fun <K> MutableMap<K, Long>.add(key: K, value: Long)  {
    val old = this[key]
    if(old == null) {
        this[key] = value
    } else {
        this[key] = old + value
    }
}

data class Material(val number: Long, val chemical: Chemical)

typealias Chemical = String

fun parseMaterial(string: String) = "(\\d+) (\\w+)".toRegex().let { re -> re.matchEntire(string)?.destructured?.let { (number, chemical) -> Material(
    number.toLong(),
    chemical
)
} }


data class Inventory(
    var consumedOre: Long,
    var producedFuel: Long,
    val surplus: MutableMap<Chemical, Long>,
    val missing: MutableMap<Chemical, Long>
)

// Take an inventory and produce/consume material to put the target number of fuels into the inventory
fun produce(reactions: Map<Material, List<Material>>, fuel: Long, inventory: Inventory): Inventory {

    // Add FUEL as missing material, and into the inventory
    inventory.missing.add("FUEL", fuel)

    // Pick one missing material, create it in a reaction, put the extra produced in surplus
    // If ORE is missing, just increase the counter instead
    while(inventory.missing.isNotEmpty()) {

        // Pick one missing material
        var (chemical, need) = inventory.missing.entries.first()
        inventory.missing.remove(chemical)

        //println("Figuring out how to produce $need $chemical")

        // Look up the recipe
        val (recipeTarget, ingredients) = reactions.entries.find { it.key.chemical == chemical }?.toPair()
            ?: error("Failed to find recipe")

        // Use up any surplus of this chemical
        val srp = inventory.surplus[chemical] ?: 0L
        val surplusToUse = if (srp > need) need else srp
        inventory.surplus[chemical] = srp - surplusToUse
        need -= surplusToUse

        // If we had enough as surplus, don't produce more
        if (need == 0L) {
            continue
        }

        // How many times to use the recipe
        val repetitions = roundUp(need, recipeTarget.number)

        // Use the recipe, place the ingredients as missing
        ingredients.forEach { mat ->
            inventory.missing.add(mat.chemical, mat.number * repetitions)
        }
        need -= (recipeTarget.number * repetitions)

        // Add any extra as surplus
        inventory.surplus.add(recipeTarget.chemical, -need) // need is negative when we have extra

        // Clean up, remove ORE as missing and just add them to the counter
        val missingOre = inventory.missing["ORE"]
        if (missingOre != null) {
            inventory.missing.remove("ORE")
            inventory.consumedOre += missingOre
            //println("We have now used ${inventory.consumedOre} Ore")
        }

        //println("List of missing ingredients: ${inventory.missing}, surplus: ${inventory.surplus}\n")

    }

    inventory.producedFuel += fuel

    return inventory

}

fun oreForXFuel(reactions: Map<Material, List<Material>>, fuel: Long): Long {
    // Initial inventory
    var inventory = Inventory(
        0,
        0,
        mutableMapOf<String, Long>().withDefault { 0L },
        mutableMapOf<String, Long>().withDefault { 0L })

    inventory = produce(reactions, fuel, inventory)

    return inventory.consumedOre
}

fun main() {

    val lines = readInput("year2019/day14.input")


    val reactions = lines.mapNotNull { line ->
        val (lhs, rhs) = line.split("=>")

        val from = lhs.split(",").mapNotNull { parseMaterial(it.trim()) }
        val to = parseMaterial(rhs.trim())!!

        to to from
    }.toMap()


    // Initial inventory
    val oreForOneFuel = oreForXFuel(reactions, 1)

    println("Ore for one fuel: $oreForOneFuel")

    // Now try to pin-point how much fuel 1000000000000 ore can produce

    // Find an interval where the target should be
    var target = 1000000000000L
    var a = target/oreForOneFuel
    var b = 10*a

    var i = 0
    var max = 100

    var aValue = oreForXFuel(reactions, a)
    var bValue = oreForXFuel(reactions, b)

    while(i++ < max) {


        var mid = (a + b) / 2

        val midValue = oreForXFuel(reactions, mid)

        println("$i: Trying with $a, $mid, $b, midValue is $midValue")

        if(midValue == target || (b == a+1L)) {
            println("Done after $i iterations: $a=>$aValue, $mid=>$midValue, $b=>$bValue")
            break
        }

        if(midValue < target) {
            a = mid
            aValue = midValue
        } else {
            b = mid
            bValue = midValue
        }
    }



}