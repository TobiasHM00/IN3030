public class Parallel extends ConvexHull{

    Parallel(final int n, final int seed, final NPunkter17 nPunkter17) {
        super(n, seed, nPunkter17);
    }

    public IntList parMethod() {
        IntList coHull = new IntList();
        IntList mAbove = new IntList();
        IntList mBelow = new IntList();

        int x1 = x[MIN_X];
        int x2 = x[MAX_X];
        int y1 = y[MIN_X];
        int y2 = y[MAX_X];

        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2 * x1 - y1 * x2;

        int dAbove = - 1;
        int dBelow = 1;
        int chosenIndexAbove = -1;
        int chosenIndexBelow = -1;
        for(int i = 0; i < x.length; i++) {
            int pointdist = a*x[i] + b*y[i] + c;
            if(pointdist > dAbove) {
                dAbove = pointdist;
                chosenIndexAbove = i;
            } else if(pointdist < dBelow) {
                dBelow = pointdist;
                chosenIndexBelow = i;
            }

            if(pointdist >= 0 && i != MAX_X && i != MIN_X) {
                mAbove.add(i);
            } else if(pointdist <= 0 && i != MAX_X && i != MIN_X) {
                mBelow.add(i);
            }
        }

        coHull.add(MAX_X);

        IntList rightCoHull = new IntList();
        IntList leftCoHull = new IntList();
        Thread[] threads = new Thread[2];
        threads[0] = new Thread(
            new Hullworker(MAX_X, MIN_X, chosenIndexAbove, 2, mAbove, rightCoHull, x, y)
            );
        threads[0].start();
        threads[1] = new Thread(
            new Hullworker(MIN_X, MAX_X, chosenIndexBelow, 2, mBelow, leftCoHull, x, y)
            );
        threads[1].start();
        
        try {
            for(int i = 0; i < 2; i++) {
                threads[i].join();
            }
        } catch (Exception e) {

        }

        coHull.append(rightCoHull);
        coHull.add(MIN_X);
        coHull.append(leftCoHull);
        return coHull;
    }
}
