package ru.vood.hazelcastgraph2.run;

import com.hazelcast.function.BiConsumerEx;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.function.PredicateEx;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.*;
import org.springframework.boot.CommandLineRunner;
import ru.vood.hazelcastgraph2.dto.SomeData;

import java.io.Serializable;

//@Component
public class SomeGraphJavaRun implements CommandLineRunner, Serializable {

    boolean flRun = true;

    @Override
    public void run(String... args) throws Exception {
        Pipeline pipeline = Pipeline.create();

        SourceBuilder<SomeData>.TimestampedStream<Void> voidTimestampedStream = SourceBuilder
                .timestampedStream("some-data-source"
                        , new FunctionEx<Processor.Context, SomeData>() {
                            @Override
                            public SomeData applyEx(Processor.Context context) throws Exception {
                                return new SomeData(null, null, "f3");
                            }
                        }
                );
        SourceBuilder<SomeData>.TimestampedStream<SomeData> someDataTimestampedStream = voidTimestampedStream
                .fillBufferFn(
                        new BiConsumerEx<SomeData, SourceBuilder.TimestampedSourceBuffer<SomeData>>() {
                            @Override
                            public void acceptEx(SomeData obj, SourceBuilder.TimestampedSourceBuffer<SomeData> buf) throws Exception {
                                if (flRun) {
                                    buf.add(obj, System.currentTimeMillis());
                                    flRun = false;
                                }
                            }
                        });
        StreamSource<SomeData> build = someDataTimestampedStream
                .build();

        StreamStage<SomeData> someDataStreamStage = pipeline.readFrom(build).withNativeTimestamps(0);

        StreamStage<String> filter = someDataStreamStage
                .setName("---------------field 1 check")
                .map((FunctionEx<SomeData, String>) SomeData -> SomeData.getField1() == null ? "Field 1 is null" : "")
                .filter((PredicateEx<String>) s -> !s.isEmpty())
                .peek();

        SinkStage sinkStage = someDataStreamStage
                .setName("------field 2 check")
                .map((FunctionEx<SomeData, String>) SomeData -> SomeData.getField2() == null ? "Field 2 is null" : "")
                .filter((PredicateEx<String>) s -> !s.isEmpty())
                //плюсует результат
                .merge(filter)
                .writeTo(Sinks.logger());

        JetInstance instance = Jet.bootstrappedInstance();
        instance.newJob(pipeline, new JobConfig().setName("mini graph"));


    }
}
