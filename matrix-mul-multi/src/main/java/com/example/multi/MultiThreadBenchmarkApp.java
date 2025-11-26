package com.example.multi;

import java.util.Random;

public class MultiThreadBenchmarkApp {

    public static void main(String[] args) {

        int size = 500;
        long durationSeconds = 1;
        int threads = Runtime.getRuntime().availableProcessors();

        if (args.length >= 1) size = Integer.parseInt(args[0]);
        if (args.length >= 2) durationSeconds = Long.parseLong(args[1]);
        if (args.length >= 3) threads = Integer.parseInt(args[2]);

        System.out.println("=== Multi-thread Benchmark (CTRL-C ile çıkabilirsiniz) ===");
        System.out.println("Matrix: " + size + "x" + size);
        System.out.println("Threads: " + threads);
        System.out.println();

        Random random = new Random(42);
        Matrix a = Matrix.randomMatrix(size, size, -1, 1, random);
        Matrix b = Matrix.randomMatrix(size, size, -1, 1, random);

        MultiThreadMatrixMultiplicationHandler handler =
                new MultiThreadMatrixMultiplicationHandler(threads);

        // Warm-up
        for (int i = 0; i < 5; i++) handler.multiply(a, b);

        long durationNanos = durationSeconds * 1_000_000_000L;

        while (true) {

            long end = System.nanoTime() + durationNanos;
            long count = 0;

            while (System.nanoTime() < end) {
                handler.multiply(a, b);
                count++;
            }

            System.out.println("Mult/s: " + count);
        }
    }
}
