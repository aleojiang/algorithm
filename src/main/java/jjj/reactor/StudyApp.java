package jjj.reactor;

import jjj.reactor.file.Address;
import jjj.reactor.file.Author;
import jjj.reactor.file.Book;
import jjj.reactor.file.parser.Headers;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.TopicProcessor;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.scheduler.forkjoin.ForkJoinPoolScheduler;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.concurrent.WaitStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  12:38 03/04/2018.
 */
public class StudyApp {
    private final static Logger log = Loggers.getLogger(StudyApp.class);
    
    private static final Path path = Paths.get(System.getenv().get("PWD"))
            .resolve("src/main/resources/data.csv");
    private static final Path book = Paths.get(System.getenv().get("PWD"))
            .resolve("src/main/resources/book.csv");
    private static final Path test = Paths.get(System.getenv().get("PWD"))
            .resolve("src/main/resources/test.csv");
    
    public static void main(String[] args) throws Exception {
        testFlux5();
    }
    
    private static void testFlux5() {
        Flux<List<String>> flux = fromPath2(test)
                .publishOn(Schedulers.parallel())
                .buffer(10)
                .publishOn(Schedulers.parallel())
                .map(a -> doNothing(a, 1))
                .publishOn(Schedulers.parallel())
                .map(a -> doNothing(a, 2))
                .publishOn(Schedulers.parallel())
                .map(a -> doNothing(a, 3))
                .publishOn(Schedulers.parallel())
                .map(a -> doNothing(a, 4));
        flux.subscribe();
        
        waitSeconds(100);
    }
    
    private static <T> T doNothing(T s, int step) {
        log.info("step {} in {} ", step, Thread.currentThread().getName());
        waitSeconds(ThreadLocalRandom.current().nextInt(10));
        return s;
    }
    
    private static void testFlux4() {
        AtomicInteger counter = new AtomicInteger();
        Flux<String> flux = fromPath2(test)
                .publishOn(Schedulers.parallel())
                .map(e -> {
                    waitSeconds(ThreadLocalRandom.current().nextInt(2));
                    return e;
                })
                .share();
        IntStream.rangeClosed(1, 2).forEach(i -> flux.subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                super.hookOnSubscribe(subscription);
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(String value) {
                super.hookOnNext(value);
                counter.incrementAndGet();
                log.info(value);
                request(1);
            }
        }));
        
        waitSeconds(5);
        log.info("counter={}", counter);
        
    }
    
    private static void testFlux3() throws IOException {
        AtomicInteger counter = new AtomicInteger();
        WorkQueueProcessor<CSVRecord> processor = WorkQueueProcessor.create();
        
        try (Stream<CSVRecord> stream = StreamSupport
                .stream(CSVFormat.RFC4180.parse(Files.newBufferedReader(test)).spliterator(), false)) {
            stream.forEach(processor::onNext);
        }
        processor.onComplete();
        
        Flux<CSVRecord> flux = Flux.from(processor);
        IntStream.rangeClosed(1, 10).forEach(i -> flux.subscribe(new BaseSubscriber<CSVRecord>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                super.hookOnSubscribe(subscription);
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(CSVRecord value) {
                super.hookOnNext(value);
                counter.incrementAndGet();
                request(1);
            }
        }));
        
        waitSeconds(5);
        log.info("counter={}", counter);
        
    }
    
    private static void testFlux2() {
        AtomicInteger counter = new AtomicInteger();
        Flux<List<Integer>> flux = Flux.range(1, 10)
                .publishOn(Schedulers.parallel())
                .flatMap(e -> {
                    waitSeconds(ThreadLocalRandom.current().nextInt(5));
                    return Mono.just(e);
                })
                .buffer(2)
                .onBackpressureBuffer();
        IntStream.rangeClosed(1, 10).forEach(i -> flux.subscribe(e -> {
            counter.incrementAndGet();
            waitSeconds(ThreadLocalRandom.current().nextInt(5));
            e.forEach(v -> log.info("msg={}", v));
        }));
        
        waitSeconds(10);
        
        log.info("counter={}", counter);
    }
    
    private static void testFlux1() {
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
                .mergeWith(Flux.interval(Duration.ofSeconds(1)))
                .mergeWith(Flux.interval(Duration.ofSeconds(1)))
                .subscribeOn(Schedulers.elastic())
                .map(a -> {
                    log.info("a");
                    return a;
                })
                .map(a -> {
                    log.info("b");
                    return a;
                })
                .map(a -> {
                    log.info("c");
                    return a;
                });
        
        IntStream.rangeClosed(1, 3).forEach(i -> flux.subscribe(e -> {
            log.info("xyz");
        }));
        
        waitSeconds(10);
    }
    
    private static void testFile6() {
        AtomicInteger counter = new AtomicInteger();
        final Flux<List<String>> source =
                Flux.defer(() -> Flux.using(() -> Files.lines(test), Flux::fromStream, BaseStream::close))
                        .publishOn(Schedulers.elastic())
                        .share()
                        .buffer(5);
        
        IntStream.rangeClosed(1, 2).forEach(i -> source.log().subscribe(new BaseSubscriber<List<String>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                super.hookOnSubscribe(subscription);
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(List<String> value) {
                super.hookOnNext(value);
                counter.incrementAndGet();
                request(1);
            }
        }));
        waitSeconds(5);
        
        log.info("counter={}", counter);
        
    }
    
    private static void testFile5() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4);
        ints.stream()
                .map(a -> {
                    log.info("step1");
                    return a * 2;
                })
                .map(a -> {
                    log.info("step2");
                    return a * 3;
                }).collect(Collectors.toList());
        
    }
    
    private static void testFile4() {
        AtomicInteger count = new AtomicInteger();
        
        Flux<List<String>> flux = Flux
                .defer(() -> Flux.using(() -> Files.lines(test), Flux::fromStream, BaseStream::close))
                .publishOn(Schedulers.elastic())
                .map(e -> e + "a")
                .log("a")
                .map(e -> e + "b")
                .log("b")
                .map(e -> e + "c")
                .log()
                .share()
                .buffer(2);
        IntStream.rangeClosed(1, 10).forEach(
                i -> flux.subscribeOn(Schedulers.elastic(), true).subscribe(new BaseSubscriber<List<String>>() {
                    ThreadLocal<Long> start = ThreadLocal.withInitial(System::currentTimeMillis);
                    
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        super.hookOnSubscribe(subscription);
                        start.set(System.currentTimeMillis());
                        subscription.request(1);
                    }
                    
                    @Override
                    protected void hookOnNext(List<String> value) {
                        super.hookOnNext(value);
                        count.incrementAndGet();
                        waitSeconds(1);
                        
                        request(1);
                    }
                    
                    @Override
                    protected void hookOnComplete() {
                        super.hookOnComplete();
                        log.info("complete={}", System.currentTimeMillis() - start.get());
                    }
                }));
        
        waitSeconds(20);
        
        log.info("count={}", count);
    }
    
    private static void testFile3() {
        AtomicInteger count = new AtomicInteger();
        Flux<CSVRecord> original = fromCSV(book);
        Flux<List<Book>> books = original
                .publishOn(ForkJoinPoolScheduler.create("a"), 1)
                .map(StudyApp::parse)
                .buffer(20);
        
        IntStream.rangeClosed(1, 10)
                .forEach(i -> books.log().subscribeOn(Schedulers.elastic()).subscribe(e -> count.incrementAndGet()));
        
        waitSeconds(10);
        log.info("count={}", count);
        
    }
    
    private static void testFile2() throws IOException {
        WorkQueueProcessor<CSVRecord> processor = WorkQueueProcessor.<CSVRecord>builder()
                .bufferSize(256)
                .waitStrategy(WaitStrategy.blocking())
                .share(false)
                .build();
        
        IntStream.rangeClosed(1, 4).forEach(e -> processor.buffer(10).log()
                .subscribe(new BaseSubscriber<List<CSVRecord>>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(1);
                    }
                    
                    @Override
                    protected void hookOnNext(List<CSVRecord> value) {
                        waitSeconds(ThreadLocalRandom.current().nextInt(2));
                        request(1);
                    }
                }));
        
        try (Stream<CSVRecord> stream = StreamSupport
                .stream(CSVFormat.RFC4180.parse(Files.newBufferedReader(book)).spliterator(), false)) {
            stream.forEach(processor::onNext);
        }
        
        processor.onComplete();
        waitSeconds(30);
        
    }
    
    private static void testFile() throws InterruptedException {
        log.info("thread=" + Thread.currentThread().getName());
        
        Flux<CSVRecord> original = fromCSV(book);
        Flux<List<Book>> books = original
                .publishOn(Schedulers.elastic())
                .map(StudyApp::parse)
                .buffer(10);
        
        books.log().subscribe(new BaseSubscriber<List<Book>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(List<Book> value) {
                value.stream().map(e -> e.toString()).forEach(System.out::println);
                waitSeconds(new Random().nextInt(10));
                request(1);
            }
        });
        books.log().subscribe(new BaseSubscriber<List<Book>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(List<Book> value) {
                value.stream().map(e -> e.getAuthor().toString()).collect(Collectors.toSet())
                        .forEach(System.out::println);
                waitSeconds(new Random().nextInt(10));
                request(1);
            }
        });
        books.log().subscribe(new BaseSubscriber<List<Book>>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(List<Book> value) {
                value.stream().map(e -> e.getAuthor().getAddress().toString()).collect(Collectors.toSet())
                        .forEach(System.out::println);
                waitSeconds(new Random().nextInt(10));
                request(1);
            }
        });
        
        waitSeconds(10);
    }
    
    private static Book parse(CSVRecord e) {
        Book book = new Book();
        
        book.setTitle(e.get(Headers.title));
        book.setIsdn(e.get(Headers.isdn));
        book.setPublishDate(e.get(Headers.publish_date));
        Author author = new Author();
        author.setDob(e.get(Headers.author_dob));
        author.setFirstName(e.get(Headers.author_first_name));
        author.setLastName(e.get(Headers.author_last_name));
        Address address = new Address();
        address.setCountry(e.get(Headers.author_addr_country));
        address.setState(e.get(Headers.author_addr_state));
        address.setCity(e.get(Headers.author_addr_city));
        address.setStreet(e.get(Headers.author_addr_street));
        address.setPostalCode(e.get(Headers.author_addr_postal_code));
        author.setAddress(address);
        book.setAuthor(author);
        return book;
    }
    
    private static void waitSeconds(long i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void testProcessor() {
        testDirectProcessor();
    }
    
    private static void testDirectProcessor() {
        DirectProcessor<String> directProcessor = DirectProcessor.create();
        Flux<String> flux = directProcessor
                .map(e -> e);
        flux.log().subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(1);
            }
            
            @Override
            protected void hookOnNext(String value) {
                request(1);
            }
        });
        
        directProcessor.onComplete();
        directProcessor.blockLast();
    }
    
    public static void test7() throws InterruptedException {
        TopicProcessor<String> topicProcessor = TopicProcessor.<String>builder()
                .share(true)
                //                                .executor(Executors.newSingleThreadExecutor())
                .build();
        
        Flux<String> flux1 = Flux.using(
                () -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close);
        
        //        Flux<Integer> flux2 = topicProcessor.map(e -> e);
        //        Flux<Integer> flux3 = topicProcessor.map(e -> e);
        
        AtomicInteger count = new AtomicInteger(0);
        flux1.subscribe(e -> {
            log.info("flux1 subscriber:{}", e);
            count.incrementAndGet();
        });
        flux1.subscribe(e -> {
            log.info("flux2 subscriber:{}", e);
            count.incrementAndGet();
        });
        flux1.subscribe(e -> {
            log.info("flux3 subscriber:{}", e);
            count.incrementAndGet();
        });
        
        //        IntStream.rangeClosed(1, 100)
        //                .parallel()
        //                .forEach(e -> {
        //                    log.info("emit:{}", e);
        //                    topicProcessor.onNext(e);
        //                });
        
        topicProcessor.onComplete();
        topicProcessor.blockLast();
        
        TimeUnit.SECONDS.sleep(10);
        System.out.println(count.get());
    }
    
    private static void test6() {
        Flux.range(1, 6)    // 1
                .log()
                .doOnRequest(n -> System.out.println("Request " + n + " values..."))    // 2
                .subscribe(new BaseSubscriber<Integer>() {  // 3
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) { // 4
                        System.out.println("Subscribed and make a request...");
                        request(1); // 5
                    }
                    
                    @Override
                    protected void hookOnNext(Integer value) {  // 6
                        try {
                            TimeUnit.SECONDS.sleep(1);  // 7
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Get value [" + value + "]");    // 8
                        request(2); // 9
                    }
                });
    }
    
    private static void test5() {
        String path = System.getenv().get("PWD");
        
        Flux<String> flux = fromPath(Paths.get(path).resolve("src/main/resources/data.csv"));
        
        final AtomicBoolean isValid = new AtomicBoolean(true);
        
        // validation
        flux.log().subscribe(e -> {
            throw new RuntimeException();
        }, error -> isValid.set(false));
        
        // parse
        flux
                .log()
                .map(text -> {
                    System.out.println("step 1");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return text;
                })
                .map(text -> {
                    System.out.println("step 2");
                    return text;
                })
                .map(text -> {
                    System.out.println("step 3");
                    return text;
                })
                .subscribe(new NoOnTextRequestSubscriber<>());
    }
    
    private static void test4() throws InterruptedException {
        String key = "message";
        Flux<String> flux = Flux.just("1", "2", "3")
                .map(s -> s)
                .map(s -> s)
                .map(s -> s)
                .map(s -> s)
                .subscriberContext(ctx -> ctx.put(key, "World"));
        flux.log().subscribe();
    }
    
    private static Flux<CSVRecord> fromCSV(Path path) {
        return Flux.defer(() -> Flux.using(
                () -> StreamSupport.stream(CSVFormat.RFC4180
                        .withIgnoreHeaderCase()
                        .withHeader(Arrays.stream(Headers.values()).map(Enum::name).toArray(String[]::new))
                        .withSkipHeaderRecord(true)
                        .parse(Files.newBufferedReader(path))
                        .spliterator(), false),
                Flux::fromStream,
                BaseStream::close));
    }
    
    private static Flux<String> fromPath(Path path) {
        return Flux.defer(() -> Flux.using(
                () -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close));
    }
    
    private static Flux<String> fromPath2(Path path) {
        return Flux.using(
                () -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close);
    }
    
    private static void test1() throws InterruptedException {
        Mono<String> mono1 = Mono.just("mono1");
        Mono<String> mono2 = Mono.just("|mono2");
        Mono<String> mono3 = Mono.just("|mono3");
        
        System.out.println("=== Flux.concat(mono1, mono3, mono2) ===");
        Flux.concat(mono1, mono3, mono2).subscribe(System.out::print);
        
        System.out.println("\n=== combine the value of mono1 then mono2 then mono3 ===");
        mono1.concatWith(mono2).concatWith(mono3).subscribe(System.out::print);
        
        Flux<String> flux1 = Flux.just("1", "2", "3", "4");
        Flux<String> flux2 = Flux.just("A", "B", "C");
        
        System.out.println("\n=== Flux.zip(flux2, flux1, combination) ===");
        Flux.zip(flux2, flux1,
                (itemFlux2, itemFlux1) -> "-[" + itemFlux2 + itemFlux1 + "]")
                .subscribe(System.out::print);
        
        System.out.println("\n=== flux1 values zip with flux2 values ===");
        flux1.zipWith(flux2,
                (itemFlux1, itemFlux2) -> "-[" + itemFlux1 + itemFlux2 + "]-")
                .subscribe(System.out::print);
        
        Flux<String> intervalFlux1 = Flux
                .interval(Duration.ofMillis(500))
                .zipWith(flux1, (i, string) -> string);
        
        Flux<String> intervalFlux2 = Flux
                .interval(Duration.ofMillis(700))
                .zipWith(flux2, (i, string) -> string);
        
        System.out.println("\n=== Flux.concat(flux2, flux1) ===");
        Flux.concat(flux2, flux1).subscribe(System.out::print);
        
        System.out.println("\n=== flux1 values and then flux2 values ===");
        flux1.concatWith(flux2).subscribe(System.out::print);
        
        System.out.println("\n=== Flux.concat(intervalFlux2, flux1) ===");
        Flux.concat(intervalFlux2, flux1).subscribe(System.out::print);
        Thread.sleep(3000);
        
        System.out.println("\n=== intervalFlux1 values and then flux2 values ===");
        intervalFlux1.concatWith(flux2).subscribe(System.out::print);
        Thread.sleep(3000);
        
        System.out.println("\n=== Flux.concat(intervalFlux2, intervalFlux1) ===");
        Flux.concat(intervalFlux2, intervalFlux1).subscribe(System.out::print);
        Thread.sleep(5000);
        
        System.out.println("\n=== intervalFlux1 values and then intervalFlux2 values ===");
        intervalFlux1.concatWith(intervalFlux2).subscribe(System.out::print);
        Thread.sleep(5000);
        
        System.out.println("\n=== Flux.merge(intervalFlux1, intervalFlux2) ===");
        Flux.merge(intervalFlux1, intervalFlux2).subscribe(System.out::print);
        Thread.sleep(3000);
        
        System.out.println("\n=== interleave flux1 values with flux2 values ===");
        intervalFlux1.mergeWith(intervalFlux2).subscribe(System.out::print);
        Thread.sleep(3000);
    }
    
    private static void test2() {
        Flux.just("red", "white", "blue")
                .log()
                .map(String::toUpperCase)
                .subscribe(new SampleSubscriber<>());
    }
    
    private static void test3() {
        //        Flux<String> x = Flux.generate()
        //        Flux.range(1, 100).buffer(20).subscribe(System.out::println);
        //        Flux.interval(Duration.ofMillis(1000)).take(2).toStream().forEach(System.out::println);
        //        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        //        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
        //        Flux.range(1, 100).window(20).subscribe(System.out::println);
        
        //        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        //        Flux<Integer> ints = Flux.range(1, 400);
        //        ints.subscribe(i -> System.out.println(i),
        //                error -> System.err.println("Error " + error),
        //                () -> System.out.println("Done"),
        //                s -> ss.request(1000));
        //        ints.subscribe(ss);
        
        //        Flux<String> flux = Flux.generate(
        //                AtomicLong::new,
        //                (state, sink) -> {
        //                    long i = state.getAndIncrement();
        //                    sink.next("3 x " + i + " = " + 3*i);
        //                    if (i == 10) sink.complete();
        //                    return state;
        //                }, (state) -> System.out.println("state: " + state));
        //
        //        flux.subscribe(System.out::println);
        
        //        Flux<String> flux1 = Flux.just("{1}", "{2}", "{3}", "{4}");
        //        Flux<String> flux2 = Flux.just("|A|", "|B|", "|C|");
        
        //        Flux.concat(flux1,flux2).subscribe(System.out::println);
        //        Flux.merge(flux1,flux2).subscribe(System.out::println);
        
        //        String path = StudyApp.class.getClassLoader().getResource("data.csv").getFile();
        //        Flux<String> source = fromPath(Paths.get(path));
        ////
        //        Task1Subscriber<String> ss = new Task1Subscriber<>();
        //        Flux<String> x = source
        //                .flatMap(s-> {
        //                    Mono<String> t1=  Mono.subscriberContext().map(ctx->ctx.get("k1")+s);
        //                    Mono<String> t2=  Mono.subscriberContext().map(ctx->ctx.get("k2")+s);
        //                    Mono<String> t3=  Mono.subscriberContext().map(ctx->ctx.get("k3")+s);
        //                    return Flux.concat(t1, t2, t3);
        //                })
        //                .subscriberContext(ctx->ctx.put("k1", "k1 "))
        //                .subscriberContext(ctx->ctx.put("k2", "k2 "))
        //                .subscriberContext(ctx->ctx.put("k3", "k3 "));
        //
        //        x.subscribe(ss);
        
        //        source.subscribe(System.out::println);
        //        source.subscribe(System.out::println);
        
        //        Flux<String> ids = ifhrIds();
        //
        //        Flux<String> combinations =
        //                ids.flatMap(id -> {
        //                    Mono<String> nameTask = ifhrName(id);
        //                    Mono<Integer> statTask = ifhrStat(id);
        //
        //                    return nameTask.zipWith(statTask,
        //                            (name, stat) -> "Name " + name + " has stats " + stat);
        //                });
        //
        //        Mono<List<String>> result = combinations.collectList();
        //
        //        List<String> results = result.block();
        //
        //        results.forEach(System.out::println);
    }
    
    private static Mono<Integer> ifhrStat(String id) {
        return Mono.just(1);
    }
    
    private static Mono<String> ifhrName(String id) {
        return Mono.just("1");
    }
    
    private static Flux<String> ifhrIds() {
        return Flux.just("1", "2", "3");
    }
}
