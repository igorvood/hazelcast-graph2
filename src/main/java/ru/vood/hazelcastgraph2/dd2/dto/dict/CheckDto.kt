package ru.vood.hazelcastgraph2.dd2.dto.dict

class CheckDto(val name: String, val parentNode: NodeInterface, val childNodes: HashMap<String, NodeInterface> = hashMapOf()) : NodeInterface {

    override fun type() = TypeNodeEnum.CHECK

    override fun nodeName() = name

    override fun parentNode() = parentNode

    override fun childrenNodes() = childNodes
}