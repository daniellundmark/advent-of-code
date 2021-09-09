package year2019.day12

import kotlinx.coroutines.runBlocking
import util.leastCommonMultiple
import util.measureTime
import util.multiply
import util.readInput
import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "<x=%3d, y=%3d, z=%3d>".format(x, y, z)
    }
}

data class Moon(val name: String, var pos: Point, var vel: Point = Point(0, 0, 0)) {

    fun adjustVelocity(other: Moon) {
        this.vel = Point(
            x = this.vel.x + other.pos.x.compareTo(this.pos.x),
            y = this.vel.y + other.pos.y.compareTo(this.pos.y),
            z = this.vel.z + other.pos.z.compareTo(this.pos.z)
        )
    }

    fun adjustPosition() {
        this.pos = Point(
            x = this.pos.x + this.vel.x,
            y = this.pos.y + this.vel.y,
            z = this.pos.z + this.vel.z
        )
    }

    fun pot(): Int {
        val (x, y, z) = pos
        return abs(x) + abs(y) + abs(z)
    }

    fun kin(): Int {
        val (x, y, z) = vel
        return abs(x) + abs(y) + abs(z)
    }

    fun tot() = pot() * kin()

}

fun println(iteration: Long, moons: Collection<Moon>) {
    println("After $iteration steps:")
    for (moon in moons) {
        println("%8s: pos=${moon.pos}, vel=${moon.vel}".format(moon.name))
    }
    println()
}

fun main()  {

    val input = readInput("year2019/day12.input")
    val names = listOf("Io", "Europa", "Ganymede", "Callisto")
    val regex = "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>".toRegex()
    val moons = names.indices.mapNotNull { i ->
        regex.matchEntire(input[i])?.destructured?.let { (x, y, z) ->
            Moon(names[i], Point(x.toInt(), y.toInt(), z.toInt()))
        }
    }

    val seen = mutableMapOf<List<Pair<Int, Int>>, Long>()
    seen[moons.map { Pair(it.pos.x, it.vel.x) }] = 0
    seen[moons.map { Pair(it.pos.y, it.vel.y) }] = 0
    seen[moons.map { Pair(it.pos.z, it.vel.z) }] = 0

    println(0, moons)

    measureTime {

        // Find cycle lengths for x, y, z
        val cycles = mutableMapOf<String, Pair<Long, Long>>()
        var i = 1L
        while(i < 1_000_000L) {

            for (m in moons.indices) {
                for (n in m + 1 until moons.size) {
                    moons[m].adjustVelocity(moons[n])
                    moons[n].adjustVelocity(moons[m])
                }
            }
            moons.forEach(Moon::adjustPosition)

            //println(i, moons)

            val x = moons.map { Pair(it.pos.x, it.vel.x) }
            val y = moons.map { Pair(it.pos.y, it.vel.y) }
            val z = moons.map { Pair(it.pos.z, it.vel.z) }

            if(cycles["x"] == null && seen[x] != null){
                println("$i: cycle found for x from ${seen[x]}")
                cycles["x"] = Pair(seen[x]!!, i)
            }
            if(cycles["y"] == null && seen[y] != null){
                println("$i: cycle found for y from ${seen[y]}")
                cycles["y"] = Pair(seen[y]!!, i)
            }
            if(cycles["z"] == null && seen[z] != null){
                println("$i: cycle found for z from ${seen[z]}")
                cycles["z"] = Pair(seen[z]!!, i)
            }

            if (cycles.size == 3) {
                println("All cycle lengths found: $cycles")
                println("LCM: ${leastCommonMultiple(cycles.values.map { it.second })}")
                break
            }

            if(i == 100L || i == 1000L) {

                println(i, moons)

                println("Energy after $i steps:")
                moons.forEach { println("pot: ${it.pot()}; \tkin: ${it.kin()}; \ttot: ${it.tot()}") }
                println("Sum of total energy: ${moons.sumBy { it.tot() }}")
                println()
            }

            seen[x] = i
            seen[y] = i
            seen[z] = i

            i++

        }

        println("Finished $i iterations")

    }





}