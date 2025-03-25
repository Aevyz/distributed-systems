package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph

import dev.lochert.ds.blockchain.Constants
import dev.lochert.ds.blockchain.address.Address

class Graph() {
    val nodes = mutableSetOf<Node>()

    fun addNode(node: Node) {
        nodes.add(node)
    }
    fun addNode(address: Address) {
        addNode(Node(address))
    }

    fun connect(node1: Node, node2: Node) {
        node1.neighbors.add(node2)
        node2.neighbors.add(node1)
    }

    fun findConnectedComponents(): List<Set<Node>> {
        val visited = mutableSetOf<Node>()
        val components = mutableListOf<Set<Node>>()

        // Make consistent for Unit Tests
        for (node in nodes) {
            if (node !in visited) {
                val component = mutableSetOf<Node>()
                dfs(node, visited, component)
                components.add(component)
            }
        }
        return components
    }

    private fun dfs(node: Node, visited: MutableSet<Node>, component: MutableSet<Node>) {
        if (node in visited) return
        visited.add(node)
        component.add(node)
        for (neighbor in node.neighbors) {
            dfs(neighbor, visited, component)
        }
    }

    fun giveConnections(): MutableSet<Address> {
        val components = findConnectedComponents()
        val rSet = mutableSetOf<Address>()

        components.forEach {
            if(it.size<=Constants.subgraphConnectionPoints){
                rSet.addAll(it.map { it.address })
            }
            else {
                if (unitTestMode) {
                    // For unit test, order alphabetically
                    rSet.addAll(it.sortedBy { it.toString() }.take(Constants.subgraphConnectionPoints).map { it.address })
                } else {
                    // Shuffle the connected nodes, take 3 elems
                    rSet.addAll(
                        it.shuffled(Constants.setRandom).take(Constants.subgraphConnectionPoints).map { it.address })
                }
            }
        }
        return rSet
    }

    override fun toString(): String {
        return "Graph(nodes=$nodes)"
    }

    companion object{
        val unitTestMode: Boolean = false

        fun printConnectedComponents(graph: Graph){

            val components = graph.findConnectedComponents()

            println("Connected Components:")
            for ((index, component) in components.withIndex()) {
                println("Group $index:")
                component.forEach { println(" - ${it.address}") }
            }
        }
    }
}