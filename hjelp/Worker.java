import java.util.concurrent.CyclicBarrier;

public class Worker implements Runnable {
    int id, start, end;
    CyclicBarrier barrier;
    int[] x, y, indices;
    int[][] buckets;
    double[][] distBuckets;
    IntList above, under;

    public Worker(int i, CyclicBarrier barrier, int[] x, int[] y, int start, int end, int[][] buckets, int[] indices, double[][] distBuckets) {
        id = i;
        this.barrier = barrier;
        this.x = x;
        this.y = y;
        this.start = start;
        this.end = end;
        this.buckets = buckets;
        this.indices = indices;
        this.distBuckets = distBuckets;
        above = new IntList((end - start)/2);
        under = new IntList((end - start)/2);
    }

    @Override
    public void run() {
        int maxX = x[start];
        int indexMaxX = start;
        int minX = maxX;
        int indexMinX = start;
        for (int i = start + 1; i < end; i++) {
            int current = x[i];
            if (current > maxX) {
                maxX = current;
                indexMaxX = i;
            }
            if (current < minX) {
                minX = current;
                indexMinX = i;
            }
        }

        buckets[id][0] = maxX;
        buckets[id][1] = indexMaxX;
        buckets[id][2] = minX;
        buckets[id][3] = indexMinX;

        try {   barrier.await();    } catch (Exception e) {}
        try {   barrier.await();    } catch (Exception e) {}
        int minIndex = 0, maxIndex = 0;
        double distMin = distToLine(indexMaxX, indexMinX, minIndex);
        double distMax = distMin;
        for (int i = start; i < end; i++) {
            double distNew = distToLine(indices[0], indices[1], i);
            if (distNew < 0) above.add(i);
            if (distNew > 0) under.add(i);
            if (distNew < distMin) {
                distMin = distNew;
                minIndex = i;
            }
            if (distNew > distMax) {
                distMax = distNew;
                maxIndex = i;
            }
        }

        distBuckets[id][0] = distMin;
        distBuckets[id][1] = minIndex;
        distBuckets[id][2] = distMax;
        distBuckets[id][3] = maxIndex;
        try {   barrier.await();    } catch (Exception e) {}
    }
    
    private double distToLine(int indexLineStart, int indexLineEnd, int indexNew){
        int x1 = x[indexLineStart];
        int y1 = y[indexLineStart];
        
        int x2 = x[indexLineEnd];
        int y2 = y[indexLineEnd];
        
        int newX = x[indexNew];
        int newY = y[indexNew];
        
        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2*x1 - y1*x2;
        
        double dist = (a * newX + b * newY + c ) / Math.sqrt(a * a + b * b);
        return dist;
    }
}
