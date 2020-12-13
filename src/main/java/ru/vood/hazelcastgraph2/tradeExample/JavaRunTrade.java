package ru.vood.hazelcastgraph2.tradeExample;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.datamodel.KeyedWindowResult;
import com.hazelcast.jet.datamodel.WindowResult;
import com.hazelcast.jet.pipeline.*;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static com.hazelcast.function.ComparatorEx.comparing;
import static com.hazelcast.jet.aggregate.AggregateOperations.counting;
import static com.hazelcast.jet.aggregate.AggregateOperations.topN;
import static com.hazelcast.jet.pipeline.WindowDefinition.sliding;
import static com.hazelcast.jet.pipeline.WindowDefinition.tumbling;
import static java.util.concurrent.TimeUnit.SECONDS;

//@Component
public class JavaRunTrade implements CommandLineRunner {

    private static final int TRADES_PER_SEC = 5000;
    private static final long MONITORING_INTERVAL = SECONDS.toMillis(60);
    private static final long REPORTING_INTERVAL = SECONDS.toMillis(5);

    private static Pipeline definePipeline() {
        Pipeline pipeline = Pipeline.create();
        StreamSource<Trade> streamSource = TradeSource.tradeStream(TRADES_PER_SEC);
        StreamStage<Trade> source = pipeline.readFrom(streamSource)
                .withNativeTimestamps(0);

        StreamStageWithKey<Trade, String> tradeStringStreamStageWithKey = source
                .groupingKey(Trade::getTicker);
        StageWithKeyAndWindow<Trade, String> window = tradeStringStreamStageWithKey
                .window(sliding(MONITORING_INTERVAL, REPORTING_INTERVAL));
        StreamStage<KeyedWindowResult<String, Long>> tradeCounts = window
                .aggregate(counting());

        StreamStage<WindowResult<List<KeyedWindowResult<String, Long>>>> topN = tradeCounts
                .window(tumbling(REPORTING_INTERVAL))
                .aggregate(topN(10, comparing(KeyedWindowResult::result)));

        StreamStage<WindowResult<List<KeyedWindowResult<String, Long>>>> aggregate = tradeCounts
                .window(tumbling(REPORTING_INTERVAL))
                .aggregate(AggregateOperations.toList());
        aggregate
                .map(wrList -> format(wrList.result()))
                .writeTo(Sinks.logger());

        topN.map(wrList -> format(wrList.result()))
                .writeTo(Sinks.logger());

        return pipeline;
    }

    private static String format(List<KeyedWindowResult<String, Long>> results) {
        StringBuilder sb = new StringBuilder("Most active stocks in past minute:");
        for (int i = 0; i < results.size(); i++) {
            KeyedWindowResult<String, Long> result = results.get(i);
            sb.append(String.format("\n\t%2d. %5s - %d trades", i + 1, result.getKey(), result.getValue()));
        }
        return sb.toString();
    }

    private static void submitForExecution(Pipeline pipeline) {
        JetInstance instance = Jet.bootstrappedInstance();
        instance.newJob(pipeline, new JobConfig().setName("trade-monitor"));
    }

    @Override
    public void run(String... args) throws Exception {
        Pipeline pipeline = definePipeline();
        submitForExecution(pipeline);

    }

}
