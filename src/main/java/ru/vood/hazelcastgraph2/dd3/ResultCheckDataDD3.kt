package ru.vood.hazelcastgraph2.dd3

import java.io.Serializable

data class ResultCheckDataDD3(val bsnKey: String, val check: String, val errText: String) : Serializable