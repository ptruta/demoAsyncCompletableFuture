package com.example.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SpringBootApplication
@Profile("async-weather")
public class AsyncSyncApplication {

    public static void main(String[] args) throws Exception {
//        runAsyncAllOf();
//        runAsynAnyOf();
        doRun();
    }

    private static CompletableFuture<Weather> runAsynAnyOf() {

        Random random = new Random();

        List<Supplier<Weather>> weatherTasks = buildWeatherTasks(random);

        List<CompletableFuture<Weather>> weatherCFS = new ArrayList<>();
        for (Supplier<Weather> task : weatherTasks) {
            CompletableFuture<Weather> future = CompletableFuture.supplyAsync(task);
            weatherCFS.add(future);
        }

//        CompletableFuture<Object> future = CompletableFuture.anyOf(weatherCFS.toArray(
//                new CompletableFuture[weatherCFS.size()]));
//
//        future.thenAccept(System.out::println).join();

        return CompletableFuture.anyOf(weatherCFS.toArray(
                new CompletableFuture[weatherCFS.size()])).thenApply(o -> (Weather) o);

    }

    private static CompletableFuture<Quotation> runAsyncAllOf() {
        Random random = new Random();

        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);

        List<CompletableFuture<Quotation>> quotationCFS = new ArrayList<>();
        for (Supplier<Quotation> task : quotationTasks) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(task);
            quotationCFS.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(quotationCFS.toArray(
                new CompletableFuture[quotationCFS.size()]));

//        CompletableFuture<Quotation> bestQuotation =
//                allOf.thenApply(
//                v -> {
////                    System.out.println("v = " + v);
////                    return null;
//                    try {
//                       return quotationCFS.stream()
//                                .map(CompletableFuture::join)
//                                .min(Comparator.comparing(Quotation::getInteger))
//                                .orElseThrow(Exception::new);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        );

//        System.out.println("bestQuotation = " + bestQuotation);


        return
                allOf.thenApply(
                        v -> {
//                    System.out.println("v = " + v);
//                    return null;
                            try {
                                return quotationCFS.stream()
                                        .map(CompletableFuture::join)
                                        .min(Comparator.comparing(Quotation::getInteger))
                                        .orElseThrow(Exception::new);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

    }

    public static void doRun() {

        CompletableFuture<Quotation> bestQuotation = runAsyncAllOf();
        CompletableFuture<Weather> anyWeather = runAsynAnyOf();

        TravelPage travelPage = new TravelPage(bestQuotation.join(), anyWeather.join()); // asta

        CompletableFuture<TravelPage> travelPageCompletableFuture = bestQuotation.thenCombine(anyWeather, TravelPage::new);// sau asta

        travelPageCompletableFuture.thenAccept(System.out::println).join();

        System.out.println("page " + travelPage);

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
