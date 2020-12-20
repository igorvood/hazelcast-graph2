package ru.vood.hazelcastgraph2.customStream

import java.io.Serializable

data class MyData (val child: HashMap<String, String>) : Serializable