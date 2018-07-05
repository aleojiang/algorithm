package jjj.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This sample demonstrate the OOM
 * <p>
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  09:09 06/04/2018.
 */
public class FluxSinkOOM {
    private static final Logger LOGGER = Loggers.getLogger(FluxSinkOOM.class);
    
    public static void main(String[] args) {
        testOverflowStrategy(FluxSink.OverflowStrategy.DROP);
    }
    
    
    /**
     * this test by using FluxSink.OverflowStrategy.BUFFER, which will created an unbounded queue to hold the data
     *
     * see {@link FluxSink.OverflowStrategy}
     * see {@link reactor.util.concurrent.SpscLinkedArrayQueue}
     * see {@link reactor.core.publisher.FluxCreate.BufferAsyncSink}
     * see {@link reactor.util.concurrent.Queues }
     *
     */
    private static void testOverflowStrategy(FluxSink.OverflowStrategy strategy) {
        final Flux<Integer> flux = Flux.
                <Integer>create(sink -> {
                    LOGGER.info("sink: {}", sink.getClass());
                    while (true) {
                        sink.next(ThreadLocalRandom.current().nextInt());
                    }
                }, strategy)
                .publishOn(Schedulers.elastic(), 1);
        
        LOGGER.info("flux:{}, prefetch:{}", flux.getClass(), flux.getPrefetch());
        
        flux.subscribe(e -> {
            LOGGER.info("subscribe:{}", e);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        
        try {
            TimeUnit.MINUTES.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
}
