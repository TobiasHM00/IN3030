import java.util.Arrays;
import java.util.Locale;

public class Oblig4 {
    public static void main(String args[]) {
        int n = Integer.parseInt(args[0]);
        int seed = 4578;
        System.out.println("n: " + n);
        int numCores = Runtime.getRuntime().availableProcessors();
        //System.out.println("Number of cores used: " + numCores + "\n");

        //sequential
        double[] seqtiming = new double[7];
        Oblig4Precode precode = null;
        IntList convex = null;
        for (int i = 0; i < 7; i++) {
            double time = System.nanoTime();
            ConvexHull ch = new ConvexHull(n, seed);
            convex = ch.quickHull();
            precode = new Oblig4Precode(ch, convex);
            seqtiming[i] = (System.nanoTime() - time)/1000000;
        }
        Arrays.sort(seqtiming);
        System.out.println("Median time for seq runs: " + seqtiming[3] + "ms");
        //if (n == 100) precode.drawGraph();
        //convex.print();

        //parallel
        double[] paratiming = new double[7];
        IntList convexPara = null;
        for (int i = 0; i < 7; i++) {
            double time = System.nanoTime();
            Parallel chPara = new Parallel(n, seed);
            convexPara = chPara.findConvexHull(numCores);
            precode = new Oblig4Precode(chPara, convexPara);
            paratiming[i] = (System.nanoTime() - time)/1000000;
        }
        Arrays.sort(paratiming);
        System.out.println("Median time for para runs: " + paratiming[3] + "ms");
        System.out.println("Speedup for n=" + n + ": " + String.format(Locale.US, "%.2f", seqtiming[3]/paratiming[3]) + "x");
        //if (n == 100) precode.drawGraph();
        //convexPara.print();
        //System.out.println("Len: " + convexPara.len);
    }
}