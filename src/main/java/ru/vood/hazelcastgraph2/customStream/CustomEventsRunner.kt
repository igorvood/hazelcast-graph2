package ru.vood.hazelcastgraph2.customStream

import com.hazelcast.jet.Jet
import com.hazelcast.jet.JetInstance
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.core.Processor
import com.hazelcast.jet.pipeline.Pipeline
import com.hazelcast.jet.pipeline.Sinks
import com.hazelcast.jet.pipeline.SourceBuilder
import org.springframework.boot.CommandLineRunner
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class CustomEventsRunner : CommandLineRunner, Serializable {

    val concurrentLinkedQueue = ConcurrentLinkedQueue<MyData>()

//    @Autowired
//    lateinit var instanceJet: InstanceJet

    val bsn = "sadsad"
    private val KEY = "BSN"

    private val DATA = "data"

    @Scheduled(fixedDelay = 3000)
    fun someTimeData() {


        concurrentLinkedQueue.offer(MyData(hashMapOf(KEY to bsn, DATA to "1")))
        println("Вот столько милисекунд ${concurrentLinkedQueue.hashCode()}")
//        concurrentLinkedQueue.hashCode()
    }

    fun dsdsd() = concurrentLinkedQueue

    override fun run(vararg args: String) {
        val streamSource = SourceBuilder
                .stream<ConcurrentLinkedQueue<MyData>>("Some", fun(it: Processor.Context): ConcurrentLinkedQueue<MyData> {
                    println(it)
                    return dsdsd()
                })
                .fillBufferFn<MyData>(fun(que: ConcurrentLinkedQueue<MyData>, mapSourceBuffer: SourceBuilder.SourceBuffer<MyData>) {
//                    println(concurrentLinkedQueue.hashCode())
                    val poll = concurrentLinkedQueue.poll()
                    poll?.let { mapSourceBuffer.add(it) }
                })
                .build()

        val pipeline = Pipeline.create()

        val flow = pipeline.readFrom(streamSource)
                .withIngestionTimestamps()
        flow
                .filter {
                    it.child[KEY] == bsn
                }
                .map { it.child[DATA]?.toBigDecimal() }
                .setName("convert")
                .writeTo(Sinks.logger())
                .setName(" Logger to file ")

        val instanceJet = Jet.bootstrappedInstance()
        addNewJob(instanceJet, "job some", pipeline)

    }

    fun addNewJob(instanceJet: JetInstance, jobName: String, pipeline: Pipeline) {
        instanceJet.newJob(pipeline, JobConfig().setName(jobName))
    }

}