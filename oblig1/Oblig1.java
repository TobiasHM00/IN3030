import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class Oblig1 {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);

        Random r = new Random();
        int[] array = new int[n];

        for(int i = 0; i < n; i++) {
            array[i] = r.nextInt();
        }

        int[] sequArr = new int[n];
        int[] arr = new int[n];
        int[] paraArr = new int[n];

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.##", symbols);

        //regular Java Arrays.sort
        System.out.print("Doing Java sort runs...");
        ArrayList<Double> javasortTiming = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            arr = Arrays.copyOf(array, n);
            double timeArr = System.nanoTime();
            Arrays.sort(arr);
            timeArr = (System.nanoTime() - timeArr)/1000000.0;    
            javasortTiming.add(timeArr);
        }
        System.out.print("done\n");
        System.out.println("Java sort run times:");
        System.out.println(javasortTiming.toString());
        javasortTiming.sort(Comparator.naturalOrder());
        System.out.println("Java Array.sort time: " + javasortTiming.get(3) + " ms");
        System.out.println("Java Array.sort speedup: " + df.format(javasortTiming.get(6)/javasortTiming.get(0)) + "x");


        //sequential task
        System.out.print("\n\nDoing sequential runs...");
        ArrayList<Double> sequentialTiming = new ArrayList<>();
        for (int x = 0; x < 7; x++) {
            sequArr = Arrays.copyOf(array, n);
            double timeSeq = System.nanoTime();
            sequential(sequArr, k, n);
            timeSeq = (System.nanoTime() - timeSeq)/1000000.0;
            sequentialTiming.add(timeSeq);
        }
        System.out.print("done\n");
        System.out.println("Sequential time runs:");
        System.out.println(sequentialTiming.toString());
        sequentialTiming.sort(Comparator.naturalOrder());
        System.out.println("Sequential time: " + sequentialTiming.get(3) + " ms");
        System.out.println("Sequential speedup: " + df.format(sequentialTiming.get(6)/sequentialTiming.get(0)) + "x");


        //parallel task
        System.out.print("\n\nDoing parallel runs...");
        ArrayList<Double> parallelTiming = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            paraArr = Arrays.copyOf(array, n);
            double timePara = System.nanoTime();
            InsertionSort.threadedSort(paraArr, n, k);
            timePara = (System.nanoTime() - timePara)/1000000.0;
            parallelTiming.add(timePara);
        }
        System.out.print("done\n");
        System.out.println("Parallel time runs:");
        System.out.println(parallelTiming.toString());
        parallelTiming.sort(Comparator.naturalOrder());
        System.out.println("Parallel time: " + parallelTiming.get(3) + " ms");
        System.out.println("Parallel speedup: " + df.format(parallelTiming.get(6)/parallelTiming.get(0)) + "x\n\n");

        
        //testing for correctness
        for(int i = 0; i < k; i++) {
            if (sequArr[i] != arr[n-1-i]) { 
                System.out.println("Error between sequArr and arr!!!!!!!!!!");
            }
            if (paraArr[i] != arr[n-1-i]) {
                System.out.println(i+": Error between paraArr and arr!!!!!!!!!!");
                System.out.println("ParaArr: " + paraArr[i] + "   Arr: " + arr[n-1-i]);
            }
        }
    }

    static void sequential (int[] a, int k, int n) {
        insertSort(a, 0, k);
            for (int i = k; i < n; i++) {
                if (a[k-1] < a[i]) {
                    int temp = a[i];
                    a[i] = a[k-1];
                    a[k-1] = temp;
                    int key = a[k-1];
                    int j = k-2;
                    while (j >= 0 && a[j] < key) {
                        a[j+1] = a[j];
                        j--;
                    }
                    a[j+1] = key;
                }
            }
    }

    static void insertSort (int[] a, int v, int h) {
        int i, t;
        for (int k = v; k < h; k++) {
            // invariant: a [v..k] is now sorted decending (largest first)
            t = a[k + 1];
            i = k;
            while (i >= v && a[i] < t) {
                a[i + 1] = a[i];
                i--;
            }
            a[i + 1] = t;
        }
    }
}

//parallel implementation using the same main bc why not
class InsertionSort {
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    public static void threadedSort(int[] a, int n, int k) {
        ArrayList<SortThreads> threads = new ArrayList<>();
        boolean exact = n%MAX_THREADS == 0;
        int chunkSize = exact? n/MAX_THREADS: n/(MAX_THREADS-1);

        CyclicBarrier barrier = new CyclicBarrier(MAX_THREADS);

        //create threads
        for (int i = 0; i < MAX_THREADS; i++) {
            int start = i*chunkSize;
            int end = (i+1)*chunkSize;
            SortThreads t = new SortThreads(a, start, end, k, barrier);
            threads.add(t);
            t.start();
        }
        
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {}
        }

        //Need to find the k largest numbers 
        for (int i = 0; i < MAX_THREADS; i++) {
            int blockStart = i*chunkSize;
            int blockEnd = blockStart + k + 1;
            for (int j = blockStart; j < blockEnd; j++) {
                if (a[j] > a[k-1]) {
                    int temp = a[k-1];
                    a[k-1] = a[j];
                    a[j] = temp;
                    //want to use a simplefied version of insertSort only to find the right spot for the newly arrived element
                    //Could not get it to work so i just used the insertSort methode we got.
                    //This might hurt the runtime but i can't find another way to make it work.
                    //Oblig1.insertSort(a, 0, k);
                    //sort the first k numbers correctly
                    int key = a[k-1];
                    int l = k-2;
                    while (l >= 0 && a[l] < key) {
                        a[l+1] = a[l];
                        l--;
                    }
                    a[l+1] = key;
                }
            }
        }
    }
}

class SortThreads extends Thread {
    int[] a;
    int start, end, k;
    CyclicBarrier barrier;
    
    SortThreads(int[] array, int start, int n, int k, CyclicBarrier b) {
        a = array;
        this.start = start;
        end = n;
        this.k = k;
        barrier = b;
    }

    @Override
    public void run() {
        Oblig1.insertSort(a, start, k);
        k = start + k; //to access the correct portion of a
        for (int i = k; i < end; i++) {
            if (a[i] > a[k-1]) {
                int temp = a[k-1];
                a[k-1] = a[i];
                a[i] = temp;
                int key = a[k-1];
                int j = k-2;
                while (j >= 0 && a[j] < key) {
                    a[j+1] = a[j];
                    j--;
                }
                a[j+1] = key;
            }
        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
        } catch (BrokenBarrierException e) {}
    }
}