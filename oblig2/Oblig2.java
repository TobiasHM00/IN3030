import java.util.Locale;

public class Oblig2 {
    static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Oblig2 <Random class seed> <matrix_size>");
            System.exit(-1);
        }

        int seed = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);

        double[][] a = Oblig2Precode.generateMatrixA(seed, n);
        double[][] b = Oblig2Precode.generateMatrixB(seed, n);
        System.out.println("Matrix sizes: " + n + "x" + n);
        System.out.println("Cores using: " + MAX_THREADS);

        //sequential not transposed
        double[] timeing1 = new double[7];
        double[][] sequalNoTransRes = null;
        for (int i = 0; i < 7; i++) {
            double time1 = System.nanoTime();
            sequalNoTransRes = sequalNoTrans(a, b, n, seed);
            time1 = (System.nanoTime()-time1)/1000000.0;
            timeing1[i] = time1;
        }
        System.out.println("\nSequential not transposed time: " + timeing1[(timeing1.length-1)/2] + "ms");
        Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, sequalNoTransRes);

        //sequential A transposed
        double[] timeing2 = new double[7];
        double[][] sequalATransRes = null;
        for (int i = 0; i < 7; i++) {
            double time2 = System.nanoTime();
            sequalATransRes = sequalATrans(a, b, n, seed);
            time2 = (System.nanoTime()-time2)/1000000.0;
            timeing2[i] = time2;
        }
        System.out.println("Sequential A transposed time: " + timeing2[(timeing2.length-1)/2] + "ms");
        Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_A_TRANSPOSED, sequalATransRes);
        
        //sequential B transposed
        double[] timeing3 = new double[7];
        double[][] sequalBTransRes = null;
        for (int i = 0; i < 7; i++) {
            double time3 = System.nanoTime();
            sequalBTransRes = sequalBTrans(a, b, n, seed);
            time3 = (System.nanoTime()-time3)/1000000.0;
            timeing3[i] = time3;
        }
        System.out.println("Sequential B transposed time: " + timeing3[(timeing3.length-1)/2] + "ms");
        Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_B_TRANSPOSED, sequalBTransRes);

        //parallel not transposed
        double[] timeing4 = new double[7];
        double[][] paraNoTransRes = null;
        for (int i = 0; i < 7; i++) {
            double time4 = System.nanoTime();
            paraNoTransRes = paraNoTrans(a, b, n, seed);
            time4 = (System.nanoTime()-time4)/1000000.0;
            timeing4[i] = time4;
        }
        System.out.println("Parallel no transpose time: " + timeing4[(timeing4.length-1)/2] + "ms");
        Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_NOT_TRANSPOSED, paraNoTransRes);

        //parallel A transpoesed
        double[] timeing5 = new double[7];
        double[][] paraATransRes = null;
        for (int i = 0; i < 7; i++) {
            double time5 = System.nanoTime();
            paraATransRes = paraATrans(a, b, n, seed);
            time5 = (System.nanoTime()-time5)/1000000.0;
            timeing5[i] = time5;
        }
        System.out.println("Parallel A transpose time: " + timeing5[(timeing5.length-1)/2] + "ms");
        Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_A_TRANSPOSED, paraATransRes);

        //parallel B transposed
        double[] timeing6 = new double[7];
        double[][] paraBTransRes = null;
        for (int i = 0; i < 7; i++) {
            double time6 = System.nanoTime();
            paraBTransRes = paraBTrans(a, b, n, seed);
            time6 = (System.nanoTime()-time6)/1000000.0;
            timeing6[i] = time6;
        }
        System.out.println("Parallel B transpose time: "+ timeing6[(timeing6.length-1)/2] + "ms");
        Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_B_TRANSPOSED, paraBTransRes);

        //speedup
        System.out.println("\nSpeedup achieved for seqClassic vs. seqATrans: " + String.format(Locale.US, "%.2f", timeing1[(timeing1.length-1)/2]/timeing2[(timeing2.length-1)/2]) + "x");
        System.out.println("Speedup achieved for seqClassic vs. seqBTrans: " + String.format(Locale.US, "%.2f", timeing1[(timeing1.length-1)/2]/timeing3[(timeing3.length-1)/2]) + "x");
        System.out.println("Speedup achieved for seqClassic vs. paraNoTrans: " + String.format(Locale.US, "%.2f", timeing1[(timeing1.length-1)/2]/timeing4[(timeing4.length-1)/2]) + "x");
        System.out.println("Speedup achieved for seqClassic vs. paraATrans: " + String.format(Locale.US, "%.2f", timeing1[(timeing1.length-1)/2]/timeing5[(timeing5.length-1)/2]) + "x");
        System.out.println("Speedup achieved for seqClassic vs. paraBTrans: " + String.format(Locale.US, "%.2f", timeing1[(timeing1.length-1)/2]/timeing6[(timeing6.length-1)/2]) + "x\n");

        //checking for correctness
        checkAndPrintResult("Sequential and parallel with no transpose", checkForCorrectness(sequalNoTransRes, paraNoTransRes, n));
        checkAndPrintResult("Sequential and parallel with A transposed", checkForCorrectness(sequalATransRes, paraATransRes, n));
        checkAndPrintResult("Sequential and parallel with B transposed", checkForCorrectness(sequalBTransRes, paraBTransRes, n));
    }

    public static double[][] sequalNoTrans(double[][] a, double[][] b, int n, int seed) {
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    result[i][j] += a[i][k] * b[k][j];

        return result;
    }

    public static double[][] sequalATrans(double[][] a, double[][] b, int n, int seed) {
        double[][] result = new double[n][n];
        double[][] transposed = transpose(a, n);

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    result[i][j] += transposed[k][i] * b[k][j];

        return result;
    }

    public static double[][] sequalBTrans(double[][] a, double[][] b, int n, int seed) {
        double[][] result = new double[n][n];
        double[][] transposed = transpose(b, n);

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    result[i][j] += a[i][k] * transposed[j][k];

        return result;
    }

    public static double[][] paraNoTrans(double[][] a, double[][] b, int n, int seed) {
        double[][] result = new double[n][n];
        Thread[] threads = new Thread[MAX_THREADS];
        int segmentSize = n/MAX_THREADS;

        for (int i = 0; i < MAX_THREADS; i++) {
            int start = i * segmentSize;
            int end = (i == MAX_THREADS - 1) ? n - 1 : (start + segmentSize - 1);
            threads[i] = new Thread(new WorkerNoTrans(a, b, result, n, start, end));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {}
        }

        return result;
    }

    public static double[][] paraATrans(double[][] a, double[][] b, int n, int seed) {
        double[][] result = new double[n][n];
        double[][] transposed = transpose(a, n);
        Thread[] threads = new Thread[MAX_THREADS];
        int segmentSize = n/MAX_THREADS;

        for (int i = 0; i < MAX_THREADS; i++) {
            int start = i *segmentSize;
            int end = (i == MAX_THREADS - 1) ? n - 1 : (start + segmentSize - 1);
            threads[i] = new Thread(new WorkerATrans(transposed, b, result, n, start, end));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {}
        }

        return result;
    }

    public static double[][] paraBTrans(double[][] a, double[][] b, int n, int seed) {
        double[][] result = new double[n][n];
        double[][] transposed = transpose(b, n);
        Thread[] threads = new Thread[MAX_THREADS];
        int segmentSize = n/MAX_THREADS;

        for (int i = 0; i < MAX_THREADS; i++) {
            int start = i *segmentSize;
            int end = (i == MAX_THREADS - 1) ? n - 1 : (start + segmentSize - 1);
            threads[i] = new Thread(new WorkerBTrans(a, transposed, result, n, start, end));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {}
        }

        return result;
    }

    public static double[][] transpose(double[][] matrix, int n) {
        double[][] trans = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                trans[i][j] = matrix[j][i];
            }
        return trans;
    }

    public static boolean checkForCorrectness(double[][] a, double[][] b, int n) {        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j] != b[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void checkAndPrintResult(String message, boolean bool) {
        if (bool) {
            System.out.println(message + " is matching!");
        } else {
            System.out.println(message + " is not matching!");
        }
    }

    //Runnable classes for my threads
    static class WorkerNoTrans implements Runnable {
        int start, end, n;
        double[][] a, b, result;

        WorkerNoTrans(double[][] a, double[][] b, double[][] result, int n, int start, int end) {
            this.start = start;
            this.end = end;
            this.n = n;
            this.a = a;
            this.b = b;
            this.result = result;
        }
 
        @Override
        public void run() {
            for (int i = start; i <= end; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        result[i][j] += a[i][k] * b[k][j];
                    }
                }
            }
        }
    }

    static class WorkerATrans implements Runnable {
        int start, end, n;
        double[][] a, b, result;

        WorkerATrans(double[][] a, double[][] b, double[][] result, int n, int start, int end) {
            this.start = start;
            this.end = end;
            this.n = n;
            this.a = a;
            this.b = b;
            this.result = result;
        }
 
        @Override
        public void run() {
            for (int i = start; i <= end; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        result[i][j] += a[k][i] * b[k][j];
                    }
                }
            }
        }
    }

    static class WorkerBTrans implements Runnable {
        int start, end, n;
        double[][] a, b, result;

        WorkerBTrans(double[][] a, double[][] b, double[][] result, int n, int start, int end) {
            this.start = start;
            this.end = end;
            this.n = n;
            this.a = a;
            this.b = b;
            this.result = result;
        }
 
        @Override
        public void run() {
            for (int i = start; i <= end; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        result[i][j] += a[i][k] * b[j][k];
                    }
                }
            }
        }
    }
}