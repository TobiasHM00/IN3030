import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WaitAndSwap {
    static Semaphore swapper = new Semaphore(1, true);
    static Semaphore holding = new Semaphore(-1, true);
    static int N = 4;
    static int globalCall = 0;

    static void waitAndSwap(int id) {
        try {
            globalCall++;
            int count = globalCall;
            if (count % 2 != 0) {
                holding.release();
                holding.acquire();
            }
            swapper.acquire();
            swapper.release();

            System.out.println("Thread: " + id + ", Call: " + count);
        } catch (Exception e) {System.out.println("Exception"); return;}
    }

    public static void main(String[] args) {
        int numberofthreads = 0;
        if (args.length < 1) {
            System.out.println("usage: java WaitNextC <number of threads> <num iterations>");
            System.out.println("   only the first arguement, number of threads, is required an should be an even number; defaults are:");
            System.out.println("   iterations: " + N);
            System.exit(0);
        }
        if (args.length >= 1) {
            numberofthreads = Integer.parseInt(args[0]);
            if (numberofthreads % 2 != 0) {
                System.out.println("Number of threads can't be an odd number");
                System.exit(0);
            }
            System.out.println("threads: " + numberofthreads);
        }
        if (args.length >= 2) {
            N = Integer.parseInt(args[1]);
            System.out.println("iterations: " + N);
        }

        for (int i = 0; i < numberofthreads; i++) {
            (new Thread(new Worker(i+1, N))).start();
        }
    }
}

class Worker implements Runnable {
    int id;
    final int N;
    Worker(int id, int n) {
        this.id = id; 
        this.N = n;
    }

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep((long) id*500); // let them start in order.
        } catch (Exception e) { return;}; 
        for (int i = 1; i <= N; i++)
            WaitAndSwap.waitAndSwap(id);
    }   
}