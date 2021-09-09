package year2019.day03

import util.readInput
import util.rest
import kotlin.math.abs


data class Point(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x,$y)"
    }
}

data class Line(val a: Point, val b: Point) {

    override fun toString(): String {
        return "$a->$b"
    }

    fun isHorizontal() = a.y == b.y
    fun isVertical() = a.x == b.x

    fun rangeX() = (firstX().x .. secondX().x)
    fun rangeY() = (firstY().y .. secondY().y)

    fun firstX() = if(a.x < b.x) a else b
    fun secondX() = if(a.x < b.x) b else a

    fun firstY() = if(a.y < b.y) a else b
    fun secondY() = if(a.y < b.y) b else a

    // Rules:
    //  Two parallel lines never intersect (for this use case)
    //  Otherwise, one's x need to be in the other's xRange and vice versa for y
    fun intersects(other: Line): Boolean {

        return if(this.isHorizontal() && other.isVertical()) {
            other.a.x in this.rangeX()
                    && this.a.y in other.rangeY()
        } else if(this.isVertical() && other.isHorizontal()) {
            other.a.y in this.rangeY()
                    && this.a.x in other.rangeX()
        } else if((this.isHorizontal() && other.isHorizontal()) || (this.isVertical() && other.isVertical())) {
            //error("Can only get intersections of straight orthogonal lines")
            false
        } else {
            false
        }
    }

    fun intersection(other: Line): Point? {
        return if(!this.intersects(other)) {
            return null
        }
        else if(this.isHorizontal() && other.isVertical()) {
            Point(
                x = this.rangeX().first { other.a.x == it  },
                y = this.a.y
            )
        } else if(this.isVertical() && other.isHorizontal()) {
            Point(
                x = this.a.x,
                y = this.rangeY().first { other.a.y == it  },
            )
        } else if((this.isHorizontal() && other.isHorizontal()) || (this.isVertical() && other.isVertical())) {
            //error("Can only get intersections of straight orthogonal lines")
            null
        } else {
            null
        }
    }

    // Only one of these will be non zero for straight lines
    fun length() = secondX().x - firstX().x + secondY().y - firstY().y

    fun contains(p: Point) = p.x in (firstX().x..secondX().x) && p.y in (firstY().y..secondY().y)
}

fun point(from: Point, input: String): Point {
    val regex = "(\\w)(\\d+)".toRegex()
    return regex.matchEntire(input)?.destructured?.let { (dir, length) ->
        when (dir) {
            "U" -> Point(from.x, from.y + length.toInt())
            "D" -> Point(from.x, from.y - length.toInt())
            "L" -> Point(from.x - length.toInt(), from.y)
            "R" -> Point(from.x + length.toInt(), from.y)
            else -> error("Wrong direction in input: $input")
        }
    } ?: error("Failed to get new point from input: $input")
}

fun List<Point>.toLines(): List<Line> {
    return (1 until this.size).map {
        Line(this[it-1], this[it])
    }
}

fun List<Line>.distanceTo(p: Point): Int {
    var distance = 0
    for(line in this) {
        if(!line.contains(p)) {
            distance += line.length()
        } else {
            // Now add a part of the line which contains the point
            distance += abs(line.a.x - p.x)
            distance += abs(line.a.y - p.y)
            break
        }
    }
    return distance
}

fun main() {

    val (a, b) = readInput("year2019/day03.input").map { line ->
        line.split(",").scan(Point(0,0)){acc, input -> point(acc, input)}.toLines()
    }

    val intersections = a.flatMap { aa -> b.map { bb -> aa.intersection(bb) } }.filterNotNull()
    println(intersections)

    println("Part1: "+intersections.drop(1).map { abs(it.x) + abs(it.y) }.minOrNull())

    val da = intersections.map { a.distanceTo(it) }
    val db = intersections.map { b.distanceTo(it) }

    println(da)
    println(db)
    val dSum = da.zip(db){a, b -> a+b}.sorted()
    println(dSum)

    println("Part2: "+dSum[0])

}