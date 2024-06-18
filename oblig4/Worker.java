public class Worker implements Runnable {
    int maxDepth = 3;
    int right, left, depth;
    IntList list, convex;
    int x[], y[];

    Worker(int p1, int p2, int depth, IntList points, IntList convexHull, int[] x, int[] y) {
        right = p2;
        left = p1;
        this.depth = depth;
        list = points;
        convex = convexHull;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        if (depth >= maxDepth) {
            quickHull(right, left, list, convex);
        } else {
            int a = y[left] - y[right];
            int b = x[right] - x[left];
            int c = (y[right] * x[left]) - (y[left] * x[right]);
    
            int maxDistance = 0;
            int maxPoint = -1;
    
            IntList pointsToLeft = new IntList();
    
            for (int i = 0; i < list.len; i++) {
                int p = list.get(i);
                int d = a * x[p] + b * y[p] + c;
    
                if (d > 0) {
                    pointsToLeft.add(p);

                    if (d > maxDistance) {
                        maxDistance = d;
                        maxPoint = p;
                    }
                }
            }

            //System.out.println("MaxPoint: " + maxPoint);
            if (maxPoint >= 0) {
                Thread[] threads = new Thread[2];

                threads[0] = new Thread(new Worker(maxPoint, right, depth+1, pointsToLeft, convex, x, y));
                threads[0].start();

                convex.add(maxPoint);

                threads[1] = new Thread(new Worker(left, maxPoint, depth+1, pointsToLeft, convex, x, y));
                threads[1].start();

                for (int i = 0; i < threads.length; i++) {
                    try {
                        threads[i].join();
                    } catch (Exception e) {}
                }
            }
        }
    }
    
    private void quickHull(int MAX_X, int MIN_X, IntList points, IntList coHull) {    
        coHull.add(MAX_X);
        findPointsToLeft(MIN_X, MAX_X, points, coHull);
        coHull.add(MIN_X);
        findPointsToLeft(MAX_X, MIN_X, points, coHull);
    }
    
    private void findPointsToLeft(int point1, int point2, IntList points, IntList convexHull) {
        int a = y[point1] - y[point2];
        int b = x[point2] - x[point1];
        int c = (y[point2] * x[point1]) - (y[point1] * x[point2]);
  
        int maxDistance = 0;
        int maxPoint = -1;
  
        IntList pointsToLeft = new IntList();
  
        for (int i = 0; i < points.size(); i++) {
            int p = points.get(i);
            int d = a * x[p] + b * y[p] + c;
  
            if (d > 0) {
                pointsToLeft.add(p);

                if (d > maxDistance) {
                    maxDistance = d;
                    maxPoint = p;
                }
            }
        }
  
        if (maxPoint >= 0) {
            findPointsToLeft(maxPoint, point2, pointsToLeft, convexHull);
            convexHull.add(maxPoint);
            findPointsToLeft(point1, maxPoint, pointsToLeft, convexHull);
        }
    }
}
