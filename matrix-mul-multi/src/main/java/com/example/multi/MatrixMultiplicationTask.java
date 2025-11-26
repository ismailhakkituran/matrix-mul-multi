package com.example.multi;

public class MatrixMultiplicationTask implements Runnable {

    private final Matrix a;
    private final Matrix b;
    private final Matrix result;
    private final int start;
    private final int end;

    public MatrixMultiplicationTask(Matrix a, Matrix b, Matrix result, int start, int end) {
        this.a = a;
        this.b = b;
        this.result = result;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        int n = a.getCols();
        int p = b.getCols();
        double[][] aData = a.getData();
        double[][] bData = b.getData();
        double[][] cData = result.getData();

        for (int i = start; i < end; i++)
            for (int k = 0; k < n; k++) {
                double a_ik = aData[i][k];
                for (int j = 0; j < p; j++)
                    cData[i][j] += a_ik * bData[k][j];
            }
    }
}
