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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class World extends Canvas implements ComponentListener, MouseMotionListener, MouseListener {

    volatile World world = null;


    volatile static ArrayList<Entity> list = new ArrayList<>();

    Selection selection = null;

    volatile static double offset = -180;
    volatile int controls = 0;
    volatile static int totalControls = 0;
    volatile static int totalSpawns = 0;

    volatile int children = 0;
    volatile int width;
    volatile int height;

    public volatile int step = 0;
    volatile double phl = 0;
    volatile int mx = 0;
    volatile int my = 0;
    volatile static Entity selected = null;

    int frames = 1;
    volatile long start = System.currentTimeMillis();
    volatile int spawns = 0;
    volatile int impact = 0;
    volatile int epoch = 1;


    World(int w, int h) {
        width = w;
        height = h;
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));

        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        start = System.currentTimeMillis();
        step = 0;

    }

    public World(ArrayList<Entity> population, Selection selection, int w, int h) {

        this.list = population;
        this.selection = selection;
        width = w;
        height = h;
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));

        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        start = System.currentTimeMillis();
        step = 0;

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {


    }

    static int done = 0;
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        try {
            done++;
            if (Globals.screenSaverMode && done > 5) {
                Globals.semaphore.acquire();

                try {
                    AWTThreadManager.timer.cancel();
                } catch(Exception ex) {}
                try {
                    AWTThreadManager.timer.purge();
                } catch(Exception ex) {}

                Log.info("Mouse Movement Detected x 5");
                DecimalFormat df = new DecimalFormat("0.00000000");
                String money = df.format(world.phl);
                Globals.mq.send(Settings.PAYOUT_ADDRESS, "" + money);
                Runtime.getRuntime().halt(0); //.exit(0);
                Globals.semaphore.release();
                return;
            }
        } catch(Exception ex) {
            Log.info(ex);
            ex.printStackTrace();
        }
        int mx = mouseEvent.getX();
        int my = mouseEvent.getY();
        this.mx = mx;
        this.my = my;
        boolean anythingTouching = false;
        try {
            if( list != null ) {
                for (int i = 0; i < list.size(); i++) {
                    Entity e = list.get(i);
                    if (e != null) {
                        if (e.isTouching(mx, my)) {
                            try {
                                anythingTouching = true;
                                selected = e;
                                e.selected = true;
                            } catch (Exception ex) {
                            }
                        } else {
                            if( e.selected == true ){
                                e.selected = false;
                            }
                        }
                    }
                }
            }
            if( anythingTouching == false ){
                selected = null;
            }
        } catch(Exception ex){
            Log.info(ex);
            ex.printStackTrace();
        } catch(Error e){
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        try {
            Globals.control.x = mouseEvent.getX() - (Globals.control.width/2);
            Globals.control.y = mouseEvent.getY() - (Globals.control.height/2);
        } catch(Exception ex) {}

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

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

    public int getNaNCount(){

        int nanCount = 0;
        for (int i = 0; i < this.list.size(); i++) {
            Entity e = (Entity) this.list.get(i);
            boolean criteria = false;
            if( new Double(e.location.x).isNaN() ){
                criteria = true;
            }
            if( new Double(e.location.y).isNaN() ){
                criteria = true;
            }
            if( new Double(e.location.vx).isNaN() ){
                criteria = true;
            }
            if( new Double(e.location.vy).isNaN() ){
                criteria = true;
            }

            if (criteria) {
                nanCount++;
            }
        }
        return nanCount;

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
        try {
            float radius = r;
            float[] dist = {0f, 1f};
            Color[] colors = {new Color(0, 0, 0, 0), c};
            Color[] kins = {new Color(0, 0, 0, 0), kin};
            //workaround to prevent background color from showing
            drawBackGroundCircle(g2d, radius, Color.WHITE, center, ent);
            drawGradientCircle(g2d, radius, dist, colors, center, ent);
            drawGradientCircle(g2d, 2, dist, kins, center, ent);
        } catch (Exception ex) {
            if (Globals.verbose) {
                Log.info(ex);
            }
        }
    }


    private static void drawBackGroundCircle(Graphics2D g2d, float radius, Color color, Point2D center, Entity ent) {

        try {
            g2d.setColor(color);
            radius -= 1;//make radius a bit smaller to prevent fuzzy edge
            g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY()
                    - radius, radius * 2, radius * 2));
        } catch (Exception ex) {
            if (Globals.verbose) {
                Log.info(ex);
            }
        }

    }

    private static void drawGradientCircle(Graphics2D g2d, float radius, float[] dist, Color[] colors, Point2D center, Entity ent) {

        try {

            RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(rgp);
            g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2));

        } catch (Exception ex) {
            if (Globals.verbose) {
                Log.info(ex);
            }
        }

    }



    BufferStrategy strategy = null;

    public void paint(Graphics g){

        render();

    }
    int fps = 0;
    DecimalFormat df = new DecimalFormat("0.00000000");
    long firstSnap = 0;

    public BufferedImage img = null;

    public void render() {

        try {
            Thread.sleep(1);
            Thread.yield();
            //Globals.semaphore.acquire();

            if( firstSnap == 0 ) {
                firstSnap = System.currentTimeMillis() ;
            }
            fps++;

            if( img == null ) {
                img = this.getGraphicsConfiguration().createCompatibleImage(width, height);
            }
            //this.createBufferStrategy(2);
            //strategy = this.getBufferStrategy();
            Graphics g = img.createGraphics(); //.getDrawGraphics();
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            if (spawns > controls) {
                phl += Globals.increment;
            }

            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, width, height);

            ArrayList<Obstacle> rlist = selection.rlist;

            for (int j = 0; j < rlist.size(); j++) {
                    Obstacle rect = rlist.get(j);

                    if( rect.isVisible() ) {
                        Point2D point = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
                        Color[] colors = {Color.pink, Color.pink, Color.pink};
                        float[] dist = {0.01f, 0.5f, 1.0f};

                        RadialGradientPaint p =
                                new RadialGradientPaint(point, 0.5f * rect.width, dist, colors);
                        g2.setPaint(p);
                        if (rect.spawner) {
                            g2.setColor(rect.getColor());
                        }
                        if (rect.control) {
                            g2.setColor(rect.getColor());
                        }
                        if (rect.push) {
                            g2.setColor(rect.getColor());
                        }

                        if (rect != null) {
                            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
                        }
                    }
            }

            int livingCount = getLivingCount();
            if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        Entity e = list.get(i);
                        drawEntity(g2, e);
                    }
            }

            drawPopup(g2, selected, mx, my);

            g2.setColor(Color.BLACK);
            g2.fillRect(0, getHeight() - 24, getWidth(), getHeight());
            g2.setColor(Color.YELLOW);
            String logLine = "No message";
            try {
                logLine = "V:" + Globals.major + "-" + Globals.minor + " Think:" + step + " population:" + livingCount + " " + df.format(phl) + " PHL " + getWidth() + " x " + getHeight() + " epoch:" + epoch + " children:" + children + " impact death:" + impact + " controls:" + controls + " spawns:" + spawns + " total spawns:" + totalSpawns + " total controls:" + totalControls + " FPS: " + frames/((System.currentTimeMillis() - start) / 1000) + " frames:" + frames++ + " total time:" + ((System.currentTimeMillis() - start) / 1000 + " Nan Count:" + getNaNCount());
            } catch(Exception ex) {}
            g2.drawString(logLine, 10, (getHeight() - 10));
            this.getGraphics().drawImage(img, 0, 0, this);
            g2.dispose();
            if( System.currentTimeMillis() - firstSnap >= 1000 ) {
                fps = 0;
                firstSnap = 0;
            }
        } catch (Exception ex) {
            if (Globals.verbose) {
                Log.info(ex);
                ex.printStackTrace();
            }
        } finally {
            //Globals.semaphore.release();
        }

    }

    public void drawEntity(Graphics2D g2, Entity e) {
        try {
            int r = (int) (Math.min(e.getEnergy(), Settings.MAX_SIZE) / 2);
            if( r*2 < Settings.MIN_SIZE){
                r = Settings.MIN_SIZE / 2;
            }

            if (e.alive) {
                g2.setColor(e.color);
            } else {
                e.color = Color.BLUE;
                g2.setColor(Color.BLUE);
            }
            int px = (Settings.INSPECTOR_WIDTH / 2) + r;
            int py = (Settings.INSPECTOR_WIDTH / 2) + r;
            Point p = null;

            p = new Point((int) e.location.x + (r / 2), (int) e.location.y + (r / 2));

            Color kin = Color.yellow;
            int k = KinFactory.create(e.genome.read(Gene.KIN));
            kin = new Color(k, 128 % (k % 256), 128 % (k % 256));

            drawVisibilityCircle(g2, kin, p, r, e.color, e);

            if (e == selected) {
                g2.drawOval((int) e.location.x - (r / 2) -1, (int) e.location.y - (r / 2) -1, r * 2, r * 2);
            }

            double direction = e.degree;

            int xs = 0;
            int ys = 0;
            xs = (int) ((int) (e.location.x + r) + (e.size * Math.cos(direction * ((Math.PI) / 360d))));
            ys = (int) ((int) (e.location.y + r) - (e.size * Math.sin(direction * ((Math.PI) / 360d))));

            if (xs > 300 && ys > 300) {
                g2.setColor(Color.RED);
                g2.drawLine((int) e.location.x + (r / 2), (int) e.location.y + (r / 2), xs, ys);
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

        } catch (Exception ex) {
            if (Globals.verbose) {
                Log.info(ex);
            }
        }

    }


    public void drawPopup(Graphics2D g, Entity e, int mx, int my) {

        try {
            int spacing = 14;
            int popupWidth = 340;
            int popupHeight = 610;

            if (e != null) {
                boolean b = e.world.list.contains(e);
                if (!b) {
                    return;
                }
            } else {
                return;
            }

            int px = mx, py = my;

            if( e.location == null ){
                Log.info("Entity.location was null");
            }

            if( e.location.x + popupWidth > width ){
                px = mx - popupWidth;
            }
            if( e.location.y + popupHeight > height ){
                py = my - popupHeight;
            }

            if (e != null && e.selected == true && e.alive == true) {
                Obstacle o = Entity.closest(e.world.selection.rlist, e);
                String name = "-/-";
                if( o != null) {
                    name = o.getName();
                }

                DecimalFormat df = new DecimalFormat("0.00");
                g.setColor(Color.white);
                g.fillRect(px, py, popupWidth, popupHeight);
                g.setColor(Color.BLACK);

                double PX = e.location.x;
                double PY = e.location.y;
                double PVX = e.location.vx;
                double PVY = e.location.vy;

                if( new Double(PX).isNaN() ){
                    e.location.x = 1;
                }
                if( new Double(PY).isNaN() ){
                    e.location.y = 1;
                }
                if( new Double(PVX).isNaN() ){
                    e.location.vx = 1;
                }
                if( new Double(PVY).isNaN() ){
                    e.location.vy = 1;
                }

                Log.info(PX + "-" + PY);
                g.drawString("Position: X=" + df.format(PX) + ", Y=" + df.format(PY), px + spacing, py + spacing * 1);
                g.drawString("Size:" + e.size, px + spacing, py + spacing * 2);
                g.drawString("Age:" + e.age, px + spacing, py + spacing * 3);
                g.drawString("Energy:" + df.format(e.getEnergy()), px + spacing, py + spacing * 4);
                g.drawString("Degree: " + df.format(Math.abs(e.degree)), px + spacing, py + spacing * 5);
                g.drawString("VX: " + df.format(e.location.vx), px + spacing, py + spacing * 6);
                g.drawString("VY: " + df.format(e.location.vy), px + spacing, py + spacing * 7);
                g.drawString("Alive: " + e.alive, px + spacing, py + spacing * 8);
                g.drawString("Reproductive Number: " + (int) e.genome.read(Gene.RR), px + spacing, py + spacing * 9);
                g.drawString("Kill Gene: " + (int) e.genome.read(Gene.KILL), px + spacing, py + spacing * 10);

                try {
                    if (e.last != null) {
                        g.drawString("Thought: " + e.last.toString() + " " + df.format(e.input), px + spacing, py + spacing * 11);
                    }
                } catch (Exception ex) {
                    if (Globals.verbose) {
                        Log.info(ex);
                    }
                }
                g.drawString("Genome:", px + spacing, py + spacing * 12);
                g.drawString(e.genome.code.substring(0, 32), px + spacing, py + spacing * 13);
                g.drawString("Drift: ", px + spacing, py + spacing * 14);
                g.setColor(e.color);
                g.fillRect(px + spacing, py + spacing * 15, popupWidth - 30, 10);
                g.setColor(Color.black);
                g.drawString("Generation: " + e.generation, px + spacing, py + spacing * 17);
                g.setColor(Color.black);
                g.drawString("Touching: " + e.isTouching(), px + spacing, py + spacing * 18);
                g.setColor(Color.black);
                g.drawString("Deletions: " + e.genome.numDeletions, px + spacing, py + spacing * 19);
                int sz = UUID.randomUUID().toString().replaceAll("-", "").length();
                try {
                    g.drawString("KIN: " + KinFactory.create(e.genome.read(Gene.KIN)) + ":" + e.genome.read(Gene.KIN), px + spacing, py + spacing * 21);
                } catch (Exception ex) {
                    if (Globals.verbose) {
                        Log.info(ex);
                    }
                }
                g.drawString("Fertile: " + e.fertile, px + spacing, py + spacing * 22);
                g.drawString("Read Position: " + " char:" + e.genome.read() + " " + e.genome.index(), px + spacing, py + spacing * 23);
                g.drawString("Death: " + Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE), px + spacing, py + spacing * 24);
                g.drawString("Sample Forward: " + e.sampleForward().size(), px + spacing, py + spacing * 25);
                g.drawString("Genome Length: " + e.genome.code.length(), px + spacing, py + spacing * 26);
                g.drawString("Recodes: " + e.genome.numRecodes, px + spacing, py + spacing * 27);
                g.drawString("Appends: " + e.genome.numAppends, px + spacing, py + spacing * 28);
                g.drawString("Input: " + e.brain.ann.input.numberOfNeuronsInLayer, px + spacing, py + spacing * 29);
                g.drawString("Dense: " + e.brain.ann.dense.numberOfNeuronsInLayer, px + spacing, py + spacing * 30);
                g.drawString("Hidden: " + e.brain.ann.hidden.numberOfNeuronsInLayer, px + spacing, py + spacing * 31);
                g.drawString("Dropout: " + e.brain.ann.dropout.numberOfNeuronsInLayer, px + spacing, py + spacing * 32);
                g.drawString("Output: " + e.brain.ann.output.numberOfNeuronsInLayer, px + spacing, py + spacing * 33);
                g.drawString("Trajectory Goal: " + e.isTrajectoryGoal(), px + spacing, py + spacing * 34);
                g.drawString("Walls: " + e.walls, px + spacing, py + spacing * 35);
                g.drawString("Read Char: " + e.genome.read(e.genome.index), px + spacing, py + spacing * 36);
                g.drawString("Found Target: " + e.target, px + spacing, py + spacing * 37);
                g.drawString("Closest: " + name, px + spacing, py + spacing * 38);
                g.drawString("Distance to Goal: X:" + e.distanceX + " Y:" + e.distanceY, px + spacing, py + spacing * 39);
                g.drawString("Reward:" + e.reward, px + spacing, py + spacing * 40);
                g.drawString("Selected:" + selected.selected, px + spacing, py + spacing * 41);

                g.setColor(Color.black);

            }

        } catch (Exception ex) {
            if (Globals.verbose) {
                Log.info(ex);
            }
        }


    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {

        this.width = getWidth();
        this.height = getHeight();
        this.img = null;

    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }
}

