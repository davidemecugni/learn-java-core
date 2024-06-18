package com.nbicocchi.exercises.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ParallelPrimes {
    public static class PrimeEngine implements Callable<List<Integer>> {
        final int start;
        final int end;

        public PrimeEngine(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public boolean isPrime(int number) {
            if (number <= 1 || number % 2 == 0 || number % 3 == 0 || number % 5 == 0) {
                return number == 2 || number == 3 || number == 5;
            }
            return IntStream.iterate(6, i -> i <= Math.sqrt(number), i -> i + 6)
                    .noneMatch(i -> number % (i + 1) == 0 || number % (i - 1) == 0);
        }

        public List<Integer> call() {
            List<Integer> result = new ArrayList<>();
            for (int i = start; i < end; i++) {
                if (isPrime(i)) {
                    result.add(i);
                }
            }
            return result;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        List<Callable<List<Integer>>> callables = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            callables.add(new PrimeEngine(i * 1000, (i + 1) * 1000));
        }

        List<Future<List<Integer>>> futures = executor.invokeAll(callables);

        // main thread does other things
        Thread.sleep(1000);

        for (Future<List<Integer>> future : futures) {
            System.out.println(future.get());
        }

        executor.shutdown();
    }
}


