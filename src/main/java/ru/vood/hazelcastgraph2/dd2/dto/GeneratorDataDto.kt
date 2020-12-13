package ru.vood.hazelcastgraph2.dd2.dto

import java.io.Serializable

 data class GeneratorDataDto(
        val bsnKey: String,
        val graphName: String,
        val context: HashMap<String, Any>,
        val parentGraph: GeneratorDataDto?,
        val child: HashMap<String, List<GeneratorDataDto>>
) : Serializable