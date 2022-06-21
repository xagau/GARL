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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line {
    public double startX;
    public double startY;
    public double endX;
    public double endY;

    public Line(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;

        this.endX = endX;
        this.endY = endY;

    }

    public static boolean intersects(Line l1, Line l2) {

        //starting point of line 1
        Point2D.Double temp1 = new Point2D.Double(l1.startX, l1.endX);
        //ending point of line 1
        Point2D.Double temp2 = new Point2D.Double(l1.endX, l1.endY);
        //starting point of line 2
        Point2D.Double temp3 = new Point2D.Double(l2.startX, l2.startY);
        //ending point of line 2
        Point2D.Double temp4 = new Point2D.Double(l2.endX, l2.endY);

        //determine if the lines intersect
        boolean intersects = Line2D.linesIntersect(temp1.x, temp1.y, temp2.x, temp2.y, temp3.x, temp3.y, temp4.x, temp4.y);

        //determines if the lines share an endpoint
        boolean shareAnyPoint = shareAnyPoint(temp1, temp2, temp3, temp4);

        if (intersects && shareAnyPoint) {
            //Log.info("Lines share an endpoint.");
            //return true;
        } else if (intersects && !shareAnyPoint) {
            //Log.info("Lines intersect.");
            return true;
        } else {
            //Log.info("Lines neither intersect nor share a share an endpoint.");
        }

        return false;

    }

    public static boolean shareAnyPoint(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double D) {
        if (isPointOnTheLine(A, B, C)) return true;
        else if (isPointOnTheLine(A, B, D)) return true;
        else if (isPointOnTheLine(C, D, A)) return true;
        else if (isPointOnTheLine(C, D, B)) return true;
        else return false;
    }

    public static boolean isPointOnTheLine(Point2D.Double A, Point2D.Double B, Point2D.Double P) {
        double m = (B.y - A.y) / (B.x - A.x);

        if( Double.isNaN(m)){
            return false;
        }
        //handle special case where the line is vertical
        if (Double.isInfinite(m)) {
            if (A.x == P.x) return true;
            else return false;
        }

        if ((P.y - A.y) == m * (P.x - A.x)) return true;
        else return false;
    }
}

