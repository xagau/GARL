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
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Entity {
    volatile Brain brain = null;
    volatile Coord location = new Coord();
    volatile Coord previous = new Coord();

    volatile Genome genome = null;
    volatile int generation = 0;
    volatile int epoch = 0;
    volatile boolean fertile = false;
    private double energy = Settings.ENERGY * 2 * Math.random();
    volatile int size = Settings.MIN_SIZE;
    volatile double degree = Math.random() * 360; // must be 0 - 360 to specify the direction the entity is facing.
    volatile boolean selected = false;
    volatile Action last = null;
    volatile double input = 0;
    volatile Entity touching = null;
    volatile boolean reachedGoal = false;
    volatile double distanceX = Double.NaN;
    volatile double distanceY = Double.NaN;

    volatile int walls = 0;
    volatile int age = 0;

    volatile Color color = Color.blue;
    volatile World world = null;
    volatile boolean alive = true;

    volatile double register = 0;
    volatile double goal = 0;
    volatile double direction = 1;


    volatile double anglex = 0;
    volatile double angley = 0;

    volatile boolean target = false;

    volatile double targetvx = Double.NaN;
    volatile double targetvy = Double.NaN;

    volatile double targetx = Double.NaN;
    volatile double targety = Double.NaN;

    volatile int depth = 0;




    public static Obstacle closest(ArrayList<Obstacle> list, Entity e) {
        int distX = Integer.MAX_VALUE;
        int distY = Integer.MAX_VALUE;
        Obstacle closest = new Obstacle();
        closest.x = Integer.MAX_VALUE - 2;
        closest.y = Integer.MAX_VALUE - 2;
        closest.width = Integer.MAX_VALUE - 1;
        closest.height = Integer.MAX_VALUE - 1;


        for (int i = 0; i < list.size(); i++) {
            Obstacle g = list.get(i);
            if( g.isVisible() ) {
                int tdistX = (int) (g.x + g.width) - ((int) e.location.x + e.size / 2);
                int tdistY = (int) (g.y + g.height) - ((int) e.location.y + e.size / 2);
                tdistX = Math.abs(tdistX);
                tdistY = Math.abs(tdistY);



                if (tdistX < distX && tdistY < distY) {
                    distX = tdistX;
                    distY = tdistY;
                    closest = g;
                }
                if( g.spawner){
                    e.distanceX = tdistX;
                    e.distanceY = tdistY;
                }
            }
        }

        if (closest != null) {
            return closest;
        }

        return null;
    }

    public static boolean isCloser(double t, double n, double o)
    {
        n = Math.abs(n);
        o = Math.abs(o);
        t = Math.abs(t);

        double od = t - o;
        double nd = t - n;
        nd = Math.abs(nd);
        od = Math.abs(od);
        if( nd < od ){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isTrajectoryGoal() {
        if( !alive ){
            return false;
        }
        ArrayList<Obstacle> mwalls = sampleForward(this);

        Obstacle first = closest(mwalls, this);
        if (walls == 0) {
            return false;
        }
        double direction = degree;
        int _xs = (int) ((int) (location.x + (size / 2)) + (size * world.getWidth() * Math.cos(direction * ((Math.PI) / 360d)))); //);
        int _ys = (int) ((int) (location.y + (size / 2)) - (size * world.getHeight() * Math.sin(direction * ((Math.PI) / 360d)))); //);

        int x = (int) location.x + ((size / 2) / 2);
        int y = (int) location.y + ((size / 2) / 2);

        Line2D line = new Line2D.Double((double) x, (double) y, (double) _xs, (double) _ys);
        Obstacle goal = Globals.spawn;
        Line2D line1 = new Line2D.Double(goal.x, goal.y, goal.x + goal.width, goal.y + goal.height);
        if( line.intersectsLine(line1) && first == Globals.spawn) {

            target = true;

            if( isCloser(goal.x, location.x, previous.x)){
                if( goal.x >= location.x ) {
                    targetx = location.x;
                    targetvx = location.vx;
                } else {
                    targetx = previous.x;
                    targetvx = previous.vx;
                }
            }
            if( isCloser(goal.y, location.y, previous.y)){
                targety = location.y;
                targetvy = location.vy;
            } else {
                targety = previous.y;
                targetvy = previous.vy;
            }

            this.goal = 1;
            targety = location.y;
            targetDegree = degree;

            return true;

        }

        return false;
    }

    volatile double targetDegree = -1;
    public ArrayList<Obstacle> sampleForward(Entity e) {
        double direction = e.degree;
        if( world == null ){
            return null;
        }
        if( e == null ) {
            return null;
        }
        int _xs = (int) ((int) (e.location.x + (e.size / 2)) + (e.size * world.getWidth() * Math.cos(direction * ((Math.PI) / 360d))));
        int _ys = (int) ((int) (e.location.y + (e.size / 2)) - (e.size * world.getHeight() * Math.sin(direction * ((Math.PI) / 360d))));

        int x = (int) e.location.x + ((e.size / 2) / 2);
        int y = (int) e.location.y + ((e.size / 2) / 2);

        Line2D line = new Line2D.Double((double) x, (double) y, (double) _xs, (double) _ys);

        ArrayList<Obstacle> walls = new ArrayList<>();
        for (int i = 0; i < world.selection.rlist.size(); i++) {
            try {
                Obstacle wall = world.selection.rlist.get(i);
                if( wall.isVisible() ) {
                    Line line1 = new Line(wall.x, wall.y, wall.x + wall.width, wall.y + wall.height);
                    if (wall.intersectsLine(line)) {
                        walls.add(wall);
                    }
                }
            } catch (Exception ex) {
            }
        }

        e.walls = walls.size();

        return walls;
    }


    public boolean isTouching(Entity e) {
        if (e == null) {
            return false;
        }
        int t = GFG.circle(location.x, location.y, e.location.x,
                e.location.y, size / 2, e.size / 2);
        if (t == 1) {
            touching = e;
            return true;
        } else if (t < 0) {
            touching = null;
            return false;
        } else {
            touching = e;
            return true;
        }
    }

    public boolean isTouching() {
        for (int i = 0; i < World.list.size(); i++) {
            Entity e = world.list.get(i);
            if (e != this) {
                if (isTouching(e)) {
                    touching = e;
                    return true;
                }
            }
        }

        touching = null;
        return false;
    }

    public void die() {
        try {
            alive = false;
            if(reachedGoal) {
                genome.code = Genome.GOAL;
            } else {
                genome.code = Genome.DEAD;
            }
            touching = null;
            brain = null;
            previous = null;
            last = null;
            world = null;
            Runtime.getRuntime().gc();
        } catch(Exception ex) { ex.printStackTrace(); Log.info(ex.getMessage()); }
    }

    public boolean intersects(Entity a, Entity b) {



        int a_startX = (int) a.location.x;
        int a_startY = (int) a.location.y;

        int b_startX = (int) b.location.x;
        int b_startY = (int) b.location.y;

        double direction = a.degree;

        //int r = a.size / 2;
        int r = 500;
        int xae = (int) ((int) (a.location.x + r) + (a.size * Math.cos(direction * ((Math.PI) / 360d)))); //);
        int yae = (int) ((int) (a.location.y + r) - (a.size * Math.sin(direction * ((Math.PI) / 360d)))); //);

        direction = b.degree;


        int xbe = (int) ((int) (b.location.x + r) + (b.size * Math.cos(direction * ((Math.PI) / 360d)))); //);
        int ybe = (int) ((int) (b.location.y + r) - (b.size * Math.sin(direction * ((Math.PI) / 360d)))); //);

        int a_endX = xae;
        int a_endY = yae;

        int b_endX = xbe;
        int b_endY = ybe;


        Line l1 = new Line(a_startX, a_startY, a_endX, a_endY);
        Line l2 = new Line(b_startX, b_startY, b_endX, b_endY);


        boolean bb = Line.intersects(l1, l2);

        if (bb) {
            return true;
        }

        return false;
    }

    public ArrayList<Entity> sampleForward() {
        if( !alive ){
            return null;
        }
        ArrayList<Entity> list = new ArrayList<>();
        for (int i = 0; i < world.list.size(); i++) {
            try {
                Entity ent = world.list.get(i);
                // should be closest.
                if (ent != null && ent != this) {
                    if (intersects(this, ent)) {
                        list.add(ent);
                    }
                }
            } catch(Exception ex) {}
        }
        return list;
    }

    public boolean isTouching(int mx, int my) {
        int t = GFG.circle(location.x, location.y, mx,
                my, size / 2, size / 2);
        if (t == 1) {
            //Circle touch to each other.
            return true;
        } else if (t < 0) {
            //Circle not touch to each other.
            return false;
        } else {
            //Circle intersect to each other.");
            return true;
        }

    }


    public Entity(World world) {
        try {
            this.world = world;
            this.genome = new Genome(this);
            this.brain = new Brain(this, genome);
            float r, g, b;
            r = genome.read(Gene.SENSORY);
            g = genome.read(Gene.HIDDEN);
            b = genome.read(Gene.SIZE);
            this.setEnergy(Settings.ENERGY*Math.random());
            this.color = Color.getHSBColor(r, 128 % g, 128 % b);
            this.size = Math.min((int)getEnergy(), Settings.MAX_SIZE);
            if( this.size > Settings.MAX_SIZE){
                this.size = Settings.MAX_SIZE;
            } else if( this.size <= Settings.MIN_SIZE ){
                this.size = Settings.MIN_SIZE;
            }

            this.degree = Math.random() * 360;
        } catch(Exception ex) {
            Log.info(ex.getMessage());
        }
    }

    Entity replicate() {
        return clone();
    }

    public Entity clone() {
        Entity e = new Entity(world);
        e.alive = true;

        int move = 1;

        boolean tryAgain = false;
        int cnt = 0;
        do {
            cnt++;
            tryAgain = false;
            if (Math.random() > 0.5) {
                e.location.x = location.x + Settings.CELL_MOVEMENT + e.size + move;
            } else {
                e.location.x = location.x - Settings.CELL_MOVEMENT - e.size - move;
            }
            if (Math.random() < 0.5) {
                e.location.y = location.y + Settings.CELL_MOVEMENT + e.size + move;
            } else {
                e.location.y = location.y - Settings.CELL_MOVEMENT - e.size - move;
            }
            ArrayList<Obstacle> list = world.selection.rlist;
            for (int i = 0; i < list.size(); i++) {
                Obstacle rect = list.get(i);
                if( rect.isVisible() ) {
                    if (world.selection.insideRect(rect, (int) e.location.x, (int) e.location.y)) {
                        tryAgain = true;
                        move++;
                    }
                }
            }
            if( isTouching()) {
                tryAgain = true;
                move++;
            }

            if( cnt++ > 10 ){
                break;
            }
        } while(tryAgain);

        e.genome.code = genome.code;
        e.genome.numAppends = 0;
        e.genome.numRecodes = 0;
        e.genome.mutate();
        e.brain = new Brain(e, e.genome);

        e.age = 0;
        e.energy = Settings.ENERGY * Math.random();
        e.size = (int)Math.min((int)e.energy, Settings.MAX_SIZE);
        e.degree = Math.random() * 360;
        e.generation = generation + 1;
        e.fertile = false;

        return e;
    }

    private void
    consume() {
        double cost = (double)age/100000;
        //Log.info("Age Cost" + cost);
        if (Math.abs(location.vx) != 0 && Math.abs(location.vy) != 0) {

            setEnergy(getEnergy() - Settings.ENERGY_STEP_COST - cost);
        } else {
            setEnergy(getEnergy() - Settings.ENERGY_STEP_SLEEP_COST - cost);
        }
    }


    public Action think(World world, long start) {

        depth = 0;
        if( !alive ){
            return Action.STOP;
        }
        age++;
        consume();
        Action action = null;
        try {
            action = brain.evaluate(world);
            long intermediate = System.currentTimeMillis();
            process(action, world, depth++);
            world.setState(action);

            if( target && isTrajectoryGoal() ){
                return Action.FASTER;
            } else {
                sample();
            }

        } catch (Exception ex) {
            if(Globals.verbose) {
                ex.printStackTrace();
                Log.info(ex);
            }
        }

        return Action.SCAN;
    }

    public void sample(){
        ArrayList<Obstacle> list = sampleForward(this);
        Obstacle closest = Entity.closest(list, this);
        if( closest != null ) {
            if (closest.getName().equals("spawner")) {
                double cx = location.x + size/2;
                double cy = location.y + size/2;
                double ex = closest.getCenterX();
                double ey = closest.getCenterY();

                Line line = new Line((int)cx, (int)cy,(int) ex,(int) ey);

                if( ex < cx ) {
                    location.vx = location.vx - (ex / ey);
                } else {
                    location.vx = location.vx + (ex / ey);
                }
                if( ey < cy ) {
                    location.vy = location.vy - (ey / ex);
                } else {
                    location.vy = location.vy + (ey / ex);
                }

            }
        }

    }



    public void process(Action action, World world, int depth) {

        try {
            double epsilon = 1.01;
            depth++;
            if (!alive) {
                return;
            }


            if (depth >= Settings.MAX_THINK_DEPTH) {
                return;
            }

            if ((Math.abs(location.vx) == Math.abs(0) && Math.abs(location.vy) == Math.abs(0))) {

                double d = brain.getOutput();
                action = ActionFactory.create(d);
                input = d;
                location.vx = Math.random();
                location.vy = Math.random();

            }

            if (action == last) {
                double d = Math.random() * Action.values().length;
                action = ActionFactory.create(d);
                input = d;
            }

            if (brain != null) {

                switch (action) {
                    case SIN:

                        anglex = Math.sin(anglex);
                        angley = Math.sin(angley);
                        process(Action.FASTER, world, depth);
                        break;
                    case COS:
                        anglex = Math.cos(anglex);
                        angley = Math.cos(angley);
                        process(Action.FASTER, world, depth);
                        break;
                    case TAN:
                        anglex = Math.tan(anglex);
                        angley = Math.tan(angley);
                        process(Action.FASTER, world, depth);
                        break;
                    case TANH:
                        anglex = Math.tanh(anglex);
                        angley = Math.tanh(angley);
                        process(Action.FASTER, world, depth);
                        break;

                    case STOP:
                        // if we're stopped - and we're touching someone, lets move.
                        //if(!target ) {
                        if (isTouching()) {
                            doKill(Action.KILL);
                        } else {
                            location.vx = 0;
                            location.vy = 0;
                            process(Action.SCAN, world, depth);
                            break;
                        }
                        if (!isTrajectoryGoal()) {
                            //process(Action.SCAN, world, depth);
                            process(Action.RECODE, world, depth);
                        }

                        break;

                    case JUMP:

                        try {
                            if (register == 0) {
                                brain.input(this, world);
                                brain.ann.input.calc();
                                register = brain.getOutput();
                            }
                            double jf = 0;
                            double jmo = 0;
                            if (register != 0d) {
                                genome.jump((int) register);
                            }
                        } catch (Exception ex) {
                            if( Globals.verbose ){
                                Log.info(ex);
                            }
                        }
                        break;
                    case IF:
                        if (genome.read((int) register) < genome.read(Gene.DECISION)) {
                            genome.advance();
                        } else {
                            genome.reverse();
                        }
                        break;
                    case DIRECTION:
                        direction = -direction;
                        break;
                    case CYCLE:
                    case CONTINUE:
                        if (target) {
                            // should revert back to origin of location where goal was set, set degree, then set vx, and vy
                            //degree = targetDegree;

                            if (targetvx != 0) {
                                if (targetx > location.x) {
                                    location.vx = -Settings.ACCELERATION;
                                } else {
                                    location.vx = targetvx;
                                }
                            }
                            if (targetvy != 0) {
                                if (targety > location.y) {
                                    location.vy = -Settings.ACCELERATION;
                                } else {
                                    location.vy = targetvy;
                                }
                            }
                        }
                        break;

                    case SAVE:
                        brain.input(this, world);
                        brain.ann.input.calc();
                        register = brain.getOutput();

                        break;
                    case DELETE:
                        int maxDeletions = genome.read(Gene.MAX_DELETIONS);
                        //if (genome.numDeletions <= maxDeletions) {
                        ArrayList<Entity> ae = sampleForward();
                        double aao = 0;
                        for (int i = 0; i < ae.size(); i++) {
                            Entity ent = ae.get(i);
                            brain.input(ent, world);
                        }
                        brain.ann.input.calc();


                        if (brain != null && brain.entity != null && brain.entity.genome != null && brain.entity.genome.code != null) {
                            try {
                                if (brain.entity.genome.index == Integer.MIN_VALUE) {
                                    brain.entity.genome.index = 33;
                                }
                                String right = brain.entity.genome.code.substring(0, brain.entity.genome.index);
                                String left = brain.entity.genome.code.substring(right.length() - 1, brain.entity.genome.code.length());

                                brain.entity.genome.code = right + left;
                                genome.numDeletions++;
                            } catch (Exception ex) {
                                if(Globals.verbose){
                                    Log.info(ex);
                                }
                            }
                        }
                        //}
                        break;


                    case APPEND:
                        try {
                            process(Action.SCAN, world, depth);
                            int maxAppends = genome.read(Gene.MAX_APPENDS);

                            double ao = 0;

                            double l = 0;
                            if (register == 0d) {
                                brain.input(this, world);
                                brain.ann.input.calc();
                                ao = brain.getOutput();
                                l = Utility.flatten((long) ao, Settings.CHAR_SET);
                            } else {
                                l = Utility.flatten((long) register, Settings.CHAR_SET);
                            }
                            long lng = (long) l;
                            char c = Long.toHexString(lng).charAt(0);
                            if( brain != null && brain.entity != null && brain.entity.genome != null && brain.entity.genome.code != null ) {
                                brain.entity.genome.code += c;
                                genome.numAppends++;
                                process(Action.JUMP, world, depth);
                            }

                        } catch (Exception ex) {
                            if(Globals.verbose){
                                Log.info(ex);
                            }
                        }
                        break;
                    case SCAN:

                        try {
                            if (!isTrajectoryGoal()) {
                                degree = Math.random() * 360d;
                                //Log.info("Degree:" + degree);
                            } else {
                                if( Globals.verbose) {
                                    Log.info("Trajectory good:" + degree);
                                }
                            }
                        } catch (Exception ex) {
                            if(Globals.verbose) {
                                Log.info(ex);
                            }
                        }
                        break;

                    case RECODE:

                        try {
                            brain.input(this, world);
                            brain.ann.input.calc();
                            double output = brain.getOutput();
                            brain.input(this, world);
                            brain.ann.input.calc();
                            double code = brain.getOutput();
                            if (Character.isLetter((char) output) || Character.isDigit((char) output)) {
                                genome.recode((int) code, (char) output);
                                register = code;
                                process(Action.JUMP, world, depth);
                            }
                        } catch (Exception ex) {
                            if (Globals.verbose) {
                                Log.info(ex);
                            }
                        }
                        break;

                    case MOVE_DOWN: {
                        double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                        location.vy += changey;

                        break;
                    }
                    case SLOW:
                        if (!isTrajectoryGoal()) {
                            location.vy = location.vy / 2;
                            location.vx = location.vx / 2;
                            process(Action.SCAN, world, depth);
                        }
                        break;
                    case FASTER:

                        if (isTrajectoryGoal()) {
                            if (isCloser(Globals.spawn.getCenterX(), location.x, previous.x)) {
                                location.vx = Math.max(targetvx * Settings.ACCELERATION, epsilon);
                            } else {
                                location.vx = Math.min(-Settings.ACCELERATION, -epsilon);
                            }
                            if (isCloser(Globals.spawn.getCenterY(), location.y, previous.y)) {
                                location.vy = Math.max(targetvy * Settings.ACCELERATION, epsilon);
                            } else {
                                location.vy = Math.min(-Settings.ACCELERATION, -epsilon);
                            }

                            targetvx = location.vx;
                            targetvy = location.vy;
                            targetDegree = degree;
                            break;
                        } else if (Utility.precision(location.vx, Math.abs(0), epsilon)) {
                            degree++;
                            location.vx += Math.max(location.vx * Settings.ACCELERATION, epsilon);
                            if (Math.random() > 0.5) {
                                location.vx = -location.vx;
                                degree = -degree;
                            }
                            break;
                        } else if (Utility.precision(location.vy, Math.abs(0), epsilon)) {
                            degree++;
                            location.vy += Math.max(location.vy * Settings.ACCELERATION, epsilon);
                            if (Math.random() > 0.5) {
                                degree--;
                                location.vy = -location.vy;
                            }
                            break;
                        }


                        location.vy = Math.max(location.vy * (1 + Settings.ACCELERATION), epsilon);
                        location.vx = Math.max(location.vx * (1 + Settings.ACCELERATION), epsilon);

                        break;

                    case MOVE_UP: {
                        double changey = Math.max(location.vy * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vy -= changey;
                        break;
                    }
                    case MOVE_UP_RIGHT: {
                        double changex = Math.max(location.vx * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        double changey = Math.max(location.vy * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vx += changex;
                        location.vy -= changey;
                        break;
                    }
                    case MOVE_UP_LEFT: {
                        double changex = Math.max(location.vx * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        double changey = Math.max(location.vy * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vx -= changex;
                        location.vy -= changey;
                        break;
                    }
                    case MOVE_DOWN_RIGHT: {
                        double changex = Math.max(location.vx * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        double changey = Math.max(location.vy * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vx += changex;
                        location.vy += changey;
                        break;
                    }
                    case MOVE_DOWN_LEFT: {
                        double changex = Math.max(location.vx * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        double changey = Math.max(location.vy * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vx -= changex;
                        location.vy += changey;
                        break;
                    }
                    case MOVE_LEFT: {
                        double changex = Math.max(location.vx * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vx -= changex;
                        break;
                    }
                    case MOVE_RIGHT: {
                        double changex = Math.max(location.vx * (1 + Settings.ACCELERATION), Settings.ACCELERATION);
                        location.vx += changex;
                        break;
                    }
                    case KILL:
                        if (target) {
                            if (isTrajectoryGoal()) {
                                process(Action.FASTER, world, depth);
                            } else {
                                process(Action.SLOW, world, depth);
                                process(Action.CONTINUE, world, depth);
                            }
                            break;
                        } else {
                            doKill(action);
                        }
                        break;
                    case NONE:
                        break;
                }

            }

            if (location.x + size > world.getWidth()) {
                location.x = (location.x - size) - Settings.CELL_MOVEMENT;
                location.vx = -location.vx;
            }
            if (location.y + size > world.getHeight()) {
                location.y = (location.y - size) - Settings.CELL_MOVEMENT;
                location.vy = -location.vy;
            }
            if (location.x <= 0) {
                location.x = 0;
                location.vx = -location.vx;
            }
            if (location.y <= 0) {
                location.y = 0;
                location.vy = -location.vy;
            }


            if (location.vx >= Settings.MAX_SPEED) {
                location.vx = Settings.MAX_SPEED;
            }
            if (location.vy >= Settings.MAX_SPEED) {
                location.vy = Settings.MAX_SPEED;
            }
            if (location.vx <= -Settings.MAX_SPEED) {
                location.vx = -Settings.MAX_SPEED;
            }
            if (location.vy <= -Settings.MAX_SPEED) {
                location.vy = -Settings.MAX_SPEED;
            }

            previous.x = location.x;
            previous.y = location.y;
            previous.vx = location.vx;
            previous.vy = location.vy;


            double nx = location.x + location.vx;
            double ny = location.y + location.vy;
            double ox = location.x + previous.vx;
            double oy = location.y + previous.vy;

            if (isCloser(Globals.spawn.getCenterX(), nx, ox)) {
                location.x = nx;
            } else {
                location.x = ox;
            }

            if (isCloser(Globals.spawn.getCenterY(), ny, oy)) {
                location.y = nx;
            } else {
                location.y = oy;
            }

            double v = Math.atan2(location.vx, location.vy);

            //boolean b = isTrajectoryGoal();
            //if(b){
            //    targetvx = location.vx;
            //    targetvy = location.vy;
            //target = true;
            //    return;
            //}

            double radiansToDegrees = 360d / Math.PI;
            degree = v * radiansToDegrees; //
            degree = degree + World.offset;

            if (last == action) {
                action = Action.CONTINUE;
            } else {
                last = action;
            }


        } catch (Exception ex) {
            if( Globals.verbose ) {
                Log.info("Exception in process() ");
                ex.printStackTrace();
            }
        }
    }



    public void doKill(Action action) {
        if (action == Action.KILL) {
            for (int i = 0; i < world.list.size(); i++) {
                Entity o = world.list.get(i);
                if (o != null) {
                    if (isTouching(o) && o != this) {

                        if (KinFactory.create(o.genome.read(Gene.KIN)) == KinFactory.create(genome.read(Gene.KIN))) {
                            o.fertile = true;
                            o.touching = this;
                            touching = o;
                            fertile = true; // partner for sharing genes.
                            return;
                        }
                        double extracted = 0;
                        if (o.alive) {
                            extracted = (genome.read(Gene.ATTACK) * size) - (o.genome.read(Gene.DEFENSE) / 8);
                        } else {
                            extracted = o.getEnergy();
                            //size = size + (o.size );
                            setEnergy(getEnergy()+Math.abs(extracted));
                            size = Math.min((int)getEnergy(), Settings.MAX_SIZE);
                            if( size > Settings.MAX_SIZE){
                                size = Settings.MAX_SIZE;
                            }
                            world.list.remove(o);
                            continue;
                        }
                        double eo = o.getEnergy();

                        extracted = eo - extracted;
                        if (extracted > o.getEnergy()) {
                            eo = o.getEnergy();
                            extracted = eo;
                        }

                        setEnergy(getEnergy() + Math.abs(extracted));
                        //size = size + (o.size );
                        size = Math.min((int)getEnergy(), Settings.MAX_SIZE);
                        o.setEnergy(o.getEnergy() - extracted);
                        o.size = Math.min((int)o.getEnergy(), Settings.MAX_SIZE);

                        if (size > Settings.MAX_SIZE) {
                            size = Settings.MAX_SIZE;
                        }
                        if (o.size > Settings.MAX_SIZE) {
                            o.size = Settings.MAX_SIZE;
                        }

                        if (size <= Settings.MIN_SIZE) {
                            size = Settings.MIN_SIZE;
                        }
                        if (o.size <= Settings.MIN_SIZE) {
                            o.size = Settings.MIN_SIZE;
                        }
                        if (o.getEnergy() <= 0 || o.size <= Settings.MIN_SIZE) {
                            o.die();
                            if (o.size <= Settings.MIN_SIZE) {
                                o.size = Settings.MIN_SIZE;
                            }
                            world.list.remove(o);
                        } else if (getEnergy() <= 0 || size <= Settings.MIN_SIZE) {
                            die();
                            if (size <= Settings.MIN_SIZE) {
                                size = Settings.MIN_SIZE;
                            }
                            world.list.remove(this);
                        }
                    }
                }
            }
        }
    }

    public Action getLast() {
        return last;
    }


    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
