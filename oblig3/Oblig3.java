import java.util.Arrays;
import java.util.Locale;

public class Oblig3 {
    public static void main(String[] args) {
        //usage
        if (args.length != 2) {
            System.out.println("Usage: java Oblig3 <n> (Were <n> has to be a number grater than 16) <cores> (were <cores> is number of cores used, if 0 max CPU cores used)");
            System.exit(0);
        }

        //n greater than 16
        int n = Integer.parseInt(args[0]);
        if (n < 16) {
            System.out.println("n must be greater than 16");
            System.exit(0);
        }

        //finding number of cores
        int numCores;
        if (args[1].equals("0")) {
            numCores = Runtime.getRuntime().availableProcessors();
        } else {
            numCores = Integer.parseInt(args[1]);
        }

        System.out.println("Number of threads: " + numCores);

        //sequental version of the sieve of Eratosthenes
        double[] timeingSeq = new double[7];
        int[] primesSeq = null;
        for (int i = 0; i < 7; i++) {
            double timeSeq = System.nanoTime();
            SieveOfEratosthenes soe = new SieveOfEratosthenes(n);
            primesSeq = soe.getPrimes();
            timeingSeq[i] = (System.nanoTime()-timeSeq)/1000000.0;
        }
        Arrays.sort(timeingSeq);
        System.out.println("\nMedian time for Seq runs: " + timeingSeq[(timeingSeq.length/2)-1] + "ms");

        //parallel version of the sieve of Eratosthenes
        double[] timeingPara = new double[7];
        int[] primesPara = null;
        for (int i = 0; i < 7; i++) {
            double timePara = System.nanoTime();
            ParaSieve para = new ParaSieve(n, numCores);
            primesPara = para.getPrimes();
            timeingPara[i] = (System.nanoTime()-timePara)/1000000.0;
        }
        Arrays.sort(timeingPara);
        System.out.println("Median time for Para runs: " + timeingPara[(timeingPara.length/2)-1] + "ms");
        System.out.println("Runs speed up: " + String.format(Locale.US, "%.2f", timeingSeq[(timeingSeq.length/2)-1]/timeingPara[(timeingPara.length/2)-1]) + "x");

        if (!Arrays.equals(primesSeq, primesPara)) {
            System.out.println("Fant feil primes !!!!!!!");
            System.out.println("Seq primes: " + Arrays.toString(primesSeq));
            System.out.println("Para primes: " + Arrays.toString(primesPara));
            System.exit(1);
        } else {
            System.out.println("Fant riktig primes");
        }

        //factorization seq
        double[] timeingFacSeq = new double[7];
        Oblig3Precode precodeSeq = null;
        for (int i = 0; i < 7; i++) {
            double timeFacSeq = System.nanoTime();
            precodeSeq = factorizeSeq(primesSeq, n);
            timeingFacSeq[i] = (System.nanoTime()-timeFacSeq)/1000000.0;
            precodeSeq.writeFactors();
        }
        Arrays.sort(timeingFacSeq);
        System.out.println("\nMedian time for factorization Seq runs: " + timeingFacSeq[(timeingFacSeq.length/2)-1] + "ms");

        //factorization para
        double[] timeingFacPara = new double[7];
        Oblig3Precode precodePara = null;
        for (int i = 0; i < 7; i++) {
            double timeFaqPara = System.nanoTime();
            FacPara fac = new FacPara(primesPara, n, numCores);
            precodePara = fac.factorize();
            timeingFacPara[i] = (System.nanoTime()-timeFaqPara)/1000000.0;
            precodePara.writeFactors();
        }
        Arrays.sort(timeingFacPara);
        System.out.println("Median time for factorization Para runs: " + timeingFacPara[(timeingFacPara.length/2)-1] + "ms");
        System.out.println("Factorization speed up: " + String.format(Locale.US, "%.2f", timeingFacSeq[(timeingFacSeq.length/2)-1]/timeingFacPara[(timeingFacPara.length/2)-1]) + "x");

        //checking if they both precodes are matching
        if (!precodeSeq.factors.equals(precodePara.factors)) {
            System.out.println(precodeSeq.factors.toString() + "\n");
            System.out.println(precodePara.factors.toString());
            System.out.println("not matching!!!!!!!");
        } else System.out.println("Matching factors!!!");
    }

    public static Oblig3Precode factorizeSeq(int[] primes, int n) {
        Oblig3Precode precode = new Oblig3Precode(n);
        long start = (long) n * n - 1;
        long end = (long) n * n - 100;
        
        for (long i = start; i >= end; i--) {
            long number = i;
            for (int prime : primes) {
                while (number % prime == 0) {
                    precode.addFactor(i, prime);
                    number /= prime;
                }
            }
            if (number != 1) {
                precode.addFactor(i, number);
            }
        }
        return precode;
    }
}