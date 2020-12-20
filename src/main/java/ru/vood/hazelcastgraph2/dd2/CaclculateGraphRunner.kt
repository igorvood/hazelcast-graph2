package ru.vood.hazelcastgraph2.dd2

import com.hazelcast.function.BiConsumerEx
import com.hazelcast.jet.Jet
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.pipeline.Pipeline
import com.hazelcast.jet.pipeline.Sinks
import com.hazelcast.jet.pipeline.SourceBuilder
import com.hazelcast.jet.pipeline.StreamStage
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import ru.vood.hazelcastgraph2.dd2.dto.CheckResultDto
import ru.vood.hazelcastgraph2.dd2.dto.GeneratorDataDto
import ru.vood.hazelcastgraph2.dd2.dto.GraphResultDto
import ru.vood.hazelcastgraph2.dd2.dto.dict.*
import java.io.Serializable

//@Component
class CaclculateGraphRunner : CommandLineRunner, Serializable {
    companion object {
        const val BSN_KEY = "BSN_KEY"
        var firstGraph = true
    }

    override fun run(vararg args: String?) {

        val graph = getGraph()
//        println(graph)
        val pipelineFirst = Pipeline.create()


        val second = GeneratorDataDto(BSN_KEY, generatorNameSECOND, hashMapOf("s1" to 1, "s2" to "s"), null, hashMapOf())
        val third = GeneratorDataDto(BSN_KEY, "third graph", hashMapOf("t1" to 1, "t2" to "s"), null, hashMapOf())
        val first = GeneratorDataDto(BSN_KEY, firstGeneratorNameROOT, hashMapOf("f1" to 1, "f2" to "s"), null, hashMapOf(generatorNameSECOND to mutableListOf(second), "third graph" to mutableListOf(third)))

        val build = SourceBuilder
                .timestampedStream<GeneratorDataDto>("graph with tree struct", { first })
                .fillBufferFn(BiConsumerEx<GeneratorDataDto, SourceBuilder.TimestampedSourceBuffer<GeneratorDataDto>> { obj, buf ->
                    if (firstGraph) {
                        buf.add(obj, System.currentTimeMillis())
                        firstGraph = false
                    }
                })
                .build()

        val flow = pipelineFirst.readFrom<GeneratorDataDto>(build)
                .withNativeTimestamps(0)
        val startDocument = flow.filter { it.graphName == firstGeneratorNameROOT }
                .map { GraphResultDto(it) }
                .setName("prepare graph $firstGeneratorNameROOT")


        var firstEdge: StreamStage<GraphResultDto> = streamStage(graph, startDocument)

        val res = firstEdge
                .map { it.resultCheck }
                .setName("result check only")
        val error = res
                .filter { !it.isEmpty() }
                .setName("filter errs")

        val reject = res
                .filter { !it.isEmpty() }
                .setName("filter rejection")

        error
                .writeTo(Sinks.logger())
                .setName(" Logger to db ")

        reject
                .writeTo(Sinks.logger())
                .setName(" Logger to file ")

        val instance = Jet.bootstrappedInstance()
        instance.newJob(pipelineFirst, JobConfig().setName(" graph and sub graph"))


    }

    private fun streamStage(graph: NodeInterface, startDocument: StreamStage<GraphResultDto>): StreamStage<GraphResultDto> {
        var firstEdge: StreamStage<GraphResultDto>? = null
        graph.childrenNodes()
                .forEach { nodeDict ->
                    val node = nodeDict.value

                    when {
                        node.type() == TypeNodeEnum.CHECK -> {
                            var map = startDocument
                                    .map { data ->
                                        val generatorDataDto = data.generatorDataDto
                                        if (generatorDataDto.context[node.nodeName()] == null) {
                                            val checkDto = node as CheckDto
                                            data.resultCheck.add(CheckResultDto(checkDto, "field " + checkDto.name + " is null"))
                                        }
                                        data
                                    }
                                    .setName("CHK-" + node.nodeName())
                            if (!node.childrenNodes().isEmpty())
                                map = streamStage(node, map)
                            firstEdge = mergeIfNotNull(firstEdge, map)
                        }
                        node.type() == TypeNodeEnum.GENERATOR -> {

                            val childGenerator = startDocument
                                    .flatMap { currentGraph ->
                                        val generatorDataList = currentGraph.generatorDataDto.child[node.nodeName()]
                                        val generatorDataDto = when {
                                            generatorDataList != null -> {
                                                generatorDataList
                                            }
                                            else -> listOf()
                                        }
                                        FlatGenerator(generatorDataDto)
                                    }
                                    .setName("separate child graph ${node.nodeName()}")
                            val streamStage = streamStage(node, childGenerator)
                            firstEdge = mergeIfNotNull(firstEdge, streamStage)

                        }
                        else -> {
                            throw IllegalStateException("""Не поддерживаемый тип ${node.type()}""")
                        }
                    }
                }

        return firstEdge!!
    }

    private fun mergeIfNotNull(
            cuurentStream: StreamStage<GraphResultDto>?,
            newStream: StreamStage<GraphResultDto>
    ) = cuurentStream?.merge(newStream) ?: newStream
}