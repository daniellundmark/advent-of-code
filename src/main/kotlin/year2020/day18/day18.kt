package year2020.day18

import util.*


fun rpn(terms: List<Char>): Long {

    val stack: Stack<Long> = mutableListOf()

    for(term in terms) {
        stack += if (term.isDigit()) {
            Character.getNumericValue(term).toLong()
        } else { // Must be operator
            // Pop 2 from stack and combine with operator
            when(term) {
                '+' -> stack.pop() + stack.pop()
                '-' -> stack.pop() - stack.pop()
                '*' -> stack.pop() * stack.pop()
                else -> error("Unsupported operator: $term")
            }
        }
    }

    return stack.pop()
}

fun Char.isOperator() = listOf('+', '-', '*', '(', ')').contains(this)
fun Char.precedence() = when(this) {
    ')' -> 4
    '(' -> 2
    '+' -> 1
    '-' -> 1
    '*' -> 0
    else -> error("Unsupported precedence")
}

fun Stack<Char>.hasOperator(): Boolean =
        this.isNotEmpty() && this.peek() != '('

fun shuntingYard(line: String): List<Char> {

    val stack: Stack<Char> = mutableListOf<Char>()
    val queue=  mutableListOf<Char>()

    line.forEach { char ->

        // if(char.isWhitespace()) {}
        when {
            // Digits get pushed to the output queue directly
            char.isDigit() -> queue += char

            char == '(' -> stack.push(char)
            char == ')' -> {
                while(stack.peek() != '(') queue += stack.pop()
                stack.pop()
            }

            // For operators, we first move other operators from the stack to the queue as needed
            char.isOperator() -> {
                while(stack.hasOperator() && stack.peek().precedence() > char.precedence()) {
                    queue += stack.pop()
                }
                stack += char
            }
        }
        //if(!char.isWhitespace())println("$char: stack:$stack queue:$queue")
    }

    while(stack.isNotEmpty()) {
        queue += stack.pop()!!
    }

    //println("Queue: $outputQueue")

    return queue

}

fun parse(line: String) = rpn(shuntingYard(line)).apply {
    println("$this: $line")
}


fun main() {

    val lines = readInput("year2020/day18.input")
    val results = lines.map(::parse)
    println(results.sum())

}