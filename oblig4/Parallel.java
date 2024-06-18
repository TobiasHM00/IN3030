public class Parallel extends ConvexHull {
    Parallel(int n, int seed) {
        super(n, seed);
    }

    IntList findConvexHull(int cores) {
        IntList convexHull = new IntList();
        Thread[] threads = new Thread[2];

        threads[0] = new Thread(new Worker(MIN_X, MAX_X, 1, points, convexHull, x, y));
        threads[0].start();

        threads[1] = new Thread(new Worker(MAX_X, MIN_X, 1, points, convexHull, x, y));
        threads[1].start();

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {}
        }

        return convexHull;
    }
}