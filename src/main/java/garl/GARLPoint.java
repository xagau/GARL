package garl;

/** Copyright (c) 2019-2022 placeh.io,
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author xagau
 * @email seanbeecroft@gmail.com
 *
 */
import java.util.Arrays;

public class GARLPoint {
    // the coordinates of the point
    private double x;
    private double y;

    Obstacle o = null;
    
    public GARLPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public GARLPoint(double x, double y, Obstacle o) {
        this.x = x;
        this.y = y;
        this.o = o;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    // distance returns the distance between this point and a given point
    public double distance(GARLPoint p) {
        return Math.sqrt((p.x - this.x) * (p.x - this.x) +
                (p.y - this.y) * (p.y - this.y));
    }

    public String toString() {
        String s = "";
        s = "(" + this.getX() + "," + this.getY() + ")";
        return s;
    }

    public static GARLPoint nearestPoint(GARLPoint[] points, GARLPoint point) {
        if( points == null ){
            return null;
        }
        if( points.length >= 1 ) {
            GARLPoint p = points[0];
            for (int i = 0; i < points.length; i++) {
                double p1 = points[i].distance(point);
                double p2 = p.distance(point);
                if( !new Double(p1).isNaN() && !new Double(p2).isNaN()) {
                    if (p1 < p2) {
                        p = points[i];
                    }
                }
            }
            return p;
        }
        return null;

    }

    public static GARLPoint[] internalPoints(GARLPoint[] points, double radius) {

        int countPoints = 0;
        for (int i = 0; i < points.length; i++) {
            double xp = points[i].getX();
            double yp = points[i].getY();
            // points are inside the circle if d^2 <= r^2
            // d^2 = (Xp-Xc)^2 + (Yp-Yc)^2
            // Xp and Yp is the point that should be checked
            // Xc and Xc is the point center (orgin)
            // Xc and Yc are 0 you end up with d^2 = (Xp-Xc)^2 + (Yp-Yc)^2
            if (xp * xp + yp * yp <= radius * radius) {
                countPoints++;
            }
        }
        int companionVar = 0;
        GARLPoint[] pointsInside = new GARLPoint[countPoints];
        for (int j = 0; j < countPoints; j++) {
            pointsInside[companionVar] = points[j];
            companionVar++;
        }
        return pointsInside;

    }

    public static void main(String[] args) {
        GARLPoint[] points = {new GARLPoint(1, 2),
                new GARLPoint(2, 3),
                new GARLPoint(5, 2)};
        new GARLPoint(12, 13); // points outside the circle
        GARLPoint point = new GARLPoint(1, 1);
        double r = 7;
        GARLPoint nearestPoint = nearestPoint(points, point);
        GARLPoint[] internalPoints = internalPoints(points, 7);

        System.out.println(nearestPoint + "   " + Arrays.toString(internalPoints));
    }

}