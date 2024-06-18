import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Parallel2 extends ConvexHull {    
    Parallel2(final int n, final int seed, final NPunkter17 p) {
        super(n, seed, p);
    }

    public IntList findConvexHull(int cores) {
        RecWorker.count.set(0);
        IntList convexHull = new IntList((int) Math.sqrt(n));
        Thread[] threads = new Thread[cores];
        Worker[] workers = new Worker[cores];
        CyclicBarrier barrier = new CyclicBarrier(cores +1);

        int start = 0;
        int chunkLenght = n / cores;

        int[][] buckets = new int[cores][4];
        int[] indices = new int[2];
        double[][] distBuckets = new double[cores][4];

        for (int i = 0; i < cores; i++) {
            int end = start + chunkLenght;
            workers[i] = new Worker(i, barrier, x, y, start, end, buckets, indices, distBuckets);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
            start = end;
        }
        workers[cores - 1] = new Worker(cores - 1, barrier, x, y, start, n, buckets, indices, distBuckets);
        threads[cores - 1] = new Thread(workers[cores - 1]);
        threads[cores - 1].start();

        int maxX = x[0];
        int minX = maxX;
        int indexMax = 0, indexMin = 0;
        IntList above = new IntList(n/2);
        IntList under = new IntList(n/2);
        try {   barrier.await();    } catch (Exception e) {}
        for (int i = 0; i < cores; i++) {
            int[] newBucket = buckets[i];
            if (newBucket[0] > maxX) {
                maxX = newBucket[0];
                indexMax = newBucket[1];
            }
            if (newBucket[2] < minX) {
                minX = newBucket[2];
                indexMin = newBucket[3];
            }
        }

        indices[0] = indexMax;
        indices[1] = indexMin;
        try {   barrier.await();    } catch (Exception e) {}

        //reason it is four is that is four sectors i am searching in
        Thread[] recThreads = new Thread[4];
        RecWorker[] recWorkers = new RecWorker[4];
        AtomicInteger freeThreads = new AtomicInteger(cores - 4);
        for (int i = 0; i < 4; i++) {
            recWorkers[i] = new RecWorker(x, y, freeThreads);
            recThreads[i] = new Thread(recWorkers[i]);
        }

        try {   barrier.await();    } catch (Exception e) {}
        double minDistRight = 0, maxDistLeft = 0;
        int minDistRightIndex = 0, maxDistLeftIndex = 0;
        for (int i = 0; i < distBuckets.length; i++) {
            above.append(workers[i].above);
            under.append(workers[i].under);
            double[] newDistBuckets = distBuckets[i];
            if (newDistBuckets[0] < minDistRight) {
                minDistRight = newDistBuckets[0];
                minDistRightIndex = (int) newDistBuckets[1];
            }
            if (newDistBuckets[2] > maxDistLeft) {
                maxDistLeft = newDistBuckets[2];
                maxDistLeftIndex = (int) newDistBuckets[3];
            }
        }

        recWorkers[0].indexLineStart = indexMax;
        recWorkers[0].indexLineEnd = minDistRightIndex;
        recWorkers[0].list = above;

        recWorkers[1].indexLineStart = minDistRightIndex;
        recWorkers[1].indexLineEnd = indexMin;
        recWorkers[1].list = above;

        recWorkers[2].indexLineStart = indexMin;
        recWorkers[2].indexLineEnd = maxDistLeftIndex;
        recWorkers[2].list = under;

        recWorkers[0].indexLineStart = maxDistLeftIndex;
        recWorkers[0].indexLineEnd = indexMax;
        recWorkers[0].list = under;

        for (int i = 0; i < 4; i++) {
            recThreads[i].start();
        }
        convexHull.add(indexMax);

        for (int i = 0; i < 4; i++) {
            try {   recThreads[i].join();     } catch (Exception e) {}
        }

        convexHull.append(recWorkers[0].result);
        convexHull.add(minDistRightIndex);

        convexHull.append(recWorkers[1].result);
        convexHull.add(indexMin);

        convexHull.append(recWorkers[2].result);
        convexHull.add(maxDistLeftIndex);

        convexHull.append(recWorkers[3].result);
        convexHull.add(indexMax);

        return convexHull;
    }
}