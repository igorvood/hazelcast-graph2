package ru.vood.hazelcastgraph2.dd2;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Traverser;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.jet.pipeline.StreamStage;
import ru.vood.hazelcastgraph2.dd2.dto.GeneratorDataDto;

public class sdfhdksjfhdskj {
    void dskjfh() {
        Pipeline pipelineFirst = Pipeline.create();
        StreamSource<GeneratorDataDto> build = null;
        StreamStage<GeneratorDataDto> flow = pipelineFirst.readFrom(build)
                .withNativeTimestamps(0);

        flow.flatMap(new FunctionEx<GeneratorDataDto, Traverser<GeneratorDataDto>>() {
            @Override
            public Traverser<GeneratorDataDto> applyEx(GeneratorDataDto generatorDataDto) throws Exception {
                new Traverser<GeneratorDataDto>() {
                    @Override
                    public GeneratorDataDto next() {
                        return null;
                    }
                };
                return null;
            }
        });
    }
}
