package util

fun main() {

    val graph = Graph<Int>()

    graph.addBidirectionalEdge(0, 1)
    graph.addBidirectionalEdge(0, 4)
    graph.addBidirectionalEdge(1, 4)

    graph.addBidirectionalEdge(4, 3)

    graph.addBidirectionalEdge(3, 2)
    graph.addBidirectionalEdge(3, 5)
    graph.addBidirectionalEdge(5, 2)

    graph.addBidirectionalEdge(5, 6)

    graph.addBidirectionalEdge(6, 7)
    graph.addBidirectionalEdge(6, 8)
    graph.addBidirectionalEdge(7, 8)

    println(graph)

    val path = graph.bfs(0, 8)
    println(path)

}