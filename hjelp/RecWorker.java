import java.util.concurrent.atomic.AtomicInteger;

public class RecWorker implements Runnable {
    static AtomicInteger count = new AtomicInteger(0);
    int id, indexLineStart, indexLineEnd;
    int[] x, y;
    IntList result = new IntList(100);
    AtomicInteger numberOfFreeThreads;
    IntList list;

    public RecWorker(int[] x, int[] y, AtomicInteger numberOfFreeThreads) {
        this.id = count.getAndIncrement();
        this.x = x;
        this.y = y;
        this.numberOfFreeThreads = numberOfFreeThreads;
    }

    @Override
    public void run() {
        result = recursion(list, indexLineStart, indexLineEnd);
    }

    public IntList recursion(IntList list, int lineStart, int lineEnd) {
        IntList indexList = new IntList(10);
        int index = 0;
        double dist = 0;
        boolean foundOuter = false;
        IntList newList = new IntList(list.len/2);
     
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
                RecWorker worker = new RecWorker(x, y, numberOfFreeThreads);
                worker.indexLineStart = index;
                worker.indexLineEnd = lineEnd;
                worker.list = newList;
                Thread thread = new Thread(worker);
                thread.start();

                IntList myList = recursion(newList, lineStart, index);
                try {   thread.join();  } catch (Exception e) {}
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
