package dev.lochert.ds.blockchain.http.server.strategy.address.subgraph
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

fun Graph.toSVG(outputFile: String? = null):String {
    val nodePositions = mutableMapOf<Node, Pair<Double, Double>>()
    val centerX = 400.0
    val centerY = 400.0
    val radius = 300.0

    val nodesList = nodes.toList()
    val totalNodes = nodesList.size

    // Assign positions for each node in a circular layout
    nodesList.forEachIndexed { index, node ->
        val angle = (2 * Math.PI * index) / totalNodes
        val x = centerX + radius * cos(angle)
        val y = centerY + radius * sin(angle)
        nodePositions[node] = x to y
    }

    // Start SVG creation
    val svgHeader = """<svg xmlns="http://www.w3.org/2000/svg" width="1600" height="800" viewBox="0 0 800 800" style="font-family: Arial, sans-serif;">"""
    val svgFooter = "</svg>"

    val lines = StringBuilder()

    // Draw edges
    for (node in nodes) {
        val (x1, y1) = nodePositions[node]!!
        for (neighbor in node.neighbors) {
            val (x2, y2) = nodePositions[neighbor]!!
            lines.append("""<line x1="$x1" y1="$y1" x2="$x2" y2="$y2" stroke="black" stroke-width="2"/>""")
        }
    }

    // Draw nodes (circles)
    for (node in nodes) {
        val (x, y) = nodePositions[node]!!
        lines.append("""<circle cx="$x" cy="$y" r="20" fill="blue" stroke="black" stroke-width="2"/>""")
        lines.append("""<text x="${x + 25}" y="${y + 5}" font-size="14" fill="black">${node.address}</text>""")
    }

    // Combine everything
    val svgContent = svgHeader + lines.toString() + svgFooter

    // Save to file
    if(outputFile!=null) {
        File(outputFile).writeText(svgContent)
        println("Graph SVG saved to $outputFile")
    }
    return svgContent
}
