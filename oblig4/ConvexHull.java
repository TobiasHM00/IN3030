/**
 * @author Shiela Kristoffersen
 *
 * This finds the convex hull for a set of points. However, if there are several
 * points on a line, then it doesn't include all those points. Including all
 * those points (in the correct order) is a task for you ;).
 *
 * However, if you find it hard, start parallelizing and then come back to
 * it later :).
 *
 * The convex hull is drawn counter clockwise, starting at the point that has
 * the highest x value.
 */

class ConvexHull {
    int n;
    int[] x, y;
    int MAX_X, MAX_Y, MIN_X;
  
    /* The list that represents our points. It's simply a list of
    integers that references indexes to the x and y arrays.
    The x and y arrays hold the coordinates of our points. */
    IntList points;
    
    ConvexHull(int n, int seed) {
        this.n = n;
        x = new int[n];
        y = new int[n];
    
        NPunkter17 np = new NPunkter17(n, seed);
        np.fyllArrayer(x, y);
        points = np.lagIntList();

        /* Find any two points we know are on the line. Here we choose the points
        with the maximum and minimum x coordinates */
        for (int i = 0; i < points.size(); i++) {
            if (x[i] > x[MAX_X])
                MAX_X = i;
            else if (x[i] < x[MIN_X])
                MIN_X = i;
            
            /* This is just for use in the precode,
            and is not part of the actual algorithm */
            if (y[i] > y[MAX_Y])
                MAX_Y = i;
        }
    }  
  
    IntList quickHull() {  
        /* Create our list in which we store the points in the convex hull */
        IntList convexHull = new IntList();
  
        /*
        Here we start our recursive steps.
            1. First we add the point with the largest x coordinate to the convex hull
            2. Then we find all the points to the left of the line MIN_X -> MAX_X
            3. Then we add the point with the smallest x coordinate to the convex hull
            4. Lastly, we find all the points to the left of the line MAX_X -> MIN_X
        */
        convexHull.add(MAX_X);
        findPointsToLeft(MIN_X, MAX_X, points, convexHull);
        convexHull.add(MIN_X);
        findPointsToLeft(MAX_X, MIN_X, points, convexHull);

        return convexHull;
    }
  
    /*
    This method does two things:
      1. Finds all the points to the left of the line point1 --> point2 and
         stores them in 'pointsToLeft'. This 'pointsToLeft' is then sent in
         as points to the next recursive call. This is done to decrease the
         number of points we have to look through.
  
      2. Finds the point 'maxPoint' furthest to the left of the line
         point1 --> point2. This 'maxPoint' is part of the convex hull.
    */
    void findPointsToLeft(int point1, int point2, IntList points, IntList convexHull) {
        int a = y[point1] - y[point2];
        int b = x[point2] - x[point1];
        int c = (y[point2] * x[point1]) - (y[point1] * x[point2]);
  
        int maxDistance = 0;
        int maxPoint = -1;
  
        /* Use to store all the points with a positive distance */
        IntList pointsToLeft = new IntList();
  
  
        for (int i = 0; i < points.size(); i++) {
  
            /* Getting the index of the point */
            int p = points.get(i);
  
            /* Calculating the 'distance' to the line point1 --> point2.
            The actual distance is (ax + by + c ) / squareroot(a^2 + b^2).
            However, the denominator of the fraction only scales down the distance,
            and since we are only interested in the distance relative to the other
            points, we can exclude that calculation. */
            int d = a * x[p] + b * y[p] + c;
  
            if (d > 0) {
                pointsToLeft.add(p);

                if (d > maxDistance) {
                    maxDistance = d;
                    maxPoint = p;
                }
            }
        } // end for loop
  
        /* Only continuing the recursion if we find a point to the left of the line */
        if (maxPoint >= 0) {
            findPointsToLeft(maxPoint, point2, pointsToLeft, convexHull);
            convexHull.add(maxPoint);
            findPointsToLeft(point1, maxPoint, pointsToLeft, convexHull);
        }
    }
}