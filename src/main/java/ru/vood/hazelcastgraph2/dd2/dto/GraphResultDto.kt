package ru.vood.hazelcastgraph2.dd2.dto

import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

data class GraphResultDto(
        val generatorDataDto: GeneratorDataDto,
        val resultCheck: CopyOnWriteArrayList<CheckResultDto> = CopyOnWriteArrayList()
) : Serializable