package com.example.multi;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadMatrixMultiplicationHandler {

    private final int threadCount;

    public MultiThreadMatrixMultiplicationHandler(int threadCount) {
        this.threadCount = threadCount;
    }

    public Matrix multiply(Matrix a, Matrix b) {
        if (a.getCols() != b.getRows())
            throw new IllegalArgumentException("A.cols != B.rows");

        int m = a.getRows();
        int p = b.getCols();
        Matrix result = new Matrix(m, p);

        List<Thread> list = new ArrayList<>();
        int rowsPerThread = m / threadCount;
        int extra = m % threadCount;

        int cur = 0;
        for (int t = 0; t < threadCount; t++) {
            int start = cur;
            int rows = rowsPerThread + (t < extra ? 1 : 0);
            int end = start + rows;
            cur = end;

            if (rows == 0) break;

            Thread th = new Thread(new MatrixMultiplicationTask(a, b, result, start, end));
            th.start();
            list.add(th);
        }

        for (Thread th : list)
            try { th.join(); } catch (InterruptedException ignored) {}

        return result;
    }
}
