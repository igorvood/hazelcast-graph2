package ru.vood.hazelcastgraph2.dd2.dto.dict

class GraphBuilder(val graph: NodeInterface) {

    constructor(firstGeneratorName: String) : this(GeneratorDto(firstGeneratorName))

    constructor(firstGeneratorName: String, parent: NodeInterface) : this(GeneratorDto(firstGeneratorName, parent))

    fun build() = graph

    fun addCheck(name: String, childNodes: HashMap<String, NodeInterface> = hashMapOf()): GraphBuilder {
        graph.childrenNodes().put(name, CheckDto(name, graph, childNodes))
        return this
    }

    fun addGenerator(generatorName: String, childNodes: HashMap<String, NodeInterface> = hashMapOf()): GraphBuilder {
        graph.childrenNodes().put(generatorName, GeneratorDto(generatorName, graph, childNodes))
        return this
    }

}

val generatorNameSECOND = "SECOND"
val firstGeneratorNameROOT = "ROOT"
fun getGraph(): NodeInterface {


    val root =
            GraphBuilder(firstGeneratorNameROOT)
                    .addCheck("f1")
                    .addCheck("f2")
                    .addGenerator(generatorNameSECOND)
                    .build()
    GraphBuilder(root.childrenNodes()["f1"]!!)
            .addCheck("f3")
            .build()

    val secondGraph = GraphBuilder(root.childrenNodes()[generatorNameSECOND]!!)
            .addCheck("s1")
            .addCheck("s2")
            .build()
    GraphBuilder(secondGraph.childrenNodes()["s1"]!!)
            .addCheck("s3")
            .build()

    return root
}