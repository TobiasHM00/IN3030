import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class FacPara {
    int cores, n;
    int[] primes;

    FacPara (int[] primes, int n, int cores) {
        this.cores = cores;
        this.n = n;
        this.primes = primes;
    }
    
    public Oblig3Precode factorize() {
        Oblig3Precode precode = new Oblig3Precode(n);
        ReentrantLock lock = new ReentrantLock();
        Thread[] threads = new Thread[cores];
        long[] numbersToFactorize = new long[100];
    
        for (int i = 1; i <= 100; i++) {
            numbersToFactorize[i-1] = (long) n * n - i;
        }
    
        for (int i = 0; i < cores; i++) {
            int index = 0;
            int[] share = new int[primes.length / cores + 1];
            for (int j = i; j < primes.length; j += cores) {
                share[index] = primes[j];
                index++;
            }
            threads[i] = new Thread(new FacWorker(precode, numbersToFactorize, lock, share));
            threads[i].start();
        }
    
        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {}
        }

        for (long factor : numbersToFactorize) {
            LinkedList<Long> factors = precode.factors.get(factor);
            
            if (factors == null) {
                precode.addFactor(factor, factor);
            } else {
                long remaining = factor;
                for (long fac : factors) {
                    remaining /= fac;
                }
                if (remaining != 1) {
                    while (!isPrime(remaining)) {
                        long smallestPrimeFactor = smallestPrimeFactor(remaining);
                        long factor1 = smallestPrimeFactor;
                        long factor2 = remaining / smallestPrimeFactor;
                        precode.addFactor(factor, factor1);
                        remaining = factor2;
                    }
                    precode.addFactor(factor, remaining);
                }
            }
        }
        return precode;
    }

    private boolean isPrime(long num) {
        if (num <= 1) return false;
        if (num <= 3) return true;
        if (num % 2 == 0 || num % 3 == 0) return false;
        for (long i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0) return false;
        }
        return true;
    }
    
    private long smallestPrimeFactor(long num) {
        for (long i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                return i;
            }
        }
        return num;
    }
}

class FacWorker implements Runnable {
    int[] threadShare; 
    Oblig3Precode precode;
    ReentrantLock lock;
    long[] toBeFac;

    FacWorker (Oblig3Precode pre, long[] tBF, ReentrantLock l, int[] TS) {
        precode = pre;
        lock = l;
        toBeFac = tBF;
        threadShare = TS;
    }

    @Override
    public void run() {
        for (int i = 0; i < toBeFac.length; i++) {
            long start = toBeFac[i];
            long current = start;

            for (int pos = 0; pos < threadShare.length; pos++) {
                if (threadShare[pos] == 0) break;
                if (current % threadShare[pos] == 0) {
                    lock.lock();
                    precode.addFactor(start, threadShare[pos]);
                    lock.unlock();
                    current = current / threadShare[pos];
                } else if (current == 1) break;
            }
        }
    }
}