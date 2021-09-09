package year2020.day22

import util.measureTime
import util.readInput
import util.split

fun iterativePart1() {
    val (l1, l2) = readInput("year2020/day22.input").split(String::isBlank)
    val p1 = ArrayDeque(l1.drop(1).map(String::toInt))
    val p2 = ArrayDeque(l2.drop(1).map(String::toInt))

    var round = 1
    while (p1.isNotEmpty() && p2.isNotEmpty() && round < 3000) {

        //println("-- Round ${round++} --")
        //println("Player 1's deck: $p1")
        //println("Player 2's deck: $p2")

        val c1 = p1.removeFirst()
        val c2 = p2.removeFirst()

        //println("Player 1 plays: $c1")
        //println("Player 2 plays: $c2")

        if (c1 > c2) {
            //println("Player 1 wins the round!")
            p1.addLast(c1)
            p1.addLast(c2)
        } else {
            //println("Player 2 wins the round!")
            p2.addLast(c2)
            p2.addLast(c1)
        }
    }

    //println("== Post-game results ==")
    //println("Player 1's deck $p1")
    //println("Player 2's deck $p2")

    println(p1.reversed().reduceIndexedOrNull { index, acc, cur -> acc + (index + 1) * cur })
    println(p2.reversed().reduceIndexedOrNull { index, acc, cur -> acc + (index + 1) * cur })
}

typealias Deck = ArrayDeque<Int>
var roundCounter = 0
var gameCounter = 0

tailrec fun recursiveCombat(playSubGames: Boolean, round: Int, game: Int, deck1: Deck, deck2:Deck, tieBreaker: MutableSet<Pair<Deck,Deck>>): Pair<Boolean, Deck> {

    //println("-- Round ${round.second} (Game ${round.first}) --")
    //println("   Player 1's deck: $deck1")
    //println("   Player 2's deck: $deck2")

    roundCounter++

    // Infinite recursion breaker
    if(tieBreaker.contains(Pair(Deck(deck1), Deck(deck2)))) {
        return Pair(true, deck1)
    }
    tieBreaker.add(Pair(deck1, deck2))

    if( deck1.isEmpty()) {
        return Pair(false, deck2)
    } else if(deck2.isEmpty()) {
        return Pair(true, deck1)
    }

    val c1 = deck1.removeFirst()
    val c2 = deck2.removeFirst()
    val p1Wins: Boolean

    // See if we should play a sub game
    if(playSubGames && c1 <= deck1.size && c2 <= deck2.size) {
        //println("---> Starting new recursive game ${gameCounter + 1}")
        val sub = recursiveCombat(playSubGames, 1, gameCounter++, Deck(deck1.subList(0, c1)), Deck(deck2.subList(0, c2)), mutableSetOf())
        p1Wins = sub.first
    } else {
        p1Wins = c1 > c2
    }

    if (p1Wins) {
       // println("   Player 1 wins the round!")
        deck1.addLast(c1)
        deck1.addLast(c2)
    } else {
        //println("   Player 2 wins the round!")
        deck2.addLast(c2)
        deck2.addLast(c1)
    }

    return recursiveCombat(playSubGames, round+1, game, deck1, deck2, tieBreaker)
}

fun main() {

    val (l1, l2) = readInput("year2020/day22.input").split(String::isBlank)
    val deck1 = Deck(l1.drop(1).map(String::toInt))
    val deck2 = Deck(l2.drop(1).map(String::toInt))

    measureTime {
        println("Iterative Part 1")
        iterativePart1()
    }

    println("--------------------")

    measureTime {
        println("Tail-recursive Part 1")
        roundCounter = 0
        val win = recursiveCombat(false, 1, gameCounter++, Deck(deck1), Deck(deck2), mutableSetOf())
        println("Played $roundCounter rounds in total")
        println(win.second.reversed().reduceIndexedOrNull { index, acc, cur -> acc + (index + 1) * cur })
    }

    println("--------------------")

    measureTime {
        println("Tail-recursive Part 2")
        roundCounter = 0
        gameCounter = 0
        val win = recursiveCombat(true, 1, gameCounter++, Deck(deck1), Deck(deck2), mutableSetOf())
        println("Played $roundCounter rounds in total over $gameCounter games")
        println(win.second.reversed().reduceIndexedOrNull { index, acc, cur -> acc + (index + 1) * cur })

    }
}