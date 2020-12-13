package ru.vood.hazelcastgraph2.dd3

import com.hazelcast.function.BiConsumerEx
import com.hazelcast.jet.Jet
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.pipeline.Pipeline
import com.hazelcast.jet.pipeline.Sinks
import com.hazelcast.jet.pipeline.SourceBuilder
import com.hazelcast.jet.pipeline.SourceBuilder.TimestampedSourceBuffer
import com.hazelcast.jet.pipeline.StreamStage
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.Serializable

//@Component
class CalculateGraphDD3 : CommandLineRunner, Serializable {
    companion object {
        const val BSN_KEY = "BSN_KEY"
        var firstGraph = true
        var secondGraph = false
        val delta = 10000
        var currentTime: Long = 0;
        val FIRST_GRAPH = "First GRAPH"
        val SECOND_GRAPH = "SECOND GRAPH"
    }

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        val pipelineFirst = Pipeline.create()
        val pipelineSecond = Pipeline.create()
        val buildFirst = SourceBuilder
                .timestampedStream<SubGraphData003>("some-data-source-first", { SubGraphData003("", "", mapOf()) })
                .fillBufferFn(BiConsumerEx<SubGraphData003, TimestampedSourceBuffer<SubGraphData003>> { obj, buf ->
                    if (secondGraph && currentTime + delta < System.currentTimeMillis()) {
                        buf.add(getSecondData(), System.currentTimeMillis())
                        secondGraph = false
                    }
                    if (firstGraph) {
                        buf.add(getFirstData(), System.currentTimeMillis())
                        firstGraph = false
                        secondGraph = true
                        val currentTimeMillis = System.currentTimeMillis()
                        currentTime = currentTimeMillis
                    }
                })
                .build()

        val buildSecond = SourceBuilder
                .timestampedStream<SubGraphData003>("some-data-source-second", { SubGraphData003("", "", mapOf()) })
                .fillBufferFn(BiConsumerEx<SubGraphData003, TimestampedSourceBuffer<SubGraphData003>> { obj, buf ->
                    if (secondGraph && currentTime + delta < System.currentTimeMillis()) {
                        buf.add(getSecondData(), System.currentTimeMillis())
                        secondGraph = false
                    }
                })
                .build()


        val withNativeTimestampsFirst = pipelineFirst.readFrom<SubGraphData003>(buildFirst)
                .withNativeTimestamps(0)

        val withNativeTimestampsSecond = pipelineFirst.readFrom<SubGraphData003>(buildSecond)
                .withNativeTimestamps(0)

        val firstGraph1 = firstGraph(withNativeTimestampsFirst)
        val secondGraph1 = secondGraph1(withNativeTimestampsSecond)

        val filter = firstGraph1
                .merge(secondGraph1)
                .filter { it != null }

        val loggerSink = filter

                .writeTo(Sinks.logger())
                .setName(" Logger ")

        val listSink = filter
                .setName(" some sink to list ")
                .writeTo(Sinks.list("resultList"))

        val groupingKey = filter
                .groupingKey { it!!.bsnKey }
//                .window(AggregateOperations.counting<ResultCheckData>())


        val instance = Jet.bootstrappedInstance()
        instance.newJob(pipelineFirst, JobConfig().setName(" graph first"))
//        instance.newJob(pipelineSecond, JobConfig().setName(" graph Second"))

    }

    private fun secondGraph1(withNativeTimestamps: StreamStage<SubGraphData003>): StreamStage<ResultCheckDataDD3?> {
        val chkFirst = withNativeTimestamps
                .filter { it.name == SECOND_GRAPH }
                .map { SubGraphDataEndCheckDD3(it, hashMapOf()) }
                .peek()
        val f1 = notNullCeck(chkFirst, "First graph check s1", "s1", "chk-10")

        val f2 = notNullCeck(chkFirst, "First graph check s2", "s2", "chk-20")
        val f3 = notNullCeck(chkFirst, "First graph check s3", "s3", "chk-30")
        val merge = f1

                //                .merge(f1)
                .merge(f2)
                .merge(f3)

        //                .map { it.checks.entries }
        return merge

    }

    private fun firstGraph(withNativeTimestamps: StreamStage<SubGraphData003>): StreamStage<ResultCheckDataDD3?> {
        val chkFirst = withNativeTimestamps
                .filter { it.name == FIRST_GRAPH }
                .map { SubGraphDataEndCheckDD3(it, hashMapOf()) }
                .peek()
        val f1 = notNullCeck(chkFirst, "First graph check f1", "f1", "chk-1")

        val f2 = notNullCeck(chkFirst, "First graph check f2", "f2", "chk-2")
        val f3 = notNullCeck(chkFirst, "First graph check f3", "f3", "chk-3")
        val merge = f1
                //                .merge(f1)
                .merge(f2)
                .merge(f3)
        //                .map { it.checks.entries }
        return merge

    }

    private fun notNullCeck(chkFirst: StreamStage<SubGraphDataEndCheckDD3>, name: String, field: String, check: String): StreamStage<ResultCheckDataDD3?> {
        return chkFirst
                .setName(name)
                .map {
                    if (it.subGraphData003.data[field] == null) {
                        ResultCheckDataDD3(it.subGraphData003.bsnKey, check, "$field is null")
                    } else null
                }
//                .filter { it != null }
    }

    private fun getFirstData(): SubGraphData003 {
        return SubGraphData003(BSN_KEY, FIRST_GRAPH, mapOf(/*"f1" to 12,*/ "f2" to "as"))
    }

    private fun getSecondData(): SubGraphData003 {
        return SubGraphData003(BSN_KEY, SECOND_GRAPH, mapOf("s1" to 12, "s2" to "as"))
    }

}