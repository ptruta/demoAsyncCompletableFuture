package com.example.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import static com.example.demo.AsyncSyncApplication.runAsynAnyOf;

@SpringBootApplication
@Profile("executor-threads")
public class ExecutorThreadsApplication {

    private static ThreadFactory quotationThreadFactory = new CustomizableThreadFactory();
    private static ThreadFactory weatherThreadFactory = new CustomizableThreadFactory();
    private static ThreadFactory minThreadFactory = new CustomizableThreadFactory();

    public static void main(String[] args) throws Exception {
        doRunExecutorService();
    }

    private static void doRunExecutorService() {

        ExecutorService quotationExecutor = Executors.newFixedThreadPool(4, quotationThreadFactory);
        ExecutorService weatherExecutor = Executors.newFixedThreadPool(4, weatherThreadFactory);
        ExecutorService minExecutor = Executors.newFixedThreadPool(4, minThreadFactory);

        Random random = new Random();

        List<Supplier<Weather>> weatherTasks = buildWeatherTasks(random);
        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);

        List<CompletableFuture<Weather>> weatherCFs = new ArrayList<>();

        for (Supplier<Weather> weatherTask : weatherTasks) {
            CompletableFuture<Weather> weatherCF = CompletableFuture.supplyAsync(weatherTask);
            weatherCFs.add(weatherCF);
        }

        List<CompletableFuture<Quotation>> quotationCFs = new ArrayList<>();

        for (Supplier<Quotation> quotationTask : quotationTasks) {
            CompletableFuture<Quotation> quotationCompletableFuture =
                    CompletableFuture.supplyAsync(quotationTask, quotationExecutor);
            quotationCFs.add(quotationCompletableFuture);
        }

        CompletableFuture<Void> allOfQuotations =
                CompletableFuture.allOf(quotationCFs.toArray(new CompletableFuture[quotationCFs.size()]));

        CompletableFuture<Quotation> bestQuotationCf =
                allOfQuotations.thenApplyAsync(
                        v -> {
                            System.out.println("AllOf then apply" + Thread.currentThread());
                            try {
                                return quotationCFs.stream()
                                        .map(CompletableFuture::join)
                                        .min(Comparator.comparing(Quotation::getInteger))
                                        .orElseThrow(Exception::new);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        minExecutor
                );

        CompletableFuture<Weather> anyWeather = runAsynAnyOf();
        CompletableFuture<Void> done =
                bestQuotationCf.thenCompose(
                        quotation -> anyWeather
                                .thenApply(weather -> new TravelPage(quotation, weather))
                                .thenAccept(System.out::println));

        done.join();

        quotationExecutor.shutdown();
        weatherExecutor.shutdown();
        minExecutor.shutdown();

    }

    private static List<Supplier<Quotation>> buildQuotationTasks(Random random) {
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

        return Arrays.asList(fetchQuotationA, fetchQuotationB, fetchQuotationC);
    }

    private static List<Supplier<Weather>> buildWeatherTasks(Random random) {
        Supplier<Weather> fetchQuotationA = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("server A", random.nextInt(60));
        };

        Supplier<Weather> fetchQuotationB = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("server B", random.nextInt(70));
        };

        Supplier<Weather> fetchQuotationC = () -> {
            try {
                Thread.sleep(random.nextInt(120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("server C", random.nextInt(80));
        };

        return Arrays.asList(fetchQuotationA, fetchQuotationB, fetchQuotationC);
    }
}
