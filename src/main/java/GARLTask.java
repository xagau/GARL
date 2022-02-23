import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

enum Action {
    NONE,
    MOVE_RIGHT,
    MOVE_LEFT,
    MOVE_UP,
    MOVE_DOWN,
    MOVE_UP_RIGHT,
    MOVE_UP_LEFT,
    MOVE_DOWN_LEFT,
    MOVE_DOWN_RIGHT,
    MOVE_DIRECTION,
    SAMPLE_FORWARD,
    SAMPLE_SELF,
    JUMP,
    RANDOM,
    COS,
    SIN,
    TAN,
    SLOW,
    STOP,
    KILL,
}

abstract class NeuralLayer {
    protected int numberOfNeuronsInLayer;
    private ArrayList<Neuron> neuron;
    protected IActivationFunction activationFnc;
    protected NeuralLayer previousLayer;
    protected NeuralLayer nextLayer;
    protected ArrayList<Double> input;
    protected ArrayList<Double> output;
    protected int numberOfInputs;

    protected void init() {
        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            try {
                neuron.get(i).setActivationFunction(activationFnc);
                neuron.get(i).init();
            } catch (IndexOutOfBoundsException iobe) {
                neuron.add(new Neuron(numberOfInputs, activationFnc));
                neuron.get(i).init();
            }
        }
    }

    protected void calc() {
        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            neuron.get(i).setInputs(this.input);
            neuron.get(i).calc();
            try {
                output.set(i, neuron.get(i).getOutput());
            } catch (IndexOutOfBoundsException iobe) {
                output.add(neuron.get(i).getOutput());
            }
        }
    }
}

class InputLayer extends NeuralLayer {
    public InputLayer(int numberofneurons, IActivationFunction iaf, int numberofinputs) {
        this.numberOfInputs = numberofinputs;
        this.numberOfNeuronsInLayer = numberofneurons;
        this.activationFnc = iaf;
    }
}

class HiddenLayer extends NeuralLayer {
    public HiddenLayer(int numberofneurons, IActivationFunction iaf, int numberofinputs) {
        this.numberOfInputs = numberofinputs;
        this.numberOfNeuronsInLayer = numberofneurons;
        this.activationFnc = iaf;
    }
}

class OutputLayer extends NeuralLayer {
    public OutputLayer(int numberofneurons, IActivationFunction iaf,
                       int numberofinputs) {
        this.numberOfInputs = numberofinputs;
        this.numberOfNeuronsInLayer = numberofneurons;
        this.activationFnc = iaf;
    }
}

class SinusoidFunction implements IActivationFunction {

    public SinusoidFunction(double gene) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calc(double summedInput) {
        return Math.sin(summedInput);
    }

    public double derivative(double net) {
        return Math.cos(net);
    }

}

/**
 * Linear combination activation function implementation, the output unit is
 * simply the weighted sum of its inputs plus a bias term.
 */
class LinearCombinationFunction implements IActivationFunction {

    /**
     * Bias value
     */
    private double bias;

    @Override
    public double calc(double summedInput) {
        return summedInput + bias;
    }

    public double derivative(double totalInput) {
        // TODO Auto-generated method stub
        return 0;
    }

}


class StepFunction implements IActivationFunction {

    private double yAbove = 1d;

    private double yBellow = 0d;

    private double threshold = 0d;

    public StepFunction(double threshold) {
        this.threshold = threshold;
    }


    public double calc(double summedInput) {
        if (summedInput >= threshold)
            return yAbove;
        else
            return yBellow;
    }


    public double deerivative(double input) {
        return 1d;
    }

}

/**
 * This class represents the pure linear activation function, implementing the
 * interface IActivationFunction
 *
 * @author Alan de Souza, Fábio Soares
 * @version 0.1
 */
class Linear implements IActivationFunction {
    private double a = 1.0;

    public Linear() {

    }

    public Linear(double value) {
        this.setA(value);
    }

    public void setA(double value) {
        this.a = value;
    }

    @Override
    public double calc(double x) {
        return a * x;
    }

    public double derivative(double x) {
        return a;
    }
}

class Sigmoid implements IActivationFunction {
    private double a = 1.0;

    public Sigmoid(double _a) {
        this.a = _a;
    }

    @Override
    public double calc(double x) {
        return 1.0 / (1.0 + Math.exp(-a * x));
    }
}


class ReluFunction implements IActivationFunction {
    private double a = 0.0;

    public ReluFunction(double _a) {
        this.a = _a;
    }

    @Override
    public double calc(double x) {
        return Math.max(a, x);
    }
}


class HTANFunction implements IActivationFunction {
    private double a = 0.0;

    public HTANFunction(double _a) {
        this.a = _a;
    }

    @Override
    public double calc(double x) {
        return Math.tanh(a * x);
    }
}


class NegateFunction implements IActivationFunction {
    private double a = 0.0;

    public NegateFunction(double _a) {
        this.a = _a;
    }

    @Override
    public double calc(double x) {
        if (x >= a) {
            x = -x;
            return x;
        }
        return -a;
    }
}

enum ActivationFunction {
    STEP, LINEAR, SIGMOID, HYPERTAN, RELU, SINUSMOID, NEGATE
}

interface IActivationFunction {
    double calc(double x);
}

class Neuron {
    protected ArrayList<Double> weight;
    private ArrayList<Double> input;
    private Double output;
    private Double outputBeforeActivation;
    private int numberOfInputs = 0;
    protected Double bias = 1.0;
    private IActivationFunction activationFunction;

    public Neuron(int numberofinputs, IActivationFunction iaf) {
        numberOfInputs = numberofinputs;
        weight = new ArrayList<>(numberofinputs + 1);
        input = new ArrayList<>(numberofinputs);
        activationFunction = iaf;
    }

    public void setActivationFunction(IActivationFunction iaf) {
        activationFunction = iaf;
    }

    public void init() {
        Random rand = new Random();
        for (int i = 0; i <= numberOfInputs; i++) {
            double newWeight = rand.nextDouble();
            try {
                this.weight.set(i, newWeight);
            } catch (IndexOutOfBoundsException iobe) {
                this.weight.add(newWeight);
            }
        }
    }

    public void calc() {
        outputBeforeActivation = 0.0;
        if (numberOfInputs > 0) {
            if (input != null && weight != null) {
                for (int i = 0; i <= numberOfInputs; i++) {
                    outputBeforeActivation += (i == numberOfInputs ? bias : input.get(i)) * weight.get(i);
                }
            }
        }
        output = activationFunction.calc(outputBeforeActivation);
    }

    public void setInputs(ArrayList<Double> input) {
        this.input.addAll(input);
    }

    public Double getOutput() {
        return output;
    }

}

class ActivationFactory {
    static IActivationFunction create(int gene) {
        int index = gene % ActivationFunction.values().length;
        Field[] fl = ActivationFunction.class.getDeclaredFields();
        Field f = fl[index];
        ActivationFunction af = ActivationFunction.valueOf(f.getName());
        switch (af) {
            case SIGMOID:
                Sigmoid sigmoid = new Sigmoid((double) gene);
                return sigmoid;
            case LINEAR:
                Linear linear = new Linear(1.0 * gene);
                return linear;
            case SINUSMOID:
                SinusoidFunction sm = new SinusoidFunction((double) gene);
                return sm;
            case STEP:
                StepFunction st = new StepFunction((double) gene);
                return st;
            case HYPERTAN:
                HTANFunction ht = new HTANFunction((double) gene);
                return ht;
            case RELU:
                ReluFunction rl = new ReluFunction((double) gene);
                return rl;
            case NEGATE:
                NegateFunction nl = new NegateFunction((double) gene);
                return nl;
            default:
                ReluFunction relu = new ReluFunction((double) gene);
                return relu;
        }
    }
}

class NeuralNet {
    InputLayer input = null;
    HiddenLayer dense = null;
    HiddenLayer second = null;
    OutputLayer output = null;
    Genome owner = null;

    public NeuralNet(Genome g) {
        owner = g;
        int numInputs = (int) g.read(Gene.SENSORY) % Settings.MAX_SIZE;
        int numDense = (int) g.read(Gene.DENSE) % Settings.MAX_SIZE;
        int numSecond = (int) g.read(Gene.HIDDEN) % Settings.MAX_SIZE;

        Sigmoid sigmoid = new Sigmoid(1.0);
        try {
            output = new OutputLayer(1, sigmoid, numDense);
        } catch (Exception ex) {
        }

        IActivationFunction iaf0 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_0));
        try {
            second = new HiddenLayer(numSecond, iaf0, g.read(Gene.HIDDEN));
            second.nextLayer = output;
        } catch (Exception ex) {
        }

        IActivationFunction iaf1 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_1));
        try {
            dense = new HiddenLayer(numDense, iaf1, g.read(Gene.DENSE));
            dense.nextLayer = second;
        } catch (Exception ex) {
        }


        IActivationFunction iaf2 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_2));
        try {
            input = new InputLayer(numInputs, iaf2, 1);
            input.nextLayer = dense;
        } catch (Exception ex) {
        }

        try {
            input.previousLayer = null;
            dense.previousLayer = input;
            second.previousLayer = dense;
            output.previousLayer = second;

            output.init();
            input.init();
            dense.init();
            second.init();
        } catch (Exception ex) {
        }

    }

    public Action output(World world) {

        int c = owner.index();
        double d = c;
        owner.advance();
        Action a = ActionFactory.create(d);
        return a;
    }
}

class ActionFactory {
    static Action create(double input) {
        int len = Action.class.getDeclaredFields().length;
        double o = (double) (input % len);
        int n = (int) Math.round(o);
        try {
            Field[] list = Action.class.getDeclaredFields();
            String name = list[n].getName();
            Action a = Action.valueOf(name);
            if (a.equals(Action.RANDOM)) {
                return create(Math.random() * len);
            } else if (a.equals(Action.SIN)) {
                return create(Math.sin(input) * len);
            } else if (a.equals(Action.COS)) {
                return create(Math.cos(input) * len);
            } else if (a.equals(Action.TAN)) {
                return create(Math.tan(input) * len);
            }

            return a;
        } catch (Exception e) {
            return Action.RANDOM;
        }
    }

    static Action create(double input, Action a) {
        int len = Action.class.getDeclaredFields().length;
        double o = (double) (input % len);
        int n = (int) Math.round(o);

        try {
            Field[] list = Action.class.getDeclaredFields();
            String name = list[n].getName();

            if (a.equals(Action.RANDOM)) {
                return create(Math.random() * len);
            } else if (a.equals(Action.SIN)) {
                return create(Math.sin(input) * len);
            } else if (a.equals(Action.COS)) {
                return create(Math.cos(input) * len);
            }

            return a;
        } catch (Exception e) {

            return Action.RANDOM;
        }
    }


}

class Activity {
    int x, y;
    double vx, vy;
}

class GenomeFactory {
    public static String create(int numSequence) {

        if (numSequence <= 32) {
            numSequence = 32;
        }
        String code = "";
        for (int i = 0; i < numSequence; i++) {
            code += UUID.randomUUID().toString();
        }
        return code;
    }


}

class Genome {
    static String DEAD = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    Entity owner = null;
    String code = null;

    Genome(Entity owner) {
        code = GenomeFactory.create(Settings.GENOME_LENGTH);

        this.owner = owner;
        code = code.replaceAll("-", "");
    }


    public char read(int loc) {
        if (loc < code.length()) {
            return code.charAt(loc);
        }
        char c = code.charAt(0);
        return c;
    }

    public void jump(int loc)
    {
        if( loc < code.length()){
            index = loc;
        }
    }

    int index = 0;
    public void advance() {
        index++;
        if (index >= code.length()) {
            index = 0;
        }
    }

    public int index() {
        return index;
    }

    public void mutate() {
        char[] c = code.toCharArray();
        int index = c.length - 1;
        int mutations = c[Gene.GENE_MUTATION_PROBABILITY];
        for(int j = 0; j < mutations; j++ ) {
            index = (int) (Math.random()) * index;
            if (index < 0) {
                index = 0;
            } else if (index >= c.length) {
                index = c.length - 1;
            }
            try {
                char t = c[index];
                c[index] = c[index - 1];
                c[index - 1] = t;
            } catch (Exception ex) {
            }
        }
        c[Gene.KIN] = GenomeFactory.create(Settings.GENOME_LENGTH).charAt(Gene.KIN);
        code = String.valueOf(c);
    }
}

class Line {
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
            //System.out.println("Lines share an endpoint.");
        } else if (intersects && !shareAnyPoint) {
            //System.out.println("Lines intersect.");
        } else {
            //System.out.println("Lines neither intersect nor share a share an endpoint.");
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

        //handle special case where the line is vertical
        if (Double.isInfinite(m)) {
            if (A.x == P.x) return true;
            else return false;
        }

        if ((P.y - A.y) == m * (P.x - A.x)) return true;
        else return false;
    }
}


class GFG {
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


class Entity {
    NeuralNet brain = null;
    Activity location = new Activity();
    Genome genome = null; //new Genome();
    Entity parent = null;
    int generation = 0;
    boolean fertile = false;
    private double energy = 1;
    int size = 1;
    double degree = 0; // must be 0 - 360 to specify the direction the entity is facing.
    boolean selected = false;
    Action last = Action.RANDOM;

    public boolean isTouching(Entity e) {
        if (e == null) {
            return false;
        }
        int t = GFG.circle(location.x, location.y, e.location.x,
                e.location.y, size / 2, e.size / 2);
        if (t == 1) {
            return true;
        } else if (t < 0) {
            return false;
        } else {
            return true;
        }
    }

    Entity touching = null;

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

    public void die()
    {
        alive = false;
        genome.code = Genome.DEAD;

    }

    public boolean intersects(Entity a, Entity b) {
        int a_startX = a.location.x;
        int a_startY = a.location.y;
        int a_endX = a.location.x + a.size;
        int a_endY = a.location.y + a.size;

        int b_startX = b.location.x;
        int b_startY = b.location.y;
        int b_endX = b.location.x + b.size;
        int b_endY = b.location.y + b.size;

        Line l1 = new Line(a_startX, a_startY, a_endX, a_endY);
        Line l2 = new Line(b_startX, b_startY, b_endX, b_endY);


        boolean bb = Line.intersects(l1, l2);

        if (bb) {
            return true;
        }

        return false;
    }

    public Entity sampleForward() {
        Entity e = null;
        for (int i = 0; i < world.list.size(); i++) {
            Entity ent = world.list.get(i);
            // should be closest.
            if (ent != this && ent != null) {
                if (intersects(this, ent)) {
                    return ent;
                }

            }
        }
        return null;
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


    Color color = Color.BLUE;
    World world = null;
    boolean alive = true;

    Entity(World world) {
        this.world = world;
        genome = new Genome(this);
        brain = new NeuralNet(genome);
        float r, g, b;
        r = genome.read(Gene.SENSORY);
        g = genome.read(Gene.HIDDEN);
        b = genome.read(Gene.SIZE);
        color = Color.getHSBColor(r, g, b);
        size = genome.read(Gene.SIZE) % Settings.MAX_SIZE;
    }

    Entity replicate() {
        return clone();
    }

    public Entity clone() {
        Entity e = new Entity(world);
        e.alive = true;
        if (Math.random() > 0.5) {
            e.location.x = location.x + Settings.CELL_MOVEMENT + e.size;
        } else {
            e.location.x = location.x - Settings.CELL_MOVEMENT - e.size;
        }
        if (Math.random() < 0.5) {
            e.location.y = location.y + Settings.CELL_MOVEMENT + e.size;
        } else {
            e.location.y = location.y - Settings.CELL_MOVEMENT - e.size;
        }


        if (Math.random() < Settings.MUTATION_RATE) {
            genome.mutate();
        }
        e.genome.code = genome.code;
        e.brain = new NeuralNet(e.genome);

        e.energy = energy;
        e.degree = Math.random() * 360;
        e.parent = this;
        e.generation = generation + 1;
        e.fertile = false;

        return e;
    }

    int age = 0;

    private void consume() {
        if (location.vx != 0 || location.vy != 0) {
            setEnergy(getEnergy() - Settings.ENERGY_STEP_COST);
        } else {
            setEnergy(getEnergy() - Settings.ENERGY_STEP_SLEEP_COST);
        }
    }

    public Action think(World world) {
        age++;
        consume();
        Action action = brain.output(world);

        process(action, world);
        world.setState(action);
        int vx = (int) location.vx;
        int vy = (int) location.vy;

        if (vx > Settings.MAX_SPEED) {
            vx = Settings.MAX_SPEED;
        }
        if (vy > Settings.MAX_SPEED) {
            vy = Settings.MAX_SPEED;
        }
        location.vx = vx;
        location.vy = vy;


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

        location.x = location.x + vx;
        location.y = location.y + vy;

        return action;
    }


    public static long checksum(String in) {
        return getCRC32Checksum(in.getBytes());
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    public static double flatten(long v, long max) {
        double d = (double) ((double) v % (double) max);
        return d;
    }

    public void doLast(Action action){
        boolean t = isTouching();
        if (touching != null) {
            if( touching.alive == false ){
                setEnergy(getEnergy() + touching.getEnergy());
                size = size + (touching.size / 8);
                if (size > Settings.MAX_SIZE) {
                    size = Settings.MAX_SIZE;
                }
                world.list.remove(touching);
                return;
            }
            else if (touching.genome.read(Gene.KIN) == genome.read(Gene.KIN)) {
                touching.fertile = true;
                fertile = true;
                t = false;
            }
        }
        if (t) {
            action = Action.KILL;
        } else {
            double r = Math.random();
            if (r > 0.66) {
                action = ActionFactory.create(r, Action.SIN);
            } else if (r > 0.33) {
                action = ActionFactory.create(r, Action.COS);
            } else {
                action = ActionFactory.create(r, Action.TAN);
            }
            process(action, world);
        }
    }

    public void process(Action action, World world) {

        if (action == last) {
            doLast(action);
            return;
        } else {
            last = action;
        }
        switch (action) {
            case STOP:
                // if we're stopped - and we're touching someone, lets move.
                if (isTouching()) {
                    process(Action.SAMPLE_FORWARD, world);
                } else {
                    location.vx = 0;
                    location.vy = 0;
                }
                break;
            case RANDOM:
                Action a = ActionFactory.create(Math.random());
                process(a, world);
                break;
            case SAMPLE_SELF:
                //TODO: Change up
                long cs = checksum(genome.code);
                double d = flatten(cs, 1);
                d += flatten(size, 1);
                d += flatten((int)energy, 1);
                d += flatten(age, 1);
                d += flatten(genome.read(genome.read(Gene.AGE)), 1);
                d += flatten(fertile?0:1, 1);

                Action flattenedAction = ActionFactory.create(d);
                process(flattenedAction, world);

                break;
            case JUMP:
                long js = checksum(genome.code);
                double gl = flatten(js, genome.code.length());
                genome.jump((int)gl);
                break;
            case SAMPLE_FORWARD:
                //TODO: Change up
                Entity e = sampleForward();
                if (e != null && e != this) {
                    long lcs = checksum(e.genome.code);
                    double ld = flatten(lcs, 1);
                    ld += flatten(e.size, 1);
                    Action lflattenedAction = ActionFactory.create(ld);
                    process(lflattenedAction, world);
                } else {
                    Action ar = ActionFactory.create(Math.random());
                    process(ar, world);
                }
                break;

            case MOVE_DIRECTION:
                process(Action.MOVE_DOWN, world);
                break;
            case NONE:
                process(last, world);
                break;
            case MOVE_DOWN:
                location.vy += Settings.CELL_MOVEMENT;

                break;
            case SLOW:
                location.vy -= Settings.CELL_MOVEMENT;
                location.vx -= Settings.CELL_MOVEMENT;
                break;
            case MOVE_UP:
                location.vy -= Settings.CELL_MOVEMENT;

                break;
            case MOVE_UP_RIGHT:
                location.vx += Settings.CELL_MOVEMENT;
                location.vy -= Settings.CELL_MOVEMENT;
                break;
            case MOVE_UP_LEFT:
                location.vx -= Settings.CELL_MOVEMENT;
                location.vy -= Settings.CELL_MOVEMENT;
                break;
            case MOVE_DOWN_RIGHT:
                location.vx += Settings.CELL_MOVEMENT;
                location.vy += Settings.CELL_MOVEMENT;
                break;
            case MOVE_DOWN_LEFT:
                location.vx -= Settings.CELL_MOVEMENT;
                location.vy += Settings.CELL_MOVEMENT;
                break;
            case MOVE_LEFT:
                location.vx -= Settings.CELL_MOVEMENT;

                break;
            case MOVE_RIGHT:
                location.vx += Settings.CELL_MOVEMENT;

                break;
            case KILL:
                doKill(action);
                break;
        }

        double v = Math.atan2(location.vx, location.vy);

        double radiansToDegrees = 180d / Math.PI;
        degree = v * radiansToDegrees;
        degree = (degree + 360) % 360;

    }

    public void doKill(Action action) {
        if (action == Action.KILL) {
            for (int i = 0; i < world.list.size(); i++) {
                Entity o = world.list.get(i);
                if (o != null) {
                    if (isTouching(o) && o != this) {

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
                        o.setEnergy(o.getEnergy() - extracted);
                        if (o.getEnergy() <= 0 || o.size <= 0) {
                            o.energy = 0;
                            o.genome.code = Genome.DEAD;
                            o.alive = false;
                            o.location.vx = 0;
                            o.location.vy = 0;
                            world.list.remove(o);
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

class Gene {
    final static int AGE = 26;
    final static int SENSORY = 0;
    final static int SIZE = 9;
    final static int MATURITY = 23;
    final static int RR = 2;
    final static int DENSE = 3;
    final static int HIDDEN = 4;
    final static int KIN = 5;
    final static int KILL = 10;
    final static int ATTACK = 11;
    final static int DEFENSE = 12;
    final static int ACTIVATION_FUNCTION_0 = 22;
    final static int ACTIVATION_FUNCTION_1 = 25;
    final static int ACTIVATION_FUNCTION_2 = 24;
    final static int GENE_MUTATION_PROBABILITY = 27;



}

class World extends JLabel {
    ArrayList<Entity> list = new ArrayList<>();

    Selection selection = null;

    int width;
    int height;

    World(int w, int h) {
        width = w;
        height = h;
        backbuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    Image backbuffer = null;

    World(ArrayList<Entity> population, Selection selection, int w, int h) {
        this.list = population;
        this.selection = selection;
        width = w;
        height = h;

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

    public Action getPreviousState() {
        return last;
    }


    HashMap<String, Entity> taken = new HashMap<String, Entity>();
    int step = 0;
    double phl = 0;
    double increment = 0.0010000;
    int mx = 0;
    int my = 0;
    Entity selected = null;


    private static void drawVisibilityCircle(Graphics2D g2d, Point center, float r, Color c) {
        float radius = r;
        float[] dist = {0f, 1f};
        Color[] colors = {new Color(0, 0, 0, 0), c};
        //workaround to prevent background color from showing
        drawBackGroundCircle(g2d, radius, Color.WHITE, center);
        drawGradientCircle(g2d, radius, dist, colors, center);
    }

    private static void drawBackGroundCircle(Graphics2D g2d, float radius, Color color, Point2D center) {

        g2d.setColor(color);
        radius -= 1;//make radius a bit smaller to prevent fuzzy edge
        g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY()
                - radius, radius * 2, radius * 2));
    }

    private static void drawGradientCircle(Graphics2D g2d, float radius, float[] dist, Color[] colors, Point2D center) {
        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(rgp);
        g2d.fill(new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2));
    }

    /*
    private static BufferedImage createImage(JPanel panel) {
        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        return bi;
    }
    */


    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        DecimalFormat df = new DecimalFormat("0.00000000");

        step++;
        phl += increment;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        Rectangle[] rlist = selection.list;

        for (int j = 0; j < rlist.length; j++) {
            g2.setColor(Color.PINK);
            if (rlist[j] != null) {
                g.fillRect(rlist[j].x, rlist[j].y, rlist[j].width, rlist[j].height);
            }
        }

        int livingCount = 0;
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);

            int r = e.size / 2;
            if (e.alive) {
                livingCount++;
                g2.setColor(e.color);
            } else {
                g2.setColor(Color.BLUE);
            }
            Point p = new Point(e.location.x + r / 2, e.location.y + r / 2);
            if (r > 0) {
                drawVisibilityCircle(g2, p, r, g.getColor());
            }

            if (e == selected) {
                g2.drawOval(e.location.x - (r / 2), e.location.y - (r / 2), r * 2, r * 2);
            }

            double direction = e.degree;
            int xs = (int) ((int) (e.location.x + r) + (e.size * Math.cos(direction * ((Math.PI) / 360d)))); //);
            int ys = (int) ((int) (e.location.y + r) - (e.size * Math.sin(direction * ((Math.PI) / 360d)))); //);

            g2.drawLine(e.location.x + r / 2, e.location.y + (r / 2), xs, ys);

            if (e == selected) {

                double d1 = (direction - 45);
                double d2 = (direction + 45);
                int _xs1 = (int) ((int) (e.location.x + r) + (e.size * Math.cos(d1 * ((Math.PI) / 360d))));
                int _ys1 = (int) ((int) (e.location.y + r) - (e.size * Math.sin(d1 * ((Math.PI) / 360d))));
                int _xs2 = (int) ((int) (e.location.x + r) + (e.size * Math.cos(d2 * ((Math.PI) / 360d))));
                int _ys2 = (int) ((int) (e.location.y + r) - (e.size * Math.sin(d2 * ((Math.PI) / 360d))));
                g2.drawLine(e.location.x + (r / 2), e.location.y + (r / 2), _xs1, _ys1);
                g2.drawLine(e.location.x + (r / 2), e.location.y + (r / 2), _xs2, _ys2);

            }

            //taken.put(e.location.x + "," + e.location.y, e);

        }


        drawPopup(g2, selected, mx, my);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, getHeight() - 24, getWidth(), getHeight());
        g2.setColor(Color.YELLOW);
        g2.drawString("Think:" + step + " population:" + livingCount + " earnings: " + df.format(phl) + " PHL " + getWidth() + " x " + getHeight(), 10, (getHeight() - 10) );


        //g.drawImage(backbuffer, 0, 0, null);

    }


    public void drawPopup(Graphics g, Entity e, int mx, int my) {

        int spacing = 14;
        int popupWidth = 340;
        int popupHeight = 370;

        if (e != null) {
            boolean b = e.world.list.contains(e);
            if (!b) {
                return;
            }
        }
        if (e != null && e.selected == true && e.alive == true) {
            DecimalFormat df = new DecimalFormat("0.00");
            g.setColor(Color.white);
            g.fillRect(mx, my, popupWidth, popupHeight);
            //g.setColor(e.color);
            //g.drawOval((e.location.x)-(e.size/2)+10, (e.location.y*2)-10, (e.size*2) + 10, (e.size*2) + 10);
            g.setColor(Color.BLACK);

            g.drawString("Position: X " + e.location.x + ": Y " + e.location.y, mx + spacing, my + spacing * 1);
            g.drawString("Size:" + e.size, mx + spacing, my + spacing * 2);
            g.drawString("Age:" + e.age, mx + spacing, my + spacing * 3);
            g.drawString("Energy:" + df.format(e.getEnergy()), mx + spacing, my + spacing * 4);
            g.drawString("Degree: " + e.degree, mx + spacing, my + spacing * 5);
            g.drawString("VX: " + e.location.vx, mx + spacing, my + spacing * 6);
            g.drawString("VY: " + e.location.vy, mx + spacing, my + spacing * 7);
            g.drawString("Alive: " + e.alive, mx + spacing, my + spacing * 8);
            g.drawString("Reproductive Number: " + (int) e.genome.read(Gene.RR), mx + spacing, my + spacing * 9);
            g.drawString("Kill Gene: " + (int) e.genome.read(Gene.KILL), mx + spacing, my + spacing * 10);

            try {
                g.drawString("Thought: " + e.last.toString(), mx + spacing, my + spacing * 11);
            } catch (Exception ex) {
            }
            g.drawString("Genome:", mx + spacing, my + spacing * 12);
            g.drawString(e.genome.code.substring(0, 32), mx + spacing, my + spacing * 13);
            g.drawString("Drift: ", mx + spacing, my + spacing * 14);
            g.setColor(e.color);
            g.fillRect(mx + spacing, my + spacing * 15, popupWidth - 30, 10);
            g.setColor(Color.black);
            g.drawString("Generation: " + e.generation, mx + spacing, my + spacing * 17);
            g.setColor(Color.black);
            g.drawString("Touching: " + e.isTouching(), mx + spacing, my + spacing * 18);
            g.setColor(Color.black);
            g.drawString("Last Thought: " + e.getLast(), mx + spacing, my + spacing * 19);
            int sz = UUID.randomUUID().toString().replaceAll("-", "").length();
            g.drawString("Genome Length: " + e.genome.code.length() / sz, mx + spacing, my + spacing * 20);
            g.drawString("KIN: " + e.genome.read(Gene.KIN), mx + spacing, my + spacing * 21);
            g.drawString("Fertile: " + e.fertile, mx + spacing, my + spacing * 22);
            g.drawString("Read Position: " + e.genome.index(), mx + spacing, my + spacing * 23);
            g.drawString("Death: " + Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE), mx + spacing, my + spacing * 24);


        }

    }

}

class Population {
    public static ArrayList<Entity> create(World world, int individuals, int width, int height) {
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < individuals; i++) {
            Entity e = new Entity(world);
            e.location.x = rand.nextInt(width);
            e.location.y = rand.nextInt(height);

            entities.add(e);
        }

        return entities;
    }

    public static ArrayList<Entity> create(World world, Entity seed, int individuals, int width, int height) {
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < individuals; i++) {
            Entity e = seed.clone();
            e.location.x = rand.nextInt(width);
            e.location.y = rand.nextInt(height);
            entities.add(e);
        }

        return entities;
    }
}

class Settings {
    static int GENOME_LENGTH = 32;
    static int STARTING_POPULATION = 2000;
    static int MAX_OFFSPRING = 3;
    final static int MAX_AGE = 10;
    final static int DEATH_MULTIPLIER = 15;

    final static int MAX_SIZE = 16;
    final static int SELECTION_EVENT = 1;
    final static double MUTATION_RATE = 0.3;
    final static int CELL_MOVEMENT = 1;
    final static int MAX_SPEED = 5;
    final static int MAX_POPULATION = 1000;

    final static double ENERGY_STEP_COST = 0.01;
    final static double ENERGY_STEP_SLEEP_COST = 0.001;


}

class Selection {

    Rectangle[] list = new Rectangle[8];

    Selection() {

        list[0] = new Rectangle();
        list[0].x = 100;
        list[0].y = 100;
        list[0].width = 400;
        list[0].height = 10;


        list[1] = new Rectangle();
        list[1].x = 100;
        list[1].y = 100;
        list[1].width = 10;
        list[1].height = 400;

        list[2] = new Rectangle();
        list[2].x = 500;
        list[2].y = 500;
        list[2].width = 10;
        list[2].height = 400;


        list[3] = new Rectangle();
        list[3].x = 200;
        list[3].y = 500;
        list[3].width = 10;
        list[3].height = 800;

        list[4] = new Rectangle();
        list[4].x = 0;
        list[4].y = 300;
        list[4].width = 10;
        list[4].height = 2800;

        list[5] = new Rectangle();
        list[5].x = 700;
        list[5].y = 200;
        list[5].width = 10;
        list[5].height = 2800;

        list[6] = new Rectangle();
        list[6].x = 0;
        list[6].y = 0;
        list[6].width = 3000;
        list[6].height = 10;

        list[7] = new Rectangle();
        list[7].x = 990;
        list[7].y = 0;
        list[7].width = 10;
        list[7].height = 700;

    }


    public boolean insideRect(Rectangle rect, int x, int y) {
        if (x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height) {
            return true;
        } else {
            return false;
        }
    }


}

class MouseHandler implements MouseMotionListener {

    World world = null;

    MouseHandler(World world) {
        this.world = world;
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        this.world.mx = mouseEvent.getX();
        this.world.my = mouseEvent.getY();
        ArrayList<Entity> list = world.list;
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (e != null) {
                if (e.isTouching(this.world.mx, this.world.my)) {
                    this.world.selected = e;
                    e.selected = true;
                    this.world.repaint();
                    return;
                }
            }
        }
    }
}

public class GARLTask {

    public boolean isRunning() {
        return running;
    }

    final static int FPS = 32;

    public static void main(String[] args) {
        GARLTask task = new GARLTask();
        //task.start();
        task.run();
    }

    Selection selection = new Selection();
    JFrame frame = new JFrame("Genetic Based Multi-Agent Reinforcement Learning");

    World world = null;// new World();

    public void run() {


        //1. Create the frame.

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int width = 1848;
        int height = 1016;
        world = new World(width, height);

        frame.setSize(width, height);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        final ArrayList<Entity> population = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getWidth());
        world.setPopulation(population);
        world.setSelection(selection);
        MouseHandler handler = new MouseHandler(world);
        world.addMouseMotionListener(handler);

        world.setMaximumSize(new Dimension(width, height));
        world.setMinimumSize(new Dimension(width, height));
        world.setPreferredSize(new Dimension(width, height));
        //frame.setContentPane(world);
        frame.add(world);;//, BorderLayout.CENTER);
        //4. Size the frame.

        //5. Show it.
        frame.setVisible(true);

        TimerTask think = new TimerTask() {
            @Override
            public void run() {


                // Perform Selection.
                Selection selection = world.selection;

                Rectangle[] rlist = world.selection.list;
                int livingCount = 0;
                for (int i = 0; i < world.list.size(); i++) {
                    Entity e = world.list.get(i);
                    for (int j = 0; j < rlist.length; j++) {
                        if (rlist[j] != null) {
                            if (selection.insideRect(rlist[j], e.location.x, e.location.y)) {
                                e.die();
                            }
                        }
                    }

                    if (e.alive) {
                        e.think(world);
                    }

                    if (e.fertile && e.alive) {
                        int min = Math.max(32, e.genome.read(Gene.MATURITY));
                        if (e.alive && (e.age > min)) {
                            if (Math.random() > 0.8) {
                                int n = e.genome.read(Gene.RR) % Settings.MAX_OFFSPRING;
                                if( Settings.MAX_POPULATION > livingCount ){
                                    n = Math.min(1, n);
                                }
                                for (int j = 0; j < n; j++) {

                                    Entity a = e.replicate();
                                    e.fertile = false;
                                    world.list.add(a);
                                }
                                e.die();

                            }
                        }
                    }

                    if (e.age > Settings.DEATH_MULTIPLIER*e.genome.read(Gene.AGE)) {
                        e.die();
                    }
                    if (e.alive) {
                        livingCount++;
                    }

                }
                if (livingCount == 2) {
                    System.out.println("Recreate population");
                    Entity seed = null;
                    for(int i = 0; i < world.list.size(); i++ ){
                        Entity a = world.list.get(i);
                        if( a.alive ){
                            seed = a;
                        }
                    }
                    world.list = Population.create(world, seed, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                }

            }

        };

        Timer timer = new Timer();
        TimerTask paint = new TimerTask() {
            @Override
            public void run() {
                //System.out.print(".");
                try {
                    world.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } catch(Error e){
                    e.printStackTrace();
                }
                //System.out.print("+");
            }
        };

        timer.schedule(paint, 0, 31);
        timer.schedule(think, 0, 100);

    }

    boolean running = true;

    public void setRunning(boolean b) {
        running = b;
    }

    String args = null;

    public void setParameter(String args) {
        this.args = args;
    }


    public ArrayList<String> getCombinedGenome() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < world.list.size(); i++) {
            Genome g = world.list.get(i).genome;
            list.add(g.code.toString());
        }
        return list;
    }

}
