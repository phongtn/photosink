package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class CommonPoolTest {

    private static Logger logger = LoggerFactory.getLogger(CommonPoolTest.class.getName());

    public static void main(String[] args) {
        logger.info("CPU Core: " + Runtime.getRuntime().availableProcessors());
        logger.info("CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());
        logger.info("CommonPool Common Parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        long start = System.nanoTime();
        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.runAsync(CommonPoolTest::blockingOperation))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        logger.info("Processed in " + Duration.ofNanos(System.nanoTime() - start).toSeconds() + " sec");
    }

    private static void blockingOperation() {
        try {
            logger.info("Simulate the function execute in 1 second....");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
