package year2021.day21

data class Cycle(private val seed: Int, private val modulo: Int, private var value: Int = seed - 1, private var steps: Int = 0) {

    // The result is supposed to start from one, not zero, we adjust this internally
    fun take(n: Int): List<Int> {
        steps += n
        val values = 0.until(n).map {
            (this.value + 1).also {
                this.value = (this.value + 1) % modulo
            }
        }
        return values
    }

    fun peek() = value + 1

    fun steps() = steps

}

data class Player(private val name: String, private val start: Int, var score: Int = 0, private val board: Cycle = Cycle(start, 10)) {
    fun roll(dice: Cycle) {

        val rolls = dice.take(3)
        board.take(rolls.sum())
        val position = board.peek()
        score += position
        println("$name rolls $rolls and moves to space $position for a total score of $score")
    }
}

fun main() {

    var dice = Cycle(1, 100)

    val p1 = Player("Player 1", 3)
    val p2 = Player("Player 2", 7)

    var p1HasTurn = true

    while(p1.score < 1000 && p2.score < 1000) {
        p1HasTurn = if(p1HasTurn) {
            p1.roll(dice)
            false
        } else {
            p2.roll(dice)
            true
        }
    }

    println("Player 1: ${p1.score}:${p1.score * dice.steps()}, Player 2: ${p2.score}:${p2.score * dice.steps()}, Dice: $dice")
}

