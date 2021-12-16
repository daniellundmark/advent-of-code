package year2021.day16


import util.readInput

data class ResultAndNewIndex<T>(val result: T, val newIndex: Int)

enum class PackageType {
    SUM, PRODUCT, MIN, MAX, LITERAL, GT, LT, EQ
}

data class Header(val version: Int, val id: PackageType)
const val HEADER_LENGTH = 6

sealed interface BitsPackage {
    val versions: Int
    val value: Long
}

data class Literal(
    val header: Header,
    val number: Long) : BitsPackage {
    override val versions get(): Int = header.version
    override val value get() = number
}

data class Operator(
    val header: Header,
    val packets: MutableList<BitsPackage> = mutableListOf()
): BitsPackage {
    override val versions get(): Int = header.version + packets.sumOf { it.versions }

    override val value get(): Long {
        return when(header.id) {
            PackageType.LITERAL -> error("Operator should not be literal")
            PackageType.SUM -> packets.sumOf { it.value }
            PackageType.PRODUCT -> packets.fold(1L){a, b -> a*b.value}
            PackageType.MIN -> packets.minOf { it.value }
            PackageType.MAX -> packets.maxOf { it.value }
            PackageType.GT -> if(packets[0].value > packets[1].value) 1 else 0
            PackageType.LT -> if(packets[0].value < packets[1].value) 1 else 0
            PackageType.EQ -> if(packets[0].value == packets[1].value) 1 else 0
        }
    }
}

fun expand(hex: Char): String {
    return hex.digitToInt(16).toString(2).padStart(4, '0')
}
fun expand(hex: String): String {
    return hex.map { expand(it) }.joinToString(separator = "")
}

fun parseHeader(binary: String, idx: Int): Header {
    return Header(
        version = binary.substring(idx.until(idx+3)).toInt(2),
        id = PackageType.values()[binary.substring((idx+3).until(idx+6)).toInt(2)]
    )
}

fun parseLiteral(header: Header, binary: String, start: Int): ResultAndNewIndex<Literal> {

    var idx = start
    var lastPackage = false
    var numberString = ""

    while(!lastPackage){
        lastPackage = binary[idx] == '0'
        numberString += binary.substring((idx+1).until(idx+5))
        idx+=5
    }
    val number = numberString.toLong(2)

    return ResultAndNewIndex(Literal(header, number), idx)
}

fun parseOperator(header: Header, binary: String, start: Int): ResultAndNewIndex<Operator> {

    var packages = mutableListOf<BitsPackage>()
    var idx: Int

    val parseByLength = binary[start] == '0'
    if(parseByLength) {
        val length = binary.substring((start+1).until(start+1+15)).toInt(2)
        idx = start+15+1
        while(idx < start+15+length) {
            val res = parsePackage(binary, idx)
            idx = res.newIndex
            packages.add(res.result)
        }
    } else {
        val numberOfChildren = binary.substring((start+1).until(start+1+11)).toInt(2)
        idx = start+1+11
        while(packages.size < numberOfChildren) {
            val res = parsePackage(binary, idx)
            idx = res.newIndex
            packages.add(res.result)
        }
    }

    return ResultAndNewIndex(Operator(header, packages), idx)
}

fun parsePackage(binary: String, start: Int = 0): ResultAndNewIndex<out BitsPackage> {
    var idx = start
    val header = parseHeader(binary, idx)
    idx += HEADER_LENGTH
    return if(header.id == PackageType.LITERAL) {
        parseLiteral(header, binary, idx)
    } else {
        parseOperator(header, binary, idx)
    }
}


fun main() {
    val input = readInput("year2021/day16.input").first()

    val (pkg) = parsePackage(expand(input))
    println("Package: $pkg")

    println("Part 1: " + pkg.versions)
    println("Part 2: " + pkg.value)

}