package ru.vood.hazelcastgraph2.customStream

import com.hazelcast.jet.Jet
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.pipeline.Pipeline
import org.springframework.stereotype.Component
import java.io.Serializable
import javax.annotation.PreDestroy


@Component
class InstanceJet: Serializable {

    private var instance = Jet.bootstrappedInstance()

    @PreDestroy
    fun destroy() {
        instance.shutdown();
    }

    fun addNewJob(jobName: String, pipeline: Pipeline) {
        instance.newJob(pipeline, JobConfig().setName(jobName))
    }

}