package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph


// This code has been generated with the assistance of ChatGPT
data class AddressPoc(val street: String, val city: String)

class NodePoc(val address: AddressPoc) {
    val neighbors = mutableListOf<NodePoc>()
}

class GraphPoc {
    private val nodes = mutableSetOf<NodePoc>()

    fun addNode(node: NodePoc) {
        nodes.add(node)
    }

    fun connect(node1: NodePoc, node2: NodePoc) {
        node1.neighbors.add(node2)
        node2.neighbors.add(node1)
    }

    fun findConnectedComponents(): List<Set<NodePoc>> {
        val visited = mutableSetOf<NodePoc>()
        val components = mutableListOf<Set<NodePoc>>()

        for (node in nodes) {
            if (node !in visited) {
                val component = mutableSetOf<NodePoc>()
                dfs(node, visited, component)
                components.add(component)
            }
        }
        return components
    }

    private fun dfs(node: NodePoc, visited: MutableSet<NodePoc>, component: MutableSet<NodePoc>) {
        if (node in visited) return
        visited.add(node)
        component.add(node)
        for (neighbor in node.neighbors) {
            dfs(neighbor, visited, component)
        }
    }
}

// Example Usage
fun main() {
    val graph = GraphPoc()

    val node1 = NodePoc(AddressPoc("123 A St", "CityX"))
    val node2 = NodePoc(AddressPoc("456 B St", "CityX"))
    val node3 = NodePoc(AddressPoc("789 C St", "CityX"))
    val node4 = NodePoc(AddressPoc("321 D St", "CityY"))
    val node5 = NodePoc(AddressPoc("654 E St", "CityY"))
    val node6 = NodePoc(AddressPoc("133 E St", "CityZ"))

    graph.addNode(node1)
    graph.addNode(node2)
    graph.addNode(node3)
    graph.addNode(node4)
    graph.addNode(node5)
    graph.addNode(node6)

    graph.connect(node1, node2)
    graph.connect(node2, node3)
    graph.connect(node4, node5)

    val components = graph.findConnectedComponents()

    println("Connected Components:")
    for ((index, component) in components.withIndex()) {
        println("Group $index:")
        component.forEach { println(" - ${it.address}") }
    }
}

// This code has been generated with the assistance of ChatGPT
