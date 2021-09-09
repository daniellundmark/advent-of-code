package year2019.day08

import util.readInput

fun main() {
    val width = 25
    val height = 6

    val layers = readInput("year2019/day08.input")
        .first()
        .map { when(it){
            '1' -> true
            '0' -> false
            else -> null
        } }
        .chunked(width*height)

    //println(layers)

    // Merge the layers to first non-null value
    val image = mutableListOf<Boolean>()

    for(i in 0 until (width*height)) {
        image += layers.first { it[i] != null }[i]!!
    }


    println(" "+ List(width){"-"}.joinToString("")+" ")
    image.map { if(it) '#' else ' ' }.chunked(width).forEach{
        print("|")
        print(it.joinToString(""))
        println("|")
    }
    println(" "+ List(width){"-"}.joinToString("")+" ")


}