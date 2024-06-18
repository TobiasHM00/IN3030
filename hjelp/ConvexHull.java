import java.util.Arrays;

/**
 * A basic class to allow the precode to compile. You will need to implement the
 * logic for finding what points make up the convex hull.
 * 
 */
public class ConvexHull {
    int n, seed, MAX_X, MAX_Y, MIN_X, MIN_Y;
    int x[], y[];

    ConvexHull(final int n, final int seed, final NPunkter17 nPunkter17) {
        this.n = n;
        this.seed = seed;
        this.x = new int[n];
        this.y = new int[n];
        nPunkter17.fyllArrayer(x, y);
        for (int i = 0; i < n; i++) {
            if (x[i] > x[MAX_X])
                MAX_X = i;
            else if (x[i] < x[MIN_X])
                MIN_X = i;
            if (y[i] > y[MAX_Y])
                MAX_Y = i;
        }
    }

    public static void main(String[] args) {
        boolean testSeq = false;
        boolean testPar = false;
        int seed = 42;
        try {
            for(String flag: args) {
                if(flag.equals("seq")) {
                    testSeq = true; 
                } else if(flag.equals("par")) {
                    testPar = true;
                } else {
                    seed = Integer.parseInt(flag);
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid flag. The flags are seq and par, anything else is parsed as a seed.");
            return;
        }
        if(testSeq) {
            final int n = 1000;
            ConvexHull ch = new ConvexHull(n, seed, new NPunkter17(n, seed));
            IntList coHull = ch.seqMethod();
            Oblig4Precode precode = new Oblig4Precode(ch, coHull);
            precode.drawGraph();
            precode.writeHullPoints();
            coHull.print();
        } else if(testPar) {
            final int n = 100;
            Parallel ch = new Parallel(n, seed, new NPunkter17(n, seed));
            IntList coHull = ch.parMethod();
            Oblig4Precode precode = new Oblig4Precode(ch, coHull);
            precode.drawGraph();
            coHull.print();
        } else {
            int[] ns = new int[]{100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
            for(int i = 0; i < ns.length; i++) {
                int n = ns[i];
                long[] seqTimes = new long[5];
                long[] parTimes = new long[5];

                for(int j = 0; j < 5; j++) {
                    NPunkter17 nps = new NPunkter17(n, seed);
                    ConvexHull chs = new ConvexHull(n, seed, nps);
                    long startTimeS = System.nanoTime();
                    IntList coHulls = chs.seqMethod();
                    long endTimeS = System.nanoTime();
                    NPunkter17 npp = new NPunkter17(n, seed);
                    Parallel chp = new Parallel(n, seed, npp);
                    long startTimeP = System.nanoTime();
                    IntList coHullp = chp.parMethod();
                    long endTimeP = System.nanoTime();

                    seqTimes[j] = (endTimeS - startTimeS);
                    parTimes[j] = (endTimeP - startTimeP);
                }
                Arrays.sort(seqTimes);
                Arrays.sort(parTimes);
                System.out.println("Seq n: " + n + " median: " + (seqTimes[2]));
                System.out.println("Par n: " + n + " median: " + (parTimes[2]));
            }
        }
    }

    public IntList seqMethod() {
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

        seqRec(MAX_X, MIN_X, chosenIndexAbove, mAbove, coHull);
        coHull.add(MIN_X);
        seqRec(MIN_X, MAX_X, chosenIndexBelow, mBelow, coHull);

        //coHull.getAndRemoveLast();
        return coHull;
    }

    //p1 right, p2 left, p3 up
    private void seqRec(int p1, int p2, int p3, IntList m, IntList coHull) {

        int x1 = x[p3];
        int x2 = x[p1];
        int y1 = y[p3];
        int y2 = y[p1];

        int a = y1 - y2;
        int b = x2 - x1;
        int c = y2 * x1 - y1 * x2;

        int d = -1;
        int chosenIndex = -1;
        IntList mRight = new IntList();
        IntList mLeft = new IntList();
        for(int i = 0; i < m.len; i++) {
            int mIndex = m.get(i);
            int pointdist = a*x[mIndex] + b*y[mIndex] + c;
            if(mIndex == p1 || mIndex == p3) {
                continue;
            } 
            if(pointdist > d) {
                d = pointdist;
                chosenIndex = mIndex;
            }
            if(pointdist >= 0) {
                mRight.add(mIndex);
            }
        }

        if(chosenIndex != -1 && (d != 0 || isBetween(p1, p3, chosenIndex))) {
            seqRec(p1, p3, chosenIndex, mRight, coHull);
        }

        coHull.add(p3);

        x1 = x[p2];
        x2 = x[p3];
        y1 = y[p2];
        y2 = y[p3];

        a = y1 - y2;
        b = x2 - x1;
        c = y2 * x1 - y1 * x2;

        d = -1;
        chosenIndex = -1;
        for(int i = 0; i < m.len; i++) {
            int mIndex = m.get(i);
            int pointdist = a*x[mIndex] + b*y[mIndex] + c;
            if(mIndex == p2 || mIndex == p3) {
                continue;
            }
            if(pointdist > d) {
                d = pointdist;
                chosenIndex = mIndex;
            }
            if(pointdist >= 0) {
                mLeft.add(mIndex);
            }
        }

        if(chosenIndex != -1 && (d != 0 || isBetween(p3, p2, chosenIndex))) {
            seqRec(p3, p2, chosenIndex, mLeft, coHull);
        }
    }

    //right, left, point
    private boolean isBetween(int lineA, int lineB, int check) {
        Double tolerance = 0.000000001;
        int xA = x[lineA];
        int yA = y[lineA];
        int xB = x[lineB];
        int yB = y[lineB];
        int xCheck = x[check];
        int yCheck = y[check];

        double distanceAB = Math.sqrt(Math.pow(xA - xB, 2) + Math.pow(yA - yB, 2));
        double distanceACheck = Math.sqrt(Math.pow(xA - xCheck, 2) + Math.pow(yA - yCheck, 2));
        double distanceBCheck = Math.sqrt(Math.pow(xB - xCheck, 2) + Math.pow(yB - yCheck, 2));
        return distanceAB - (distanceACheck + distanceBCheck) <= tolerance && distanceAB - (distanceACheck + distanceBCheck) >= -tolerance;
    }
}