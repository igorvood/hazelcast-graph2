package ru.vood.hazelcastgraph2.dd2

import com.hazelcast.jet.Traverser
import ru.vood.hazelcastgraph2.dd2.dto.GeneratorDataDto

class NextGen(currentGeneratorDataDto: GeneratorDataDto) : Traverser<GeneratorDataDto> {
    var generators = currentGeneratorDataDto.child.entries.flatMap { it.value }.iterator()

    override fun next(): GeneratorDataDto {
        return generators.next()
    }
}