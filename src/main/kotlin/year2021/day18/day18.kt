package year2021.day18

import util.measureTime
import util.readInput

class Token(val depth: Int, var text: String) {
    constructor(depth: Int, text: Char) : this(depth, text.toString())

    fun needsExplode() = text == "[" && depth >= 4
    fun needsSplit() = (text.toIntOrNull() ?: -1)  > 9
    fun isNumber() = text.toIntOrNull() != null
    fun toNumber() = text.toInt()
    fun setNumber(number: Int) { this.text = number.toString() }

    override fun toString(): String = text
}

sealed interface Node
class Pair(val parent: Pair?, var left: Node?, var right: Node?): Node {
    fun addChild(child: Node) {
        if(left == null) {
            left = child
        } else if(right == null) {
            right = child
        } else {
            error("Attempt to add third child $child to $this")
        }
    }
}
class Leaf(val parent: Pair?, val number: Int): Node

fun parseTokens(line: String): MutableList<Token> {
    val list = mutableListOf<Token>()
    var depth = 0
    var buf = ""
    line.forEach { token ->
        when(token) {
            '[' -> {  list.add(Token(depth, token)); buf = ""; depth++;}
            ',' -> { if(buf.isNotEmpty()){list.add(Token(depth, buf))}; list.add(Token(depth, token)); buf = ""; }
            in ('0'..'9') -> { buf += token }
            ']' -> { if(buf.isNotEmpty()){list.add(Token(depth, buf))}; depth--; buf = "";   list.add(Token(depth, token)); }
            else -> error("Unexpected token: $token")
        }
    }
    return list
}

fun parseTree(tokens: List<Token>): Pair {
    var current: Pair? = null
    tokens.forEachIndexed { idx, token ->
        when {
            token.isNumber() -> {
                val leaf = Leaf(parent = current, number = token.toNumber())
                current!!.addChild(leaf)
            }
            token.text == "[" -> {
                current = Pair(parent = current, left = null, right = null)
            }
            token.text == "]" -> {
                val prev = current!!
                if(prev.parent != null) {
                    current = prev.parent
                    current!!.addChild(prev)
                } else if(idx < tokens.size - 1) {
                    error("Missing parent before end of tokens")
                }
            }
            else -> {}
        }
    }
    return current!!
}

fun MutableList<Token>.explodeOne(): Boolean {

    // Find the first left parenthesis that needs explode
    val startIdx = this.indexOfFirst { it.needsExplode() }

    if(startIdx < 0) return false

    // Find the right parenthesis after it (it is always 5 tokens) [N,M]
    val endIdx =  startIdx + 4

    val depth = this[startIdx].depth

    // Explode the numbers to the left and right
    val leftNumber = this.subList(0, startIdx-1).findLast { it.isNumber() }
    leftNumber?.let { it.setNumber(it.toNumber() + this[startIdx+1].toNumber()) }

    val rightNumber = this.subList(endIdx, this.size).firstOrNull { it.isNumber() }
    rightNumber?.let { it.setNumber(it.toNumber() + this[endIdx-1].toNumber()) }

    // Remove this pair from the list
    (startIdx..endIdx).forEach { _ -> this.removeAt(startIdx) }

    // Insert a zero
    this.add(startIdx, Token(depth, "0"))

    return true
}

fun MutableList<Token>.splitOne(): Boolean {
    val idx = this.indexOfFirst { it.needsSplit() }

    if(idx < 0) return false

    // Remove this token
    val split = this.removeAt(idx)

    val number = split.toNumber()
    val left = number / 2
    val right = number - left

    // Add a new pair instead
    this.add(idx, Token(split.depth, "["))
    this.add(idx+1, Token(split.depth+1, left.toString()))
    this.add(idx+2, Token(split.depth+1, ","))
    this.add(idx+3, Token(split.depth+1, right.toString()))
    this.add(idx+4, Token(split.depth, "]"))

    return true
}
fun MutableList<Token>.reduce(): MutableList<Token> {
    while(this.explodeOne() || this.splitOne());
    return this
}


fun MutableList<Token>.addition(other: List<Token>): MutableList<Token> {
    return (listOf(Token(this.first().depth, "[")) +
            this.map { Token(it.depth+1, it.text) } +
            Token(this.first().depth+1, ",") +
            other.map { Token(it.depth+1, it.text) } +
            Token(this.first().depth, "]")).toMutableList()
        .reduce()
}

fun List<Token>.toDisplayString() = this.joinToString(separator = "") { it.text }

fun Node.magnitude(): Long {
    return when(this) {
        is Leaf -> this.number.toLong()
        is Pair -> (3 * left!!.magnitude()) + (2 * right!!.magnitude())
    }
}

fun List<Token>.magnitude() = parseTree(this).magnitude()

fun main() {
    val numbers = readInput("year2021/day18.input").map { parseTokens(it) }

    measureTime {
        val sum = numbers.reduce{a,b -> a.addition(b) }
        println("Part 1: ${sum.magnitude()}")
    }

    measureTime {
        val sums = numbers.flatMap { x ->
            numbers.filterNot { it == x }.map { y ->
                x.addition(y).magnitude()
            }
        }
        println("Part 2: ${sums.maxOf { it }}")
    }

}