package ru.vood.hazelcastgraph2.dd2.dto.dict

import java.io.Serializable

interface NodeInterface : Serializable{

    fun type(): TypeNodeEnum

    fun nodeName(): String

    fun parentNode(): NodeInterface?

    fun childrenNodes(): HashMap<String, NodeInterface>
}
