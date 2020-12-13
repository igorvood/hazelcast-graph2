package ru.vood.hazelcastgraph2.dd2.dto.dict

 class GeneratorDto(val name: String,  val parentNode: NodeInterface? = null, val childNodes: HashMap<String, NodeInterface> = hashMapOf()) : NodeInterface {

    override fun type() = TypeNodeEnum.GENERATOR

    override fun nodeName() = name

    override fun parentNode() = parentNode

    override fun childrenNodes() = childNodes
}