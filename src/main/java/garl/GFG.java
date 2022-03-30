package garl;

public class GFG {
    static int circle(double x1, double y1, double x2,
                      double y2, double r1, double r2) {
        double distSq = (x1 - x2) * (x1 - x2) +
                (y1 - y2) * (y1 - y2);
        double radSumSq = (r1 + r2) * (r1 + r2);
        if (distSq == radSumSq) {
            return 1;
        } else if (distSq > radSumSq) {
            return -1;
        } else {
            return 0;
        }
    }
}