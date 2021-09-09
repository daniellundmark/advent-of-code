package year2020.day16

import util.multiply
import util.readInput
import util.rest
import util.split

data class Field(val name: String, val low: IntRange, val high: IntRange) {
    fun validFor(value: Int) = low.contains(value) || high.contains(value)

    companion object {
        private val regex = "([\\w ]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)".toRegex()
        fun parse(string: String): Field? = regex.matchEntire(string)
                ?.destructured
                ?.let { (name, a, b, c, d) -> Field(name, a.toInt() .. b.toInt(), c.toInt() .. d.toInt()) }
    }
}

class Ticket {

    private val matchingFields: Map<Int, List<Field>>
    private val fields: List<Field>
    val numbers: List<Int>

    constructor(numbers: List<Int>, fields: List<Field>) {
        this.numbers = numbers
        this.fields = fields

        this.matchingFields = getMatchingFields()
    }

    private fun getMatchingFields(): Map<Int, List<Field>> {
       val possibleMatches = mutableMapOf<Int, MutableList<Field>> ()

        // Check which fields possibly match each position on the ticket
        numbers.forEachIndexed { i, n ->
            possibleMatches[i] = mutableListOf<Field>()
            fields.forEach { field ->
                if(field.validFor(n)) {
                    possibleMatches[i]?.add(field)
                }
            }
        }

        return possibleMatches
    }

    fun fieldMatches(index: Int, field: Field) = (this.matchingFields[index] ?: error("Should have had matching fields")).contains(field)

    override fun toString(): String {
        return "$numbers: ${this.matchingFields.map {e -> "${numbers[e.key]}:${e.value.map { it.name }}"}}"
        //return "$numbers: ${this.matchingFields}"
    }

    fun valid() = this.matchingFields.values.all { it.isNotEmpty() }
}

fun main() {

    val (r, y, nearby) = readInput("year2020/day16.input").split (String::isBlank)

    val fields = r.mapNotNull (Field.Companion::parse)

    println("Fields")
    fields.forEach(::println)

    val tickets = nearby.rest().map { Ticket(it.split(",").map(String::toInt), fields ) }

    //println(tickets)
    val validTickets = tickets.filter{it.valid()}
    println("Valid tickets")
    validTickets.forEach(::println)



    // Take the indexes and find names that are OK for all valid tickets there
    val indices = validTickets.first().numbers.indices
    val possibleFieldsForEachPosition = indices.map { i ->
        fields.filter { field ->
            // Check if this field is valid for all tickets in position i
            val valid = validTickets.map { ticket -> ticket.fieldMatches(i, field) }.all { it }
            println("$i $field $valid")
            valid
        }.toMutableList()
    }

    println("Possible fields for each position")
    possibleFieldsForEachPosition.forEachIndexed{index, possible ->
        println("$index: ${possible.map { it.name }}")
    }

    // Now we know for each index what the possible fields are
    // For each field, pick an index if the field is the only one allowed there, then remove it from the other possible sets
    val determined = mutableMapOf<Int, Field>()

    while(determined.values.size < fields.size) {
        val firstLonelyIndex = possibleFieldsForEachPosition.indexOfFirst {fields -> fields.size == 1 }
        val firstLonelyField = possibleFieldsForEachPosition[firstLonelyIndex].first()
        determined[firstLonelyIndex] = firstLonelyField

        // Remove this from the remaining possibilities
        possibleFieldsForEachPosition.forEach { if(it.contains(firstLonelyField)) it.remove(firstLonelyField) }
    }

    println("Determined")
    println(determined.map { (index, field) -> "$index: ${field.name}" })

    val yourTicket = Ticket(y.rest().first().split(",").map(String::toInt), fields)

    val departures = determined.filter { it.value.name.startsWith("departure") }
    println("Departures: $departures")

    println("In your ticket")
    println(yourTicket)

    val fromTicket = departures.map { entry ->
        val fieldFromTicket = yourTicket.numbers[entry.key]
        println("$fieldFromTicket ${entry.value.name}")
        fieldFromTicket
    }
    println(fromTicket.map(Int::toLong).multiply())


}