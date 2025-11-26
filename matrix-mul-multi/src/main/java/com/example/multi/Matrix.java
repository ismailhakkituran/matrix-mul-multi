package com.example.multi;

import java.util.Random;

public class Matrix {
    private final int rows;
    private final int cols;
    private final double[][] data;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data.length == 0 ? 0 : data[0].length;
        this.data = data;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public double[][] getData() { return data; }
    public double get(int r, int c) { return data[r][c]; }
    public void set(int r, int c, double value) { data[r][c] = value; }

    public static Matrix randomMatrix(int rows, int cols, double min, double max, Random random) {
        Matrix m = new Matrix(rows, cols);
        double range = max - min;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                m.set(i, j, min + random.nextDouble() * range);
        return m;
    }
}
