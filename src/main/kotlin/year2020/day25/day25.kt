package year2020.day25

import util.chineseRemainder
import util.measureTime
import util.primeFactors
import util.rangeTo
import java.math.BigInteger

// Finds k: b^k = a mod m
// Uses the naive implementation,
fun discreteLogarithmNaive(b: BigInteger, a: BigInteger, m: BigInteger): BigInteger {
    // Try raising b to each integer in (1 .. m - 1)
    var i = BigInteger.ONE
    while (i < m) {
        if (b.modPow(i, m) == a) {
            return i
        }
        i += BigInteger.ONE
    }
    error("Failed to find discrete logarithm for $b === $a mod $m")
}

// Finds k: b^k = a mod m
// Uses the baby-steps / giant-steps algorithm: https://en.wikipedia.org/wiki/Baby-step_giant-step
fun babyStepsGiantSteps(b: BigInteger, a: BigInteger, modulo: BigInteger): BigInteger {
    val root = modulo.sqrt() + BigInteger.ONE

    val table = mutableMapOf<BigInteger, BigInteger>()

    var i = BigInteger.ZERO
    while (i <= root) {
        table[b.modPow(i, modulo)] = i
        i += BigInteger.ONE
    }

    val c = b.modPow(root * (modulo - BigInteger.valueOf(2)), modulo)

    var j = BigInteger.ZERO
    while (j < root) {
        //  y = (a * pow(c, j, p)) % p
        val y = (a * c.modPow(j, modulo)).mod(modulo)
        if (table.contains(y)) {
            // return j * root + t[y]
            return j * root + table[y]!!
        }
        j += BigInteger.ONE
    }

    error("Failed to find logarithm using baby-step/giant-step")

}


fun pohligHellman(b: BigInteger, a: BigInteger, modulo: BigInteger): BigInteger {
    val q = modulo - BigInteger.ONE
    val factors = q.primeFactors()
    val logs = mutableListOf<BigInteger>()

    for(qe in factors) {
        val me = qe.first.pow(qe.second)
        val g = b.modPow(modulo / me, modulo)
        val h = a.modPow(modulo / me, modulo)

        // Find a discrsete logarithm for this factor
        val f = babyStepsGiantSteps(g, h, modulo) % me
        //println("Small log of($g, $h, ${modulo}) is $f")

        logs += f
    }

    val log = chineseRemainder(factors.map { qe -> qe.first.pow(qe.second) }, logs)

    return log


}

fun main() {

    val cardPk = BigInteger.valueOf(1327981)
    val doorPk = BigInteger.valueOf(2822615)
    val b = BigInteger.valueOf(7)

    val modulo = BigInteger.valueOf(20201227)

    measureTime {
        val cardK = pohligHellman(b, cardPk, modulo)
        println(doorPk.modPow(cardK, modulo))
    }

    measureTime {
        val cardK =  babyStepsGiantSteps(b, cardPk, modulo)
        println(doorPk.modPow(cardK, modulo))
    }

    measureTime {
        val cardK =  discreteLogarithmNaive(b, cardPk, modulo)
        println(doorPk.modPow(cardK, modulo))
    }

}