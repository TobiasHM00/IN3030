import java.util.concurrent.CyclicBarrier;

public class ParaSieve {
    int n, root, numOfPrimes, threadNum;
    byte[] oddNumbers;

    public ParaSieve(int n, int cores) {
        this.n = n;
        threadNum = cores;
        root = (int) Math.sqrt(n);
        oddNumbers = new byte[(n/16) + 1];
    }

    int[] getPrimes() {
        if (n <= 1)
            return new int[0];

        sieve();

        return collectPrimes();
    }

    private void sieve() {
        Thread[] threads = new Thread[threadNum];
        CyclicBarrier barrier = new CyclicBarrier(threadNum);
        int[] numOfPrimesArr = new int[threadNum];
        int range = (n - 3) / threadNum;
        
        for (int i = 0; i < threadNum; i++) {
            int start = 3 + i * range;
            int end = (i == threadNum - 1) ? n : start + range;
            threads[i] = new Thread(new Worker(i, n, root, oddNumbers, barrier, start, end, numOfPrimesArr));
            threads[i].start();
        }

        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        }

        numOfPrimes = 1;
        for (int i = 0; i < numOfPrimesArr.length; i++) {
            numOfPrimes += numOfPrimesArr[i];
        }
    }

    private int[] collectPrimes() {
        int[] primes = new int[numOfPrimes];

        primes[0] = 2;

        int j = 1;

        for (int i = 3; i <= n; i += 2)
            if (isPrime(i))
                primes[j++] = i;

        return primes;
    }

    private boolean isPrime(int num) {
        int bitIndex = (num % 16) / 2;
        int byteIndex = num / 16;
        return (oddNumbers[byteIndex] & (1 << bitIndex)) == 0;
    }
}

class Worker implements Runnable {
    int n, root, end, start, id;
    byte[] oddNumbers;
    CyclicBarrier barrier;
    int[] numOfPrimesArr;

    public Worker(int id, int n, int root, byte[] oddN, CyclicBarrier b, int s, int e, int[] pc) {
        this.n = n;
        this.root = root;
        oddNumbers = oddN;
        barrier = b;
        start = s;
        end = e;
        this.id = id;
        numOfPrimesArr = pc;
    }

    @Override
    public void run() {
        int prime = 3;
        
        while (prime != -1 && prime <= root) {
            traverse(prime);
            prime = nextPrime(prime);
        }
        
        try {
            barrier.await();
        } catch (Exception e) {}

        int count = 0;
        for (int i = start; i < end; i += 2) {
            if (isPrime(i)) {
                count++;
            }
        }

        numOfPrimesArr[id] = count;
    }

    private void traverse(int prime) {
        for (int i = prime * prime; i <= end; i += prime * 2)
            mark(i);
    }

    private int nextPrime(int prev) {
        for (int i = prev + 2; i <= root; i += 2)
            if (isPrime(i))
                return i;
        return -1;
    }

    private boolean isPrime(int num) {
        int bitIndex = (num % 16) / 2;
        int byteIndex = num / 16;
    
        return (oddNumbers[byteIndex] & (1 << bitIndex)) == 0;
    }

    private void mark(int num) {
        int bitIndex = (num % 16) / 2;
        int byteIndex = num / 16;
        oddNumbers[byteIndex] |= (1 << bitIndex);
    }
}