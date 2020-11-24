package ru.vood.hazelcastgraph2.run;

import com.hazelcast.function.BiConsumerEx;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.SourceBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.vood.hazelcastgraph2.dto.SomeData;

@Component
public class SomeGraphJavaRun implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Pipeline pipeline = Pipeline.create();

        /*SourceBuilder
                .timestampedStream("trade-source"
                        , new FunctionEx<Processor.Context, SomeData>() {
                            @Override
                            public SomeData applyEx(Processor.Context context) throws Exception {
                                return new SomeData("f1", "f2", "f3");
                            }
                        }
                )
                .fillBufferFn(
                        new BiConsumerEx<SomeData, SourceBuilder.TimestampedSourceBuffer<SomeData>>() {
                            @Override
                            public void acceptEx(SomeData obj, SourceBuilder.TimestampedSourceBuffer<SomeData> buf) throws Exception {
                                buf.add(obj, System.currentTimeMillis()
                                );
                            }
                        })
                .build();*/
    }
}
