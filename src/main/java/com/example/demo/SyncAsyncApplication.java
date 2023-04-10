package com.example.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
@Profile("async-quotation")
public class SyncAsyncApplication {

    public static void main(String[] args) throws Exception {
        runSync();
        runAsyncOne();
        runAsyncTwo();

//        completableFuture();
        runAsyncThree();
    }

    private static void completableFuture() {
        CompletableFuture<Quotation> quotationCompletableFuture =
                CompletableFuture.supplyAsync(() -> doSomething());

        CompletableFuture<Quotation> infoCF =
                quotationCompletableFuture.thenApply( quotation -> doSomethingQuotation(quotation));
    }

    private static Quotation doSomethingQuotation(Quotation quotation) {
        return quotation;
    }

    private static <U> U doSomething() {
        System.out.println("lala");
        return null;
    }

    public static void runSync() throws Exception {
        Random random = new Random();

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(random.nextInt(120));
            return new Quotation("server A", random.nextInt(60));
        };

        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(random.nextInt(120));
            return new Quotation("server B", random.nextInt(70));
        };

        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(random.nextInt(120));
            return new Quotation("server C", random.nextInt(80));
        };

        List<Callable<Quotation>> quotationTasks = Arrays.asList(fetchQuotationA, fetchQuotationB, fetchQuotationC);

        Instant begin = Instant.now();

        Quotation bestQuotation = quotationTasks.stream().map(fetchQuotation())
                .min(Comparator.comparing(Quotation::getInteger))
                .orElseThrow(Exception::new);

        Instant end = Instant.now();

        Duration duration = Duration.between(begin, end);

        System.out.println("Best quotation [SYNC] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
    }

    private static Function<Callable<Quotation>, Quotation> fetchQuotation() {
        return task -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static void runAsyncOne() throws Exception {
        Random random = new Random();

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(random.nextInt(120));
            return new Quotation("server A", random.nextInt(60));
        };

        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(random.nextInt(120));
            return new Quotation("server B", random.nextInt(70));
        };

        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(random.nextInt(120));
            return new Quotation("server C", random.nextInt(80));
        };

        List<Callable<Quotation>> quotationTasks = Arrays.asList(fetchQuotationA, fetchQuotationB, fetchQuotationC);

        Instant begin = Instant.now();

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<Future<Quotation>> futures = new ArrayList<>();

        for (Callable<Quotation> task : quotationTasks) {
            Future<Quotation> future = executorService.submit(task);
            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        for (Future<Quotation> future: futures) {
            Quotation quotation = future.get();
            quotations.add(quotation);
        }

//        Quotation bestQuotation = quotationTasks.stream().map(fetchQuotation())
//                .min(Comparator.comparing(Quotation::getInteger))
//                .orElseThrow(Exception::new);

        getBestQuotation(begin, executorService, quotations);
    }

    public static void runAsyncTwo() throws Exception {
        Random random = new Random();

        Supplier<Quotation> fetchQuotationA = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("server A", random.nextInt(60));
        };

        Supplier<Quotation> fetchQuotationB = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("server B", random.nextInt(70));
        };

        Supplier<Quotation> fetchQuotationC = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("server C", random.nextInt(80));
        };

        List<Supplier<Quotation>> quotationTasks = Arrays.asList(fetchQuotationA, fetchQuotationB, fetchQuotationC);

        Instant begin = Instant.now();

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<CompletableFuture<Quotation>> futures = new ArrayList<>();

        for (Supplier<Quotation> task : quotationTasks) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(task);

            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        for (CompletableFuture<Quotation> future: futures) {
            Quotation quotation = future.join(); // future.join() does the same as get() but is not throwing any exception
            quotations.add(quotation);
        }

//        Quotation bestQuotation = quotationTasks.stream().map(fetchQuotation())
//                .min(Comparator.comparing(Quotation::getInteger))
//                .orElseThrow(Exception::new);

        getBestQuotation(begin, executorService, quotations);
    }

    public static void runAsyncThree() throws Exception {
        Random random = new Random();

        Supplier<Quotation> fetchQuotationA = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("server A", random.nextInt(60));
        };

        Supplier<Quotation> fetchQuotationB = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("server B", random.nextInt(70));
        };

        Supplier<Quotation> fetchQuotationC = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("server C", random.nextInt(80));
        };

        List<Supplier<Quotation>> quotationTasks = Arrays.asList(fetchQuotationA, fetchQuotationB, fetchQuotationC);

        Instant begin = Instant.now();

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<CompletableFuture<Quotation>> futures = new ArrayList<>();

        for (Supplier<Quotation> task : quotationTasks) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(task);

            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        List<CompletableFuture<Void>> voids = new ArrayList<>();
        for (CompletableFuture<Quotation> future: futures) {
//            Quotation quotation = future.join(); // future.join() does the same as get() but is not throwing any exception
//            quotations.add(quotation);
            CompletableFuture<Void> voidCompletableFuture = future.thenAccept(System.out::println);
            voids.add(voidCompletableFuture);
        }

        voids.forEach(
                CompletableFuture::join
        );

//        Quotation bestQuotation = quotationTasks.stream().map(fetchQuotation())
//                .min(Comparator.comparing(Quotation::getInteger))
//                .orElseThrow(Exception::new);

        getBestQuotation(begin, executorService, quotations);
    }
    private static void getBestQuotation(Instant begin, ExecutorService executorService, List<Quotation> quotations) throws Exception {
        Quotation bestQuotation = quotations.stream()
                .min(Comparator.comparing(Quotation::getInteger))
                .orElseThrow(Exception::new);

        executorService.shutdown();

        Instant end = Instant.now();

        Duration duration = Duration.between(begin, end);

        System.out.println("Best quotation [ASYNC] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
    }

}

