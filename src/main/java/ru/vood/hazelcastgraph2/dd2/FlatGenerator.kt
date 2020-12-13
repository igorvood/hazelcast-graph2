package ru.vood.hazelcastgraph2.dd2

import com.hazelcast.jet.Traverser
import ru.vood.hazelcastgraph2.dd2.dto.GeneratorDataDto
import ru.vood.hazelcastgraph2.dd2.dto.GraphResultDto

class FlatGenerator(generatorsData: List<GeneratorDataDto>) : Traverser<GraphResultDto> {
    private val generatorsDataIterator: Iterator<GeneratorDataDto> = generatorsData.iterator()

    override fun next() = if (generatorsDataIterator.hasNext()) GraphResultDto(generatorsDataIterator.next()) else null

}