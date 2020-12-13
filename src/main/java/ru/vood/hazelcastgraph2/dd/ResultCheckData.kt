package ru.vood.hazelcastgraph2.dd

import java.io.Serializable

data class ResultCheckData(val bsnKey: String, val check: String, val errText: String) : Serializable