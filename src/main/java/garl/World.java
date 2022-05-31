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
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class World extends JLabel {
    static ArrayList<Entity> list = new ArrayList<>();

    Selection selection = null;

    static double offset = -180;
    int controls = 0;
    static int totalControls = 0;
    static int totalSpawns = 0;

    int children = 0;
    int width;
    int height;
    int bestSpawn = 0;
    static ArrayList<Entity> bestSeeds = new ArrayList<>();
    static ArrayList<Entity> prospectSeeds = new ArrayList<>();
    int step = 0;
    double phl = 0;
    static double increment = 0.00001000;
    int mx = 0;
    int my = 0;
    static Entity selected = null;


    int spawns = 0;
    int impact = 0;
    int epoch = 1;

    public void reset(){
        list = null;
        Runtime.getRuntime().gc();
    }

    World(int w, int h) {
        width = w;
        height = h;

    }

    World(ArrayList<Entity> population, Selection selection, int w, int h) {
        this.list = population;
        this.selection = selection;
        width = w;
        height = h;

    }

    public int getLivingCount() {
        int livingCount = 0;
        if( this.list == null ){
            return 0;
        }
        if( this.list.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < this.list.size(); i++) {
            Entity e = (Entity) this.list.get(i);
            if (e.alive) {
                livingCount++;
            }
        }
        return livingCount;
    }

    public int getDeadCount() {
        int deadCount = 0;
        if( this.list.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < this.list.size(); i++) {
            Entity e = (Entity) this.list.get(i);
            if (!e.alive) {
                deadCount++;
            }
        }
        return deadCount;
    }

    public void setPopulation(ArrayList<Entity> population) {
        this.list = population;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    Action last = null;

    public void setState(Action action) {
        last = action;
    }

    public Action getState() {
        return last;
    }

    private static void drawVisibilityCircle(Graphics2D g2d, Color kin, Point center, float r, Color c, Entity ent) {
        float radius = r;
        float[] dist = {0f, 1f};
        Color[] colors = {new Color(0, 0, 0, 0), c};
        Color[] kins = {new Color(0, 0, 0, 0), kin};
        //workaround to prevent background color from showing
        drawBackGroundCircle(g2d, radius, Color.WHITE, center, ent);
        drawGradientCircle(g2d, radius, dist, colors, center, ent);
        drawGradientCircle(g2d, 2, dist, kins, center, ent);

    }

    /////////////////////////////////////////////////////////////////////////////////


    private static void drawVisibilityAutomata(Graphics2D g2d, Color kin, Point center, float r, Color c, Entity ent) {
        float radius = r;
        float[] dist = {0f, 1f};
        Color[] colors = {new Color(0, 0, 0, 0), c};
        Color[] kins = {new Color(0, 0, 0, 0), kin};
        //workaround to prevent background color from showing
        drawBackGroundAutomata(g2d, radius, Color.WHITE, center, ent);
        drawGradientAutomata(g2d, radius, dist, colors, center, ent);
        drawGradientAutomata(g2d, 2, dist, kins, center, ent);

    }

    private static void drawBackGroundAutomata(Graphics2D g2d, float radius, Color color, Point2D center, Entity ent) {

        g2d.setColor(color);
        radius -= 1;//make radius a bit smaller to prevent fuzzy edge
        g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY()
                - radius, radius * 2, radius * 2));

        int X = ent.size * 8, Y = ent.size * 8;
        BufferedImage I = new BufferedImage(X, Y, BufferedImage.TYPE_INT_RGB);

        int sz = 40;
        for (int i = 0; i < X; ++i)
            for (int j = 0; j < Y; ++j)
                I.setRGB(i, j, 0xffc068); // Tan packground
        for (int j = 0; j < sz; ++j) I.setRGB(j, j, 0); // Scratch upper left corner
        WritableRaster wr = I.getRaster();
        if (1 > 0) {
            int x = X - sz, y = Y - sz;
            int[] a = new int[y * x * 3]; // 96 bit pixels
            for (int j = 0; j < y; ++j)
                for (int i = 0; i < x; ++i) {
                    int z = 3 * (j * x + i);
                    a[z] = ent.genome.read();
                    a[z + 1] = ent.genome.read();
                    a[z + 2] = ent.genome.read();
                }
            wr.setPixels(20, 20, x, y, a);
        } else {
            int[] a = new int[3];
            a[2] = ent.genome.read();
            ;
            for (int j = 0; j < Y - sz; ++j) {
                a[1] = ent.genome.read();
                for (int i = 0; i < X - sz; ++i) {
                    a[0] = ent.genome.read();
                    wr.setPixel(20 + ent.genome.read(), 20 + ent.genome.read(), a);
                }
            }
        }
        //g2d.drawImage(I, (int)ent.location.x, (int)ent.location.y, null);
        g2d.drawImage(I, (Settings.INSPECTOR_WIDTH / 2) - sz - (int) ent.size, (Settings.INSPECTOR_WIDTH / 2) - sz - (int) ent.size, null);
    }


    private static void drawGradientAutomata(Graphics2D g2d, float radius, float[] dist, Color[] colors, Point2D center, Entity ent) {

        //GradientPaint gp4 = new GradientPaint(radius, radius,
        //        ent.color, radius/2, radius, Color.black, true);

        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(rgp);
        g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2));

        //int[] xValues = {(int)center.getX() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size};
        //int[] yValues = {(int)center.getY() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size};
        //Polygon poly = new Polygon(xValues, yValues, 6);
        //g2d.fill(poly);
    }

    /////////////////////////////////////////////////////////////////////////////////


    private static void drawBackGroundCircle(Graphics2D g2d, float radius, Color color, Point2D center, Entity ent) {

        g2d.setColor(color);
        radius -= 1;//make radius a bit smaller to prevent fuzzy edge
        g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY()
                - radius, radius * 2, radius * 2));
    }

    private static void drawGradientCircle(Graphics2D g2d, float radius, float[] dist, Color[] colors, Point2D center, Entity ent) {

        //GradientPaint gp4 = new GradientPaint(radius, radius,
        //        ent.color, radius/2, radius, Color.black, true);

        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(rgp);
        g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2));

        //int[] xValues = {(int)center.getX() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getX() -ent.genome.read(ent.genome.index())%ent.size};
        //int[] yValues = {(int)center.getY() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() +ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size, (int)center.getY() -ent.genome.read(ent.genome.index())%ent.size};
        //Polygon poly = new Polygon(xValues, yValues, 6);
        //g2d.fill(poly);
    }


    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        //Graphics2D g2d = (Graphics2D) g;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);


        DecimalFormat df = new DecimalFormat("0.00000000");

        step++;
        if (totalSpawns > totalControls) {
            phl += increment;
        }

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        ArrayList<Obstacle> rlist = selection.rlist;

        for (int j = 0; j < rlist.size(); j++) {
            try {
                Obstacle rect = rlist.get(j);
                Point2D point = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
                Color[] colors = {Color.pink, Color.pink, Color.pink};
                float[] dist = {0.0f, 0.5f, 1.0f};
                Point2D center = new Point2D.Float(0.5f * rect.width, 0.5f * rect.height);

                RadialGradientPaint p =
                        new RadialGradientPaint(center, 0.5f * rect.width, dist, colors);
                //RadialGradientPaint rgp = new RadialGradientPaint(point, (float)rect.width, (float)rect.height, rect.color);
                g2.setPaint(p);
                if (rect.spawner) {
                    g2.setColor(rect.getColor());
                }
                if (rect.control) {
                    g2.setColor(rect.getColor());
                }

                if (rect != null) {
                    g2.fillRect(rect.x, rect.y, rect.width, rect.height);
                }
            } catch (Exception ex) {
            }
        }

        int livingCount = getLivingCount();
        if( list != null ){
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            drawEntity(g2, e, false);
        }
        }


        //BufferedImage buf = g2.getDeviceConfiguration().createCompatibleImage(width-Settings.INSPECTOR_WIDTH, height);
        //drawOverlay(buf, getWidth()-Settings.INSPECTOR_WIDTH, getHeight(), GARLTask.inspectorContainer);

        drawPopup(g2, selected, mx, my);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, getHeight() - 24, getWidth(), getHeight());
        g2.setColor(Color.YELLOW);
        g2.drawString("V:" + Globals.major + "-" + Globals.minor + " Think:" + step + " population:" + livingCount + " killed:" + (list.size() - livingCount) + " " + df.format(phl) + " PHL " + getWidth() + " x " + getHeight() + " epoch:" + epoch + " children:" + children + " impact death:" + impact + " controls:" + controls + " spawns:" + spawns + " total spawns:" + totalSpawns + " total controls:" + totalControls + " best seed:" + bestSpawn, 10, (getHeight() - 10));
        g2.dispose();
    }

    public void drawEntity(Graphics2D g2, Entity e, boolean nncanvas) {
        int r = (int) Math.ceil((double) e.size / 2);

        if (e.alive) {
            g2.setColor(e.color);
        } else {
            e.color = Color.BLUE;
            g2.setColor(Color.BLUE);
        }
        int px = (Settings.INSPECTOR_WIDTH / 2) + r;
        int py = (Settings.INSPECTOR_WIDTH / 2) + r;
        Point p = null;
        if (nncanvas) {
            p = new Point((int) px + (r / 2), (int) py + (r / 2));
        } else {
            p = new Point((int) e.location.x + (r / 2), (int) e.location.y + (r / 2));
        }
        //if (r >= 1) {
        Color kin = Color.yellow;
        try {
            int k = KinFactory.create(e.genome.read(Gene.KIN));
            kin = new Color(k, 128 % (k % 256), 128 % (k % 256));
        } catch (Exception ex) {

        }

        if (nncanvas) {
            drawVisibilityAutomata(g2, kin, p, r, e.color, e);
        } else {
            drawVisibilityCircle(g2, kin, p, r, e.color, e);
        }

        if (e == selected) {
            if (nncanvas) {
                g2.drawOval((int) px - (r / 2), (int) py - (r / 2), r * 2, r * 2);
            } else {
                g2.drawOval((int) e.location.x - (r / 2), (int) e.location.y - (r / 2), r * 2, r * 2);
            }
        }

        double direction = e.degree;

        int xs = 0;
        int ys = 0;
        int _xs = 0;
        int _ys = 0;

        if (nncanvas) {
            xs = (int) ((int) (px + r) + (e.size * Math.cos(direction * ((Math.PI) / 360d))));
            ys = (int) ((int) (py + r) - (e.size * Math.sin(direction * ((Math.PI) / 360d))));
            _xs = (int) ((int) (px + r) + (e.size * getWidth() * Math.cos(direction * ((Math.PI) / 360d))));
            _ys = (int) ((int) (py + r) - (e.size * getHeight() * Math.sin(direction * ((Math.PI) / 360d))));
        } else {
            xs = (int) ((int) (e.location.x + r) + (e.size * Math.cos(direction * ((Math.PI) / 360d))));
            ys = (int) ((int) (e.location.y + r) - (e.size * Math.sin(direction * ((Math.PI) / 360d))));
            _xs = (int) ((int) (e.location.x + r) + (e.size * getWidth() * Math.cos(direction * ((Math.PI) / 360d))));
            _ys = (int) ((int) (e.location.y + r) - (e.size * getHeight() * Math.sin(direction * ((Math.PI) / 360d))));
        }


        if (xs > 300 && ys > 300) {
            g2.setColor(Color.RED);
            if (nncanvas) {
                g2.drawLine((int) px + (r / 2), (int) py + (r / 2), xs, ys);
            } else {
                g2.drawLine((int) e.location.x + (r / 2), (int) e.location.y + (r / 2), xs, ys);
            }

        }

        if (e == selected) {

            double d1 = (direction - 45);
            double d2 = (direction + 45);
            int _xs1 = (int) ((int) (e.location.x + r) + (e.size * Math.cos(d1 * ((Math.PI) / 360d))));
            int _ys1 = (int) ((int) (e.location.y + r) - (e.size * Math.sin(d1 * ((Math.PI) / 360d))));
            int _xs2 = (int) ((int) (e.location.x + r) + (e.size * Math.cos(d2 * ((Math.PI) / 360d))));
            int _ys2 = (int) ((int) (e.location.y + r) - (e.size * Math.sin(d2 * ((Math.PI) / 360d))));
            g2.drawLine((int) e.location.x + (r / 2), (int) e.location.y + (r / 2), _xs1, _ys1);
            g2.drawLine((int) e.location.x + (r / 2), (int) e.location.y + (r / 2), _xs2, _ys2);

        }
    }


    public void drawPopup(Graphics g, Entity e, int mx, int my) {

        int spacing = 14;
        int popupWidth = 340;
        int popupHeight = 560;

        if (e != null) {
            boolean b = e.world.list.contains(e);
            if (!b) {
                return;
            }
        }

        if (e != null && e.selected == true && e.alive == true) {
            DecimalFormat ddf = new DecimalFormat("0.00000000");

            DecimalFormat df = new DecimalFormat("0.00");
            g.setColor(Color.white);
            g.fillRect(mx, my, popupWidth, popupHeight);
            g.setColor(Color.BLACK);

            g.drawString("Position: X " + df.format(e.location.x) + "-Y " + df.format(e.location.y), mx + spacing, my + spacing * 1);
            g.drawString("Size:" + e.size, mx + spacing, my + spacing * 2);
            g.drawString("Age:" + e.age, mx + spacing, my + spacing * 3);
            g.drawString("Energy:" + df.format(e.getEnergy()), mx + spacing, my + spacing * 4);
            g.drawString("Degree: " + df.format(Math.abs(e.degree)), mx + spacing, my + spacing * 5);
            g.drawString("VX: " + ddf.format(e.location.vx), mx + spacing, my + spacing * 6);
            g.drawString("VY: " + ddf.format(e.location.vy), mx + spacing, my + spacing * 7);
            g.drawString("Alive: " + e.alive, mx + spacing, my + spacing * 8);
            g.drawString("Reproductive Number: " + (int) e.genome.read(Gene.RR), mx + spacing, my + spacing * 9);
            g.drawString("Kill Gene: " + (int) e.genome.read(Gene.KILL), mx + spacing, my + spacing * 10);

            try {
                if( e.last != null ) {
                    g.drawString("Thought: " + e.last.toString() + " " + df.format(e.input), mx + spacing, my + spacing * 11);
                }
            } catch (Exception ex) {
                if( Globals.verbose) {
                    Log.info(ex);
                }
            }
            g.drawString("Genome:", mx + spacing, my + spacing * 12);
            g.drawString(e.genome.code.substring(0, 32), mx + spacing, my + spacing * 13);
            g.drawString("Drift: ", mx + spacing, my + spacing * 14);
            g.setColor(e.color);
            g.setColor(e.color);
            g.fillRect(mx + spacing, my + spacing * 15, popupWidth - 30, 10);
            g.setColor(Color.black);
            g.drawString("Generation: " + e.generation, mx + spacing, my + spacing * 17);
            g.setColor(Color.black);
            g.drawString("Touching: " + e.isTouching(), mx + spacing, my + spacing * 18);
            g.setColor(Color.black);
            g.drawString("Deletions: " + e.genome.numDeletions, mx + spacing, my + spacing * 19);
            int sz = UUID.randomUUID().toString().replaceAll("-", "").length();
            try {
                g.drawString("KIN: " + KinFactory.create(e.genome.read(Gene.KIN)) + ":" + e.genome.read(Gene.KIN), mx + spacing, my + spacing * 21);
            } catch(Exception ex) {
                if( Globals.verbose ){
                    Log.info(ex);
                }
            }
            g.drawString("Fertile: " + e.fertile, mx + spacing, my + spacing * 22);
            g.drawString("Read Position: "+ " char:" + e.genome.read() + " " + e.genome.index( ) , mx + spacing, my + spacing * 23);
            g.drawString("Death: " + Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE), mx + spacing, my + spacing * 24);
            g.drawString("Sample Forward: " + e.sampleForward().size(), mx + spacing, my + spacing * 25);
            g.drawString("Genome Length: " + e.genome.code.length(), mx + spacing, my + spacing * 26);
            g.drawString("Recodes: " + e.genome.numRecodes, mx + spacing, my + spacing * 27);
            g.drawString("Appends: " + e.genome.numAppends, mx + spacing, my + spacing * 28);
            g.drawString("Input: " + e.brain.ann.input.numberOfNeuronsInLayer, mx + spacing, my + spacing * 29);
            g.drawString("Dense: " + e.brain.ann.dense.numberOfNeuronsInLayer, mx + spacing, my + spacing * 30);
            g.drawString("Hidden: " + e.brain.ann.hidden.numberOfNeuronsInLayer, mx + spacing, my + spacing * 31);
            g.drawString("Dropout: " + e.brain.ann.dropout.numberOfNeuronsInLayer, mx + spacing, my + spacing * 32);
            g.drawString("Output: " + e.brain.ann.output.numberOfNeuronsInLayer, mx + spacing, my + spacing * 33);
            g.drawString("Trajectory Goal: " + e.isTrajectoryGoal(), mx + spacing, my + spacing * 34);
            g.drawString("Walls: " + e.walls, mx + spacing, my + spacing * 35);
            g.drawString("Read Char: " + e.genome.read(e.genome.index), mx + spacing, my + spacing * 36);
            g.drawString("Found Target: " + e.target, mx + spacing, my + spacing * 37);
            g.drawString("Closest: " + Entity.closest(e.world.selection.rlist, e).getName(), mx + spacing, my + spacing * 38);
            g.drawString("Distance to Goal: X:" + e.distanceX + " Y:" + e.distanceY, mx + spacing, my + spacing * 39);

            g.setColor(Color.black);

        }

    }

}

