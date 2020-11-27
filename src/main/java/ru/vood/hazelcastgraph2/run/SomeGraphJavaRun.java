package ru.vood.hazelcastgraph2.run;

import com.hazelcast.function.BiConsumerEx;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.vood.hazelcastgraph2.dto.SomeDataJava;

@Component
public class SomeGraphJavaRun implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Pipeline pipeline = Pipeline.create();

        SourceBuilder<SomeDataJava>.TimestampedStream<Void> voidTimestampedStream = SourceBuilder
                .timestampedStream("trade-source"
                        , new FunctionEx<Processor.Context, SomeDataJava>() {
                            @Override
                            public SomeDataJava applyEx(Processor.Context context) throws Exception {
                                return new SomeDataJava("f1", "f2", "f3");
                            }
                        }
                );
        SourceBuilder<SomeDataJava>.TimestampedStream<SomeDataJava> someDataJavaTimestampedStream = voidTimestampedStream
                .fillBufferFn(
                        new BiConsumerEx<SomeDataJava, SourceBuilder.TimestampedSourceBuffer<SomeDataJava>>() {
                            @Override
                            public void acceptEx(SomeDataJava obj, SourceBuilder.TimestampedSourceBuffer<SomeDataJava> buf) throws Exception {
                                buf.add(obj, System.currentTimeMillis()
                                );
                            }
                        });
        StreamSource<SomeDataJava> build = someDataJavaTimestampedStream
                .build();
    }
}
