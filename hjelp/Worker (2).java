import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    static AtomicInteger count = new AtomicInteger(0);
    int id, start, end, indexLineStart, indexLineEnd;
    CyclicBarrier barrier;
    int[] x, y, indices;
    int[][] buckets;
    double[][] distBuckets;
    IntList above, under, result;
    AtomicInteger numberOfFreeThreads;
    IntList list;

    public Worker(int i, CyclicBarrier barrier, int[] x, int[] y, int start, int end, int[][] buckets, int[] indices, double[][] distBuckets, AtomicInteger numberOfFreeThreads) {
        id = i;
        this.barrier = barrier;
        this.x = x;
        this.y = y;
        this.start = start;
        this.end = end;
        this.buckets = buckets;
        this.indices = indices;
        this.distBuckets = distBuckets;
        this.numberOfFreeThreads = numberOfFreeThreads;
        above = new IntList((end - start) / 2);
        under = new IntList((end - start) / 2);
        result = new IntList(100);
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

        result = recursion(list, indexLineStart, indexLineEnd);
    }

    public IntList recursion(IntList list, int lineStart, int lineEnd) {
        IntList indexList = new IntList(10);
        int index = 0;
        double dist = 0;
        boolean foundOuter = false;
        if (list == null) return indexList;
        IntList newList = new IntList(list.len / 2);

        for (int i = 0; i < list.len; i++) {
            int checkingIndex = list.get(i);
            if (checkingIndex == lineStart || checkingIndex == lineEnd) {
                continue;
            }

            double d = distToLine(lineStart, lineEnd, checkingIndex);
            if (d < 0) newList.add(checkingIndex);
            if (d < dist) {
                dist = d;
                index = checkingIndex;
                foundOuter = true;
            }
        }

        if (foundOuter) {
            if (numberOfFreeThreads.get() > 0) {
                numberOfFreeThreads.getAndDecrement();
                Worker worker = new Worker(id, barrier, x, y, 0, newList.len, buckets, indices, distBuckets, numberOfFreeThreads);
                worker.indexLineStart = index;
                worker.indexLineEnd = lineEnd;
                worker.list = newList;
                Thread thread = new Thread(worker);
                thread.start();

                IntList myList = recursion(newList, lineStart, index);
                try {
                    thread.join();
                } catch (Exception e) {
                }
                numberOfFreeThreads.getAndIncrement();
                indexList.append(myList);
                indexList.add(index);
                indexList.append(worker.result);

            } else {
                IntList list1 = recursion(newList, indexLineStart, index);
                IntList list2 = recursion(newList, index, indexLineEnd);
                indexList.append(list1);
                indexList.add(index);
                indexList.append(list2);
            }
            return indexList;
        }

        return indexList;
    }

    private double distToLine(int indexLineStart, int indexLineEnd, int indexNew) {
        int x1 = x[indexLineStart];
        int y1 = y[indexLineStart];

        int x2 = x[indexLineEnd];
        int y2 = y[indexLineEnd];

        int newX = x[indexNew];
        int newY = y[indexNew];

        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2 * x1 - y1 * x2;

        double dist = (a * newX + b * newY + c) / Math.sqrt(a * a + b * b);
        return dist;
    }
}