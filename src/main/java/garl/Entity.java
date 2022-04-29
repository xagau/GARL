package garl;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Entity {
    Brain brain = null;
    Coord location = new Coord();
    Genome genome = null;
    int generation = 0;
    boolean fertile = false;
    private double energy = Settings.ENERGY * 2 * Math.random();
    int size = Settings.MIN_SIZE;
    double degree = Math.random() * 360; // must be 0 - 360 to specify the direction the entity is facing.
    boolean selected = false;
    Action last = null;
    double input = 0;
    Entity touching = null;
    boolean reachedGoal = false;
    double distanceX = Double.NaN;
    double distanceY = Double.NaN;

    int walls = 0;
    int age = 0;

    Color color = Color.blue;
    World world = null;
    boolean alive = true;

    double register = 0;
    double goal = 0;
    double direction = 1;
    double cycle = 0;

    double anglex = 0;
    double angley = 0;

    boolean target = false;

    double targetvx = Double.NaN;
    double targetvy = Double.NaN;

    double targetx = Double.NaN;
    double targety = Double.NaN;

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

            //if (g.getCenterX()+g.width/2 < (e.location.x+e.size/2) && (g.getCenterY()+g.height/2 < e.location.y+e.size/2)) {
            int tdistX = (int) (g.x + g.width ) - ((int) e.location.x + e.size / 2);
            int tdistY = (int) (g.y + g.height ) - ((int) e.location.y + e.size / 2);
            tdistX = Math.abs(tdistX);
            tdistY = Math.abs(tdistY);

            //if( g.spawner) {
            if (tdistX < distX && tdistY < distY) {
                distX = tdistX;
                distY = tdistY;
                closest = g;
            }
            //}
            if( g.spawner){

                e.distanceX = tdistX;
                e.distanceY = tdistY;
            }
        }
        if (e.selected) {
            Log.info("Closest:" + closest.x + "-" + closest.y + " w:" + closest.width + " h:" + closest.height + " spawn:" + closest.spawner);
            Log.info("garl.Entity:" + e.location.x + "-" + e.location.y);
        }
        if (closest != null) {
            return closest;
        }

        return null;
    }

    public boolean isTrajectoryGoal() {
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
        Line line1 = new Line(goal.x, goal.y, goal.x + goal.width, goal.y + goal.height);

        if (walls == 1 && first == Globals.spawn) {
            target = true;
            targetvx = location.vx;
            targetvy = location.vy;

            this.goal = 1;
            targetx = location.x;
            targety = location.y;

            return true;

        }

        return false;
    }

    public ArrayList<Obstacle> sampleForward(Entity e) {
        double direction = e.degree;
        int _xs = (int) ((int) (e.location.x + (e.size / 2)) + (e.size * world.getWidth() * Math.cos(direction * ((Math.PI) / 360d)))); //);
        int _ys = (int) ((int) (e.location.y + (e.size / 2)) - (e.size * world.getHeight() * Math.sin(direction * ((Math.PI) / 360d)))); //);

        int x = (int) e.location.x + ((e.size / 2) / 2);
        int y = (int) e.location.y + ((e.size / 2) / 2);

        Line2D line = new Line2D.Double((double) x, (double) y, (double) _xs, (double) _ys);

        ArrayList<Obstacle> walls = new ArrayList<>();
        for (int i = 0; i < world.selection.rlist.size(); i++) {
            try {
                Obstacle wall = world.selection.rlist.get(i);
                Line line1 = new Line(wall.x, wall.y, wall.x + wall.width, wall.y + wall.height);
                if (wall.intersectsLine(line)) {
                    walls.add(wall);
                }
            } catch (Exception ex) {
            }
        }

        e.walls = walls.size();

        return walls;
    }

    public boolean isTrajectoryDeath() {

        if (isTrajectoryGoal()) {
            return false;
        }

        ArrayList<Obstacle> walls = sampleForward(this);
        // check to see if wall with closest trajectory is spawn or not.
        Obstacle first = closest(walls, this);

        this.walls = walls.size();
        if (walls.isEmpty()) {
            return false;
        } else if (!first.spawner) {
            return true;
        } else {
            return true;
        }
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
        for (int i = 0; i < world.list.size(); i++) {
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
        alive = false;
        genome.code = Genome.DEAD;
        touching = null;
        brain = null;
    }

    public boolean intersects(Entity a, Entity b) {

        if (!a.alive || !b.alive) {
            return false;
        }

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

        //r = b.size / 2;
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
        ArrayList<Entity> list = new ArrayList<>();
        for (int i = 0; i < world.list.size(); i++) {
            Entity ent = world.list.get(i);
            // should be closest.
            if (ent != null && ent != this) {
                if (intersects(this, ent)) {
                    list.add(ent);
                }
            }
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
        this.world = world;
        genome = new Genome(this);
        brain = new Brain(this, genome);
        float r, g, b;
        r = genome.read(Gene.SENSORY);
        g = genome.read(Gene.HIDDEN);
        b = genome.read(Gene.SIZE);
        color = Color.getHSBColor(r, 128 % g, 128 % b);
        size = Math.max(genome.read(Gene.SIZE) % Settings.MAX_SIZE, Settings.MIN_SIZE);
        degree = Math.random() * 360;
    }

    Entity replicate() {
        return clone();
    }

    public Entity clone() {
        Entity e = new Entity(world);
        e.alive = true;

        int move = 1;

        boolean tryAgain = false;
        do {
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
                if (world.selection.insideRect(rect, (int) e.location.x, (int) e.location.y)) {
                    tryAgain = true;
                    move++;
                }
            }

        } while(tryAgain);

        e.genome.code = genome.code;
        e.genome.numAppends = 0;
        e.genome.numRecodes = 0;
        e.genome.mutate();
        e.size = size;
        e.brain = new Brain(e, e.genome);

        e.age = 0;
        e.energy = Settings.ENERGY * 2 * Math.random();
        e.degree = Math.random() * 360;
        e.generation = generation + 1;
        e.fertile = false;

        return e;
    }

    private void consume() {
        if (Math.abs(location.vx) != 0 && Math.abs(location.vy) != 0) {
            setEnergy(getEnergy() - Settings.ENERGY_STEP_COST);
        } else {
            setEnergy(getEnergy() - Settings.ENERGY_STEP_SLEEP_COST);
        }
    }
    int depth = 0;

    public Action think(World world, long start) {



        age++;
        consume();
        Action action = null;
        try {
            action = brain.evaluate(world);
            long intermediate = System.currentTimeMillis();
            process(action, world, depth++);
            world.setState(action);

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
                    process(Action.FASTER, world, 0);

                }
            }
            if( target && isTrajectoryGoal() ){
                return Action.FASTER;
            }

        } catch (Exception ex) {
        }



        return Action.CONTINUE;
    }




    public void process(Action action, World world, int depth) {

        double epsilon = 1.01;
        depth++;

        if( depth > Settings.MAX_THINK_DEPTH || (Math.abs(location.vx) == Math.abs(0) && Math.abs(location.vy) == Math.abs(0)) ){
            //System.out.println("Return too much depth");
            if( action != Action.CONTINUE) {
                action = ActionFactory.create(Math.random());
            }
        }

        if (brain != null) {

            switch (action) {
                /*
                case RANDOM:
                    Action aa = brain.evaluate(this, world);
                    double bo = brain.getOutput();
                    Log.setDebug(true);
                    Log.info("Brain Output:" + bo);
                    Log.setDebug(false);
                    double r = bo % Action.values().length;
                    Action aaa = ActionFactory.create(r);
                    process(aaa, world, depth++);
                    break;

                 */
                case NONE:
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

                case STOP:
                    // if we're stopped - and we're touching someone, lets move.
                    //if(!target ) {
                        if (isTouching()) {
                            doKill(Action.KILL);
                        } else {
                            location.vx = 0;
                            location.vy = 0;
                            //process(Action.RANDOM, world, depth);
                        }
                        if(  !isTrajectoryGoal() ){
                            process(Action.SCAN, world, depth);
                            //process(Action.RECODE, world, depth);
                        } else {
                            process(Action.SCAN, world, depth);
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
                        if( targetvx != 0) {
                            location.vx = targetvx;
                        }
                        if( targetvy != 0 ) {
                            location.vy = targetvy;
                        }
                    }
                    break;
                case GOAL:

                    if (isTrajectoryGoal()) {

                        goal = 1;
                        target = true;

                    } else {
                        if (target) {
                            if (isTrajectoryGoal()) {
                                process(Action.CONTINUE, world, depth);
                            } else {
                                process(Action.SCAN, world, depth);
                            }
                            break;
                        }
                    }

                    break;
                case DEATH:

                    if (target) {
                        if (isTrajectoryGoal()) {
                            process(Action.CONTINUE, world, depth);
                        } else {
                            process(Action.SLOW, world, depth);
                            process(Action.SCAN, world, depth);
                        }
                        break;
                    }
                    if (isTrajectoryGoal()) {
                        goal = 1;
                        process(Action.CONTINUE, world, depth);
                    } else {
                        register = 0;
                        process(Action.SLOW, world, depth);
                        process(Action.RECODE, world, depth);
                    }
                    break;
                case COMMIT:
                    if (target) {
                        if (isTrajectoryGoal()) {
                            process(Action.CONTINUE, world, depth);
                        } else {
                            process(Action.SCAN, world, depth);
                        }
                        break;
                    }


                    break;
                case SAVE:
                    brain.input(this, world);
                    brain.ann.input.calc();
                    register = brain.getOutput();

                    break;
                case DELETE:
                    int maxDeletions = genome.read(Gene.MAX_DELETIONS);
                    if (genome.numDeletions <= maxDeletions) {
                        ArrayList<Entity> ae = sampleForward();
                        double ao = 0;
                        for (int i = 0; i < ae.size(); i++) {
                            Entity ent = ae.get(i);
                            brain.input(ent, world);
                        }
                        brain.ann.input.calc();
                        ao = brain.getOutput();

                        double l = Utility.flatten((long) ao, Settings.CHAR_SET);
                        long lng = (long) l;
                        char c = Long.toHexString(lng).charAt(0);

                        String right = brain.entity.genome.code.substring(0, brain.entity.genome.index);
                        String left = brain.entity.genome.code.substring(right.length() - 1, brain.entity.genome.code.length());

                        brain.entity.genome.code = right + left;
                        genome.numDeletions++;
                    }
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
                        brain.entity.genome.code += c;
                        genome.numAppends++;
                        process(Action.JUMP, world, depth);

                    } catch (Exception ex) {
                    }
                    break;
                case SCAN:

                    try {
                        if (!isTrajectoryGoal()) {

                            degree += Math.random() * 360d;
                            if (isTrajectoryDeath()) {
                                genome.advance();
                                Action a = Action.RECODE;
                                process(Action.SLOW, world, depth);
                                process(a, world, depth);
                            }
                        } else if( isTrajectoryGoal()) {
                            location.vx = targetvx;
                            location.vy = targetvy;

                            process(Action.FASTER, world, depth);
                            process(Action.CONTINUE, world, depth);
                        }

                    } catch (Exception ex) {
                        Log.info(ex);
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
                        Log.info(ex);
                    }
                    break;

                case MOVE_DOWN: {
                    double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vy += changey;

                    break;
                }
                case SLOW:
                    location.vy = location.vy / 2;
                    location.vx = location.vx / 2;
                    break;
                case FASTER:

                    if( isTrajectoryGoal()) {
                        location.vx = Math.max(targetvx * Settings.ACCELERATION, epsilon);; //Math.max(location.vx * Settings.ACCELERATION, epsilon); //targetvx;
                        location.vy = Math.max(targetvy * Settings.ACCELERATION, epsilon);; //Math.max(location.vy * Settings.ACCELERATION, epsilon);;

                        process(Action.FASTER, world, depth);
                        process(Action.CONTINUE, world, depth);
                        break;
                    }
                    else if( Utility.precision(location.vx, Math.abs(0), epsilon )){
                        degree++;
                        location.vx += Math.max(location.vx * Settings.ACCELERATION, epsilon);
                        if( Math.random() > 0.5 ){
                            location.vx = -location.vx;
                            degree = -degree;
                        }
                        break;
                    }
                    else if( Utility.precision(location.vy, Math.abs(0), epsilon ) ){
                        degree++;
                        location.vy += Math.max(location.vy * Settings.ACCELERATION, epsilon);
                        if( Math.random() > 0.5 ){
                            degree--;
                            location.vy = -location.vy;
                        }
                        break;
                    }
                    else {
                        process(Action.SCAN, world, depth);

                    }

                    location.vy = Math.max(location.vy * (1 + Settings.ACCELERATION), epsilon);
                    location.vx = Math.max(location.vx * (1 + Settings.ACCELERATION), epsilon);
                    break;

                case MOVE_UP: {
                    double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vy -= changey;
                    break;
                }
                case MOVE_UP_RIGHT: {
                    double changex = Math.max(location.vx * Settings.ACCELERATION, Settings.ACCELERATION);
                    double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vx += changex;
                    location.vy -= changey;
                    break;
                }
                case MOVE_UP_LEFT: {
                    double changex = Math.max(location.vx * Settings.ACCELERATION, Settings.ACCELERATION);
                    double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vx -= changex;
                    location.vy -= changey;
                    break;
                }
                case MOVE_DOWN_RIGHT: {
                    double changex = Math.max(location.vx * Settings.ACCELERATION, Settings.ACCELERATION);
                    double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vx += changex;
                    location.vy += changey;
                    break;
                }
                case MOVE_DOWN_LEFT: {
                    double changex = Math.max(location.vx * Settings.ACCELERATION, Settings.ACCELERATION);
                    double changey = Math.max(location.vy * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vx -= changex;
                    location.vy += changey;
                    break;
                }
                case MOVE_LEFT: {
                    double changex = Math.max(location.vx * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vx -= changex;
                    break;
                }
                case MOVE_RIGHT: {
                    double changex = Math.max(location.vx * Settings.ACCELERATION, Settings.ACCELERATION);
                    location.vx += changex;
                    break;
                }
                case KILL:
                    if (target) {
                        if (isTrajectoryGoal()) {
                            process(Action.CONTINUE, world, depth);
                        } else {
                            process(Action.SCAN, world, depth);
                        }
                        break;
                    }

                    doKill(action);
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
        if (location.x < 0) {
            location.x = 0;
            location.vx = -location.vx;
        }
        if (location.y < 0) {
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


        location.x = location.x + location.vx;
        location.y = location.y + location.vy;

        double v = Math.atan2(location.vx, location.vy);

        isTrajectoryGoal();

        double radiansToDegrees = 360d / Math.PI;
        degree = v * radiansToDegrees; //
        degree = degree + World.offset;

        if( last == action ){
            action = Action.CONTINUE;
        }
        last = action;


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
                        }
                        double eo = o.getEnergy();

                        extracted = eo - extracted;
                        if (extracted > o.getEnergy()) {
                            eo = o.getEnergy();
                            extracted = eo;
                        }

                        setEnergy(getEnergy() + Math.abs(extracted));
                        size = size + (o.size / 8);
                        o.size = o.size - (size / 8);
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
                        o.setEnergy(o.getEnergy() - extracted);
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
