public class Hullworker extends Thread{
    int levelMax = 3;

    int right, left, furthest, level;
    IntList m, localCoHull;
    int x[], y[];

    Hullworker(int right, int left, int furthest, int level, IntList m, IntList localCoHull, int x[], int y[]) {
        this.right = right;
        this.left = left;
        this.furthest = furthest;
        this.level = level;
        this.m = m;
        this.localCoHull = localCoHull;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {

        if(level >= levelMax) {
            seqRec(right, left, furthest, m, localCoHull);
        } else {
            //Right is right, furthest is left
            Thread[] threads = new Thread[2];

            IntList rightCoHull = new IntList();
            int x1 = x[furthest];
            int x2 = x[right];
            int y1 = y[furthest];
            int y2 = y[right];

            int a = y1 - y2;
            int b = x2 - x1;
            int c = y2 * x1 - y1 * x2;

            int d = - 1;
            int chosenIndex = -1;
            IntList mRight = new IntList();
            for(int i = 0; i < m.len; i++) {
                int mIndex = m.get(i);
                int pointdist = a*x[mIndex] + b*y[mIndex] + c;
                if(pointdist > d) {
                    d = pointdist;
                    chosenIndex = mIndex;
                }
                if(pointdist >= 0 && mIndex != right && mIndex != furthest) {
                    mRight.add(mIndex);
                }
            }

            threads[0] = new Thread(
                new Hullworker(right, furthest, chosenIndex, level + 1, mRight, rightCoHull, x, y));
            threads[0].start();

            //furthest is right, left is left
            IntList leftCoHull = new IntList();
            x2 = x[left];
            y2 = y[left];

            a = y1 - y2;
            b = x2 - x1;
            c = y2 * x1 - y1 * x2;

            d = 1;
            chosenIndex = -1;
            IntList mLeft = new IntList();
            for(int i = 0; i < m.len; i++) {
                int mIndex = m.get(i);
                int pointdist = a*x[mIndex] + b*y[mIndex] + c;
                if(pointdist < d) {
                    d = pointdist;
                    chosenIndex = mIndex;
                }

                if(pointdist <= 0 && mIndex != left && mIndex != furthest) {
                    mLeft.add(mIndex);
                }
            }

            threads[1] = new Thread(
                new Hullworker(furthest, left, chosenIndex, level + 1, mLeft, leftCoHull, x, y));
            threads[1].start();

            try {
                for(int i = 0; i < threads.length; i++) {
                    threads[i].join();
                }
            } catch (Exception e) {
                
            }

            localCoHull.append(rightCoHull);
            localCoHull.append(leftCoHull);
        }
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

        /* if(d == 0) {
            System.out.println("p1: " + p1 + " p3: " + p3 + " chosen: " + chosenIndex);
            System.out.println(isBetween(p1, p3, chosenIndex));
        } */
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

        /* if(d == 0) {
            System.out.println("p3: " + p3 + " p2: " + p2 + " chosen: " + chosenIndex);
            System.out.println(isBetween(p3, p2, chosenIndex));
        } */
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
