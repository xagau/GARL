import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

enum Action {
    MOVE_RIGHT,
    MOVE_LEFT,
    MOVE_UP,
    MOVE_DOWN,
    MOVE_UP_RIGHT,
    MOVE_UP_LEFT,
    MOVE_DOWN_LEFT,
    MOVE_DOWN_RIGHT,
    SCAN,
    IF,
    JUMP,
    COS,
    SIN,
    TAN,
    SLOW,
    STOP,
    KILL,
}

abstract class NeuralLayer {
    public NeuralNet owner;
    public String name;
    protected int numberOfNeuronsInLayer;
    protected ArrayList<Neuron> neuron = new ArrayList<>();
    protected IActivationFunction activationFnc;
    protected NeuralLayer previousLayer;
    protected NeuralLayer nextLayer;
    protected ArrayList<Double> input = new ArrayList<Double>();
    protected ArrayList<Double> output = new ArrayList<Double>();
    protected int numberOfInputs;

    protected void init() {


        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            activationFnc = ActivationFactory.create(owner.owner.read(owner.owner.index));
            owner.owner.advance();
                try {
                    neuron.get(i).setActivationFunction(activationFnc);
                    neuron.get(i).init();
                } catch (IndexOutOfBoundsException iobe) {
                    neuron.add(new Neuron(numberOfInputs, activationFnc));
                    neuron.get(i).init();
                }

        }
    }

    boolean debug = false;
    protected void calc() {
        if( debug ) {
            System.out.println(this.name + " calc() " + numberOfNeuronsInLayer);
        }
        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            if( previousLayer != null ) {
                if( debug ) {
                    System.out.println("input-" + name + ":" + previousLayer.input);
                }
                neuron.get(i).setInputs(previousLayer.input);
                neuron.get(i).calc();
            } else {
                if( debug ) {
                    System.out.println("input-" + name + ":" + input);
                }
                neuron.get(i).setInputs(input);
                neuron.get(i).calc();
            }
            try {
                output.set(i, neuron.get(i).getOutput());
            } catch (IndexOutOfBoundsException iobe) {
                //iobe.printStackTrace();
                output.add(neuron.get(i).getOutput());
            }
        }
        if(nextLayer != null ) {
            if( debug ) {
                System.out.println("Compute next Layer:");
            }
            nextLayer.input = output;
            nextLayer.calc();
        }
    }

    public void setNeuralNet(NeuralNet net){
        owner = net;
    }

    public NeuralNet getNeuralNet(){
        return owner;
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

    public IActivationFunction getActivationFunction() {
        return activationFnc;
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


class CosusoidFunction implements IActivationFunction {

    public CosusoidFunction(double gene) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calc(double summedInput) {
        return Math.cos(summedInput);
    }

    public double derivative(double net) {
        return Math.sin(net);
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
    private double bias = Math.random();

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
        if( x >= a ) { return Math.tanh(x); }
        return x;
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

class SoftmaxFunction implements IActivationFunction {

    private double max = 1.0;

    public SoftmaxFunction(double max) {
        this.max = max;
    }

    NeuralLayer layer = null;
    public void setLayer(NeuralLayer layer)
    {
        this.layer = layer;
    }
    @Override
    public double calc(double netInput) {
        double totalLayerInput = 0;
        // add max here for numerical stability - find max netInput for all neurons in this layer
        double max = netInput;

        if( layer == null ){
            System.out.println("Layer is null;");
            return 1;
        }
        ArrayList<Neuron> list = layer.neuron;
        for (int i = 0; i < list.size(); i++ ) {
            Neuron neuron = (Neuron)list.get(i);
            totalLayerInput += Math.exp(neuron.getOutput()-max);
        }

        double output = Math.exp(netInput-max) / totalLayerInput;
        return output;
    }

}

enum ActivationFunction {
    LINEAR, SIGMOID, HYPERTAN, RELU, SINUSMOID
}

interface IActivationFunction {
    double calc(double x);
}

class Neuron {
    protected ArrayList<Double> weight = new ArrayList<>();
    private ArrayList<Double> input = new ArrayList<>();
    private Double output = 0d;
    private Double outputBeforeActivation = 0d;
    private int numberOfInputs = 0;
    protected Double bias = Math.random();
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
                    double v =0 ;
                    try {
                        v = (i == numberOfInputs ? bias : input.get(i));
                    } catch(Exception ex) { v = bias ; }
                    outputBeforeActivation += v * weight.get(i);
                }
            }
        }
        output = activationFunction.calc(outputBeforeActivation);
    }

    public void setInputs(ArrayList<Double> input) {
        this.input.addAll(input);
    }

    public Double getOutput() {
        return Math.abs(output);
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
                Sigmoid sigmoid = new Sigmoid(1.0);
                return sigmoid;
            case LINEAR:
                Linear linear = new Linear(1.0 * gene);
                return linear;
            //case STEP:
            //    StepFunction st = new StepFunction((double) gene);
            //    return st;
            case HYPERTAN:
                HTANFunction ht = new HTANFunction((double) gene);
                return ht;
            case RELU:
                ReluFunction rl = new ReluFunction((double) gene);
                return rl;
            //case NEGATE:
            //    NegateFunction nl = new NegateFunction((double) gene);
            //    return nl;

            default:
                Sigmoid smx = new Sigmoid((double) gene);
                return smx;
        }
    }
}

class Globals {
    static GARLRectangle spawn = new GARLRectangle();
    static boolean verbose = true;
}

class NeuralNet {
    InputLayer input = null;
    HiddenLayer dense = null;
    HiddenLayer second = null;
    OutputLayer output = null;
    Genome owner = null;

    public NeuralNet(Genome g) {
        owner = g;
        int numInputs = Settings.NUMBER_OF_INPUTS;
        int numDense = (int) g.read(Gene.DENSE) % Settings.MAX_NEURONS;
        int numHidden = (int) g.read(Gene.HIDDEN) % Settings.MAX_NEURONS;

        SoftmaxFunction softmax = new SoftmaxFunction(Action.values().length);
        try {
            output = new OutputLayer(1, softmax, numDense);
            output.setNeuralNet(this);
            output.name = "output";
            softmax.setLayer(output);

        } catch (Exception ex) {
        }

        IActivationFunction iaf0 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_0));
        try {
            second = new HiddenLayer(numHidden, iaf0, numDense);
            second.setNeuralNet(this);
            second.nextLayer = output;
            second.name = "hidden";
        } catch (Exception ex) {
        }

        IActivationFunction iaf1 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_1));
        try {
            dense = new HiddenLayer(numDense, iaf1, numInputs);
            dense.setNeuralNet(this);
            dense.nextLayer = second;
            dense.name = "dense";
        } catch (Exception ex) {
        }


        IActivationFunction iaf2 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_2));
        try {
            input = new InputLayer(numInputs, ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_INPUT)), numInputs);
            input.setNeuralNet(this);
            input.name = "input";
            input.nextLayer = dense;
        } catch (Exception ex) {
        }

        try {
            input.previousLayer = null;
            dense.previousLayer = input;
            second.previousLayer = dense;
            output.previousLayer = second;

            input.init();
            dense.init();
            second.init();
            output.init();
        } catch (Exception ex) {
            System.out.println("Unable to build Neural Network:" + ex);
        }

    }




}

class ActionFactory {

    static Action create(double input) {
        int len = Action.class.getDeclaredFields().length;

        double o = (double) (input % len);

        int n = (int) Math.round(Math.abs(o));
        try {
            Field[] list = Action.class.getDeclaredFields();
            String name = list[n].getName();
            Action a = Action.valueOf(name);
            if (a.equals(Action.SIN)) {
                return create(Math.sin(input) * len);
            } else if (a.equals(Action.COS)) {
                return create(Math.cos(input) * len);
            } else if (a.equals(Action.TAN)) {
                return create(Math.tanh(input) * len);
            }

            return a;
        } catch (Exception e) {
            return Action.JUMP;
        }
    }

}

class Activity {
    int x, y;
    double vx, vy;
}

class GenomeFactory {
    public static String create(int numSequence) {

        if (numSequence <= Settings.GENOME_LENGTH) {
            numSequence = Settings.GENOME_LENGTH;
        }
        String code = "";
        for (int i = 0; i < numSequence; i++) {
            code += UUID.randomUUID().toString();
        }
        code = code.replaceAll("-", "");
        return code;
    }

    public static String create(int numSequence, char c) {
        char[] chars = new char[numSequence];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = c;
        }
        return String.valueOf(chars);
    }
}

class GARLRectangle extends Rectangle {
    boolean kill = true;
    boolean spawner = false;
    Color color = Color.pink;

    Color getColor() {
        if (spawner) {
            return Color.green;
        }
        return color;
    }

}

class Genome {
    static String DEAD = GenomeFactory.create(Settings.GENOME_LENGTH * Settings.GENOME_LENGTH, '0');
    Entity owner = null;
    String code = null;

    Genome(Entity owner) {
        code = GenomeFactory.create(Settings.GENOME_LENGTH);
        this.owner = owner;
    }


    public synchronized char read(int loc) {
        if (loc < code.length()) {
            return code.charAt(loc);
        }
        else if( loc >= code.length()){
            try {
                int more = code.length() - loc;
                return read(more);
            } catch(Exception ex) {}
        }
        char c = code.charAt(Settings.GENOME_LENGTH+(int)Math.random()%2);
        //advance();
        return c;
    }

    public void jump(int loc) {
        if (loc < code.length()) {
            index += loc;
        } else {
            index = Settings.GENOME_LENGTH;
        }
    }

    int index = 0;

    public void advance() {
        index++;
        if (index >= code.length()) {
            index = Settings.GENOME_LENGTH;
        }
    }

    public int index() {
        return index;
    }

    public synchronized void mutate() {
        Random rand = new Random();
        char[] c = code.toCharArray();
        int index = c.length - 1;
        int mutations = (int) (c[Gene.GENE_MUTATION_PROBABILITY] * rand.nextDouble() * c[Gene.GENE_MUTATION_MULTIPLIER]);
        for (int j = 0; j < mutations; j++) {
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
        String time = "" + Long.toHexString(System.currentTimeMillis());
        time = reverse(time);

        c[Gene.KIN] = time.charAt(0);
        code = String.valueOf(c);
    }

    public static String reverse(String in) {
        char[] c = in.toCharArray();
        String o = "";
        for (int i = in.length() - 1; i > 0; i--) {
            o += c[i];
        }
        return o;
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
            //return true;
        } else if (intersects && !shareAnyPoint) {
            //System.out.println("Lines intersect.");
            return true;
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

class Utility {
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

    public static double flatten(double v, double max) {
        if( v > 1 ) {
            v = Math.abs(Math.cos(v));
            return v;
        } else if( v == 0 ){
            v = Math.abs(Math.sin(v));
            return v;
        }
        return 0.5;
    }

}

class Brain {
    Entity entity = null;
    Genome genome = null;
    public Brain(Entity owner, Genome genome){
        ann = new NeuralNet(genome);
        this.genome = genome;
        this.entity = owner;
    }
    NeuralNet ann = null;

    public static void main(String args[])
    {
        World world = new World(1848,1016);
        Entity e = new Entity(world);
        e.age = 100;
        Action a = e.brain.evaluate(world);
        e.brain.ann.input.calc();
        double d = e.brain.getOutput();

    }
    public void input(Entity e, World world) {
        ArrayList<Double> list = new ArrayList<>();
        if (e == null) {
            System.out.println("Genome is null");
        }
        list.add((double) world.getWidth());
        list.add((double) world.getHeight());
        list.add((double) Globals.spawn.x);
        list.add((double) Globals.spawn.y);
        list.add((double) Globals.spawn.width);
        list.add((double) Globals.spawn.height);
        list.add((double) (0d));
        for(int i =0; i < world.selection.rlist.size(); i++ ){
            list.add((double) world.selection.rlist.get(i).x);
            list.add((double) world.selection.rlist.get(i).y);
            list.add((double) world.selection.rlist.get(i).width);
            list.add((double) world.selection.rlist.get(i).height);
            list.add((double) (world.selection.rlist.get(i).kill?1d:0d));
        }

        for(int i =0; i < world.list.size(); i++ ){
            list.add((double) world.list.get(i).location.x);
            list.add((double) world.list.get(i).location.y);
            list.add((double) world.list.get(i).location.vx);
            list.add((double) world.list.get(i).location.vy);
            list.add((double) e.age);
            list.add((double)(e.fertile?1d:0d));
            list.add((double) world.list.get(i).size);
            list.add((double) (world.list.get(i).alive?1d:0d));
            list.add((double) world.list.get(i).getEnergy());
            list.add((double) (world.list.get(i).genome.read(Gene.KIN)));
        }

        list.add((double) e.location.x);
        list.add((double) e.location.y);
        list.add( e.location.vy);
        list.add( e.location.vx);
        list.add((double) e.age);
        list.add((double)(e.fertile?1d:0d));
        list.add((double) e.size);
        list.add( e.getEnergy());
        list.add((double) e.genome.read(Gene.KIN));
        list.add((double) e.genome.index);
        list.add((double) e.genome.read(e.genome.index));

        long mlcs = Utility.checksum(e.genome.code);
        double mld = Utility.flatten(mlcs, 1);
        list.add(mld);
        list.add((double)e.sampleForward().size());

        try {
            e.brain.ann.input.input = list;
        } catch(Exception ex){}
    }

    public double getOutput()
    {
        return getOutput(entity);
    }


    public double getOutput(Entity e)
    {
        NeuralLayer d = e.brain.ann.output;
        ArrayList<Neuron> n = d.neuron;
        Neuron nn = d.neuron.get(0);
        double r = 0;
        if( nn == null ){
            System.out.println("nn is null");
        } else {
            r = nn.getOutput();
        }
        return r;
    }

    Action last = null;

    public Action evaluate(World world) {

        if( entity != null ) {
            input(entity, world);
            try {
                entity.brain.ann.input.calc();
            } catch(Exception ex) {
                return null;
            }
            double d = entity.brain.getOutput();
            Action a = ActionFactory.create(entity.genome.read(entity.genome.index+(int)d));
            entity.genome.advance();
            if( entity.last != a ) {
                entity.last = a;
                return a;
            } else {
                input(entity, world);
                ArrayList<Entity> ls = entity.sampleForward();
                for(int i = 0; i < ls.size(); i++ ){
                   input(ls.get(i), world);
                }
                entity.brain.ann.input.calc();
                double dd = entity.brain.getOutput();
                dd = Utility.flatten(dd, (double)Action.values().length);
                a = ActionFactory.create(entity.genome.read(entity.genome.index+(int)dd));
                entity.genome.advance();
                entity.last = a;
                return a;
            }
        } else {
            entity.genome.advance();
            entity.last = Action.IF;
            return Action.IF;
        }
    }

    public Action evaluate(Entity entity, World world) {

        if( entity != null ) {
            input(entity, world);
            try {
                entity.brain.ann.input.calc();
            } catch(Exception ex) {
                return null;
            }
            double d = entity.brain.getOutput();
            Action a = ActionFactory.create(entity.genome.read(entity.genome.index+(int)d));
            entity.genome.advance();
            if( entity.last != a ) {
                entity.last = a;
                return a;
            } else {
                input(entity, world);
                ArrayList<Entity> ls = entity.sampleForward();
                for(int i = 0; i < ls.size(); i++ ){
                    input(ls.get(i), world);
                }
                entity.brain.ann.input.calc();
                double dd = entity.brain.getOutput();
                dd = Utility.flatten(dd, (double)Action.values().length);
                a = ActionFactory.create(entity.genome.read(entity.genome.index+(int)dd));
                entity.genome.advance();
                entity.last = a;
                return a;
            }
        } else {
            entity.genome.advance();
            entity.last = Action.IF;
            return Action.IF;
        }
    }
}

class Entity {
    Brain brain = null;
    Activity location = new Activity();
    Genome genome = null;
    Entity parent = null;
    int generation = 0;
    boolean fertile = false;
    private double energy = 1;
    int size = 1;
    double degree = 0; // must be 0 - 360 to specify the direction the entity is facing.
    boolean selected = false;
    Action last = null;
    Entity touching = null;

    int age = 0;

    Color color = Color.blue;
    World world = null;
    boolean alive = true;

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
        brain = null;
        touching = null;
    }

    public boolean intersects(Entity a, Entity b) {

        if (!a.alive || !b.alive) {
            return false;
        }

        int a_startX = a.location.x;
        int a_startY = a.location.y;

        int b_startX = b.location.x;
        int b_startY = b.location.y;

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


    Entity(World world) {
        this.world = world;
        genome = new Genome(this);
        brain = new Brain(this, genome);
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

        genome.mutate();
        e.genome.code = genome.code;
        e.genome.mutate();
        e.brain = new Brain(e, e.genome);

        e.age = 0;
        e.energy = 1;
        e.degree = Math.random() * 360;
        e.parent = this;
        this.parent = null;
        e.generation = generation + 1;
        e.fertile = false;

        return e;
    }

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
        Action action = brain.evaluate(world);

        if( action == null ){
            return null;
        }

        int depth = 0;
        process(action, world, depth);
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


    double cycle = 0;
    public void process(Action action, World world, int depth) {

        depth++;
        if( depth > Settings.MAX_THINK_DEPTH){
            System.out.println("Hit Max Depth");
            return;
        }

        switch (action) {
            case STOP:
                // if we're stopped - and we're touching someone, lets move.
                if (isTouching()) {
                    doKill(Action.KILL);
                } else {
                    location.vx = 0;
                    location.vy = 0;
                }
                break;

            case JUMP:

                try {
                    brain.input(this, world);
                    double jf = brain.getOutput(); // TODO: replace with jump factor.
                    double jmo = genome.index + Math.abs(jf-1);
                    genome.jump((int) jmo);
                } catch (Exception ex) {
                }
                break;
            case IF:
                if (genome.read(genome.index) == (char) Gene.DECISION) {
                    genome.advance();
                    process(Action.JUMP, world, depth);
                } else {
                    genome.advance();
                }
                break;
            case SCAN:

                ArrayList<Entity> e = sampleForward();

                double o = 0;
                for(int i =0; i < e.size(); i++) {
                    Entity ent = e.get(0);
                    brain.input(ent, world);
                    brain.ann.input.calc();
                    o += brain.getOutput();
                }

                {
                    double v = 0;
                    if( Math.random() > 0.5 ) {
                        v = Utility.flatten(o, 180d);
                    } else {
                        v = Utility.flatten(cycle, 180d);
                        cycle++;
                    }
                    double radiansToDegrees = 180d / Math.PI;
                    degree = v * radiansToDegrees;
                    degree = (degree + 360) % 360;
                }
                return;

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
                return;
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

                        if( o.genome.read(Gene.KIN) == genome.read(Gene.KIN)){
                            o.fertile = true;
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
                        o.setEnergy(o.getEnergy() - extracted);
                        if (o.getEnergy() <= 0 || o.size <= 0) {
                            o.die();
                            world.list.remove(o);
                        } else if (getEnergy() <= 0 || size <= 0) {
                            die();
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

class Gene {
    final static int SENSORY = 0;
    final static int GENE_MUTATION_PROBABILITY = 1;
    final static int RR = 2;
    final static int DENSE = 3;
    final static int HIDDEN = 4;
    final static int KIN = 5;
    final static int GENE_MUTATION_MULTIPLIER = 6;
    final static int SIZE = 9;
    final static int KILL = 10;
    final static int ATTACK = 11;
    final static int DEFENSE = 12;
    final static int MATURITY = 21;
    final static int ACTIVATION_FUNCTION_0 = 22;
    final static int ACTIVATION_FUNCTION_1 = 24;
    final static int ACTIVATION_FUNCTION_2 = 25;
    final static int AGE = 26;
    final static int DECISION = 27;
    final static int ACTIVATION_FUNCTION_INPUT = 28;

}

class World extends JLabel {
    ArrayList<Entity> list = new ArrayList<>();

    Selection selection = null;

    int children = 0;
    int width;
    int height;
    int bestSpawn = 0;
    static ArrayList<Entity> bestSeeds = new ArrayList<>();
    static ArrayList<Entity> prospectSeeds = new ArrayList<>();
    int step = 0;
    double phl = 0;
    double increment = 0.0010000;
    int mx = 0;
    int my = 0;
    Entity selected = null;

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

    private static void drawVisibilityCircle(Graphics2D g2d, Color kin, Point center, float r, Color c) {
        float radius = r;
        float[] dist = {0f, 1f};
        Color[] colors = {new Color(0, 0, 0, 0), c};
        Color[] kins = {new Color(0, 0, 0, 0), kin};
        //workaround to prevent background color from showing
        drawBackGroundCircle(g2d, radius, Color.WHITE, center);
        drawGradientCircle(g2d, radius, dist, colors, center);
        drawGradientCircle(g2d, 2, dist, kins, center);

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


    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        DecimalFormat df = new DecimalFormat("0.00000000");

        step++;
        phl += increment;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        ArrayList<GARLRectangle> rlist = selection.rlist;

        for (int j = 0; j < rlist.size(); j++) {
            GARLRectangle rect = rlist.get(j);
            g2.setColor(rect.getColor());
            if (rect != null) {
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
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
                e.color = Color.BLUE;
                g2.setColor(Color.BLUE);
            }
            Point p = new Point(e.location.x + r / 2, e.location.y + r / 2);
            if (r > 0) {
                int k = e.genome.read(Gene.KIN);
                Color kin = new Color(k, k, k);
                drawVisibilityCircle(g2, kin, p, r, e.color);
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

        }


        drawPopup(g2, selected, mx, my);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, getHeight() - 24, getWidth(), getHeight());
        g2.setColor(Color.YELLOW);
        g2.drawString("Think:" + step + " population:" + livingCount + " killed:" + (Settings.STARTING_POPULATION - livingCount) + " " + df.format(phl) + " PHL " + getWidth() + " x " + getHeight() + " epoch:" + epoch + " children:" + children + " impact death:" + impact + " spawns:" + spawns + " best seed:" + bestSpawn, 10, (getHeight() - 10));

    }

    int spawns = 0;
    int impact = 0;
    int epoch = 1;

    public void drawPopup(Graphics g, Entity e, int mx, int my) {

        int spacing = 14;
        int popupWidth = 340;
        int popupHeight = 400;

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
            g.drawString("Sample Forward: " + e.sampleForward().size(), mx + spacing, my + spacing * 25);


        }

    }

}

class Population {

    public static ArrayList<Entity> create(World world, int individuals, int width, int height) {
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        for (int i = 0; i < individuals; i++) {
            Entity e = new Entity(world);
            e.location.x = rand.nextInt(width);
            e.location.y = rand.nextInt(height);
            entities.add(e);
        }

        return entities;
    }

    public static ArrayList<Entity> create(World world, ArrayList seedList, int individuals, int width, int height) {
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        world.children = 0;

        Comparator<Entity> comparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                if( e1.generation == e2.generation) {
                    return 0;
                } else if( e1.generation < e2.generation ){
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        try {
            Collections.sort(seedList, comparator);
        } catch(Exception ex) {ex.printStackTrace();}

        int adjustedIndividuals = individuals - seedList.size();
        for (int i = 0; i < individuals; i++) {
            try {
                Entity seed = (Entity) seedList.get(i);
                Entity e = seed.replicate();
                e.location.x = rand.nextInt(width);
                e.location.y = rand.nextInt(height);
                entities.add(e);
                if( Globals.verbose ) {
                    System.out.println("{ \"epoch\":" + world.epoch + ", \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                }
            } catch (Exception ex) {
                break;
            }
        }

        if( adjustedIndividuals > 0 ) {
            for (int i = 0; i < adjustedIndividuals; i++) {
                try {
                    Entity e = new Entity(world);
                    e.location.x = rand.nextInt(width);
                    e.location.y = rand.nextInt(height);
                    entities.add(e);
                    if (Globals.verbose) {
                        System.out.println("{ \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                    }
                } catch (Exception ex) {
                }
            }
        }

        return entities;
    }
}

class Settings {
    static int GENOME_LENGTH = 32;
    static int STARTING_POPULATION = 200;
    static int MAX_OFFSPRING = 6;
    //final static int MAX_AGE = 10;
    final static int MAX_THINK_DEPTH = 4;
    final static int NUMBER_OF_INPUTS = 1;
    final static int DEATH_MULTIPLIER = 25;
    final static int GENE_POOL = 8;
    final static int MAX_SIZE = 18;
    final static int MAX_NEURONS = 5;

    //final static int SELECTION_EVENT = 1;
    //final static double MUTATION_RATE = 0.2;
    final static int CELL_MOVEMENT = 1;
    final static int MAX_SPEED = 4;
    final static int MAX_POPULATION = 250;

    final static double ENERGY_STEP_COST = 0.001;
    final static double ENERGY_STEP_SLEEP_COST = 0.00001;


}

class Selection {

    ArrayList<GARLRectangle> rlist = new ArrayList<>();
    Selection() {

        makeNewList();

    }

    public void makeNewList()
    {
        GARLRectangle[] list = new GARLRectangle[15];
        rlist = new ArrayList<>();

        list[0] = new GARLRectangle();
        list[0].x = 1820;
        list[0].y = 0;
        list[0].width = 20;
        list[0].height = 1016;

        list[1] = new GARLRectangle();
        list[1].x = 100;
        list[1].y = 100;
        list[1].width = 400;
        list[1].height = 20;

        list[2] = new GARLRectangle();
        list[2].x = 0;
        list[2].y = -10;
        list[2].width = 3000;
        list[2].height = 30;

        list[3] = new GARLRectangle();
        list[3].x = 0;
        list[3].y = 930;
        list[3].width = 1800;
        list[3].height = 20;

        list[4] = new GARLRectangle();
        list[4].x = (int)(200*Math.random());
        list[4].y = (int)(200*Math.random());
        list[4].width = 20;
        list[4].height = 400;

        list[5] = new GARLRectangle();
        list[5].x = (int)(500*Math.random());
        list[5].y = (int)(500*Math.random());
        list[5].width = 20;
        list[5].height = 400;


        list[6] = new GARLRectangle();
        list[6].x = (int)(200*Math.random());
        list[6].y = (int)(500*Math.random());
        list[6].width = 20;
        list[6].height = 430;

        list[7] = new GARLRectangle();
        list[7].x = 0;
        list[7].y = 300;
        list[7].width = 20;
        list[7].height = 630;

        list[8] = new GARLRectangle();
        list[8].x = (int)(700*Math.random());
        list[8].y = (int)(200*Math.random());
        list[8].width = 20;
        list[8].height = 730;


        list[9] = new GARLRectangle();
        list[9].x = (int)(990*Math.random());
        list[9].y = 0;
        list[9].width = 20;
        list[9].height = 700;


        list[10] = new GARLRectangle();
        list[10].x = (int)(1440*Math.random());
        list[10].y = 500;
        list[10].width = 20;
        list[10].height = 430;


        list[11] = new GARLRectangle();
        list[11].x = 1400;
        list[11].y = 500;
        list[11].width = 100;
        list[11].height = 20;


        list[12] = new GARLRectangle();
        list[12].x = 0;
        list[12].y = 1000;
        list[12].width = 1800;
        list[12].height = 20;

        list[13] = new GARLRectangle();
        list[13].x = 1400;
        list[13].y = 100;
        list[13].width = 100;
        list[13].height = 300;

        list[14] = new GARLRectangle();
        list[14].x = (int)(1200*Math.random());
        list[14].y = (int)(500*Math.random());
        list[14].width = 100;
        list[14].height = 100;
        list[14].spawner = true;
        list[14].kill = false;

        Globals.spawn = list[14];

        for(int i =0; i < list.length; i++ ){
            rlist.add(list[i]);
        }
    }


    public boolean insideRect(GARLRectangle rect, int x, int y) {
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

class SelectionTask extends TimerTask {

    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;

    public SelectionTask(JFrame frame, World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
    }

    @Override
    public void run() {
        // Perform Selection.
        Selection selection = world.selection;

        ArrayList<GARLRectangle> rlist = world.selection.rlist;
        int livingCount = 0;
        for (int i = 0; i < world.list.size(); i++) {
            Entity e = world.list.get(i);
            if (e.location.y == 0) {
                e.die();
            } else if (e.location.x == 0) {
                e.die();
            }
            Random rand = new Random();
            for (int j = 0; j < rlist.size(); j++) {
                GARLRectangle rect = rlist.get(j);
                if (rect != null) {
                    if (e.alive) {
                        if (selection.insideRect(rect, e.location.x, e.location.y)) {
                            if (rect.spawner) {
                                Globals.spawn = rect;
                                for (int k = 0; k < 5; k++) {
                                    Entity n = e.clone();
                                    n.location.x = rand.nextInt(frame.getWidth());
                                    n.location.y = rand.nextInt(frame.getHeight());
                                    n.alive = true;
                                    world.list.add(n);
                                    world.spawns++;
                                    world.prospectSeeds.add(e.clone());
                                    if (world.spawns >= world.bestSpawn) {
                                        world.bestSeeds.add(e.clone());
                                        world.bestSpawn = world.spawns;
                                    }
                                }
                                e.die();
                            } else if (rect.kill) {
                                world.impact++;
                                e.die();
                            }
                        }
                    }
                }
            }

            if (e.getEnergy() <= 0) {
                e.die();
            }
            else if (e.age >= Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE)) {
                e.die();
            }

        }
    }
}

class ReplicationTask extends TimerTask {

    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;

    public ReplicationTask(JFrame frame, World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
    }

    @Override
    public void run() {

        int livingCount = 0;
        for (int i = 0; i < world.list.size(); i++) {

            Entity e = world.list.get(i);

            if (e.fertile && e.alive) {
                int min = Math.max(32, e.genome.read(Gene.MATURITY));
                if (e.alive && (e.age > min)) {
                    if (Math.random() > 0.8) {
                        int n = e.genome.read(Gene.RR) % Settings.MAX_OFFSPRING;
                        if (livingCount > Settings.MAX_POPULATION) {
                            n = Math.min(1, n);
                        } else {
                            for (int j = 0; j <= n; j++) {

                                Entity a = e.replicate();
                                world.list.add(a);
                                world.prospectSeeds.add(a);
                                world.children++;
                                world.spawns++;
                                if (world.spawns > world.bestSpawn) {
                                    world.bestSpawn = world.spawns;
                                }
                            }
                        }
                        e.die();

                    }
                }
            }
        }
    }
}
class ThinkTask extends TimerTask {
    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;

    public ThinkTask(JFrame frame, World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
    }

    int ctr = 0;
    @Override
    public void run() {


        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        ctr++;
                        if(ctr > 10)

                        {
                            //System.out.println("THINK");
                            //System.out.println("Size:" + world.list.size() + " " + world.prospectSeeds.size() + " " + world.bestSeeds.size());
                            ctr = 0;
                        }

                        int livingCount = 0;
                        for (int i = 0; i < world.list.size(); i++) {
                            Entity e = world.list.get(i);
                            if (e.alive) {
                                livingCount++;
                                e.think(world);
                            }
                        }

                        if (livingCount <= Settings.GENE_POOL || livingCount >= Settings.MAX_POPULATION) {
                            //System.out.println("Recreate population");
                            ArrayList<Entity> seedList = new ArrayList<>();
                            for (int i = 0; i < world.list.size(); i++) {
                                Entity a = world.list.get(i);
                                if (a.alive) {
                                    seedList.add(a);
                                }
                            }

                            for (int j = 0; j < world.bestSeeds.size(); j++) {
                                Entity a = world.bestSeeds.get(j);
                                seedList.add(a);
                                seedList.add(new Entity(world));
                            }
                            for (int j = 0; j < world.prospectSeeds.size(); j++) {
                                Entity a = world.prospectSeeds.get(j);
                                seedList.add(world.prospectSeeds.get(j));
                                seedList.add(new Entity(world));
                            }

                            for (int j = 0; j < seedList.size(); j++) {
                                Entity ee = (Entity) seedList.get(j);
                                if (ee.genome.code.endsWith("000000000")) {
                                    seedList.remove(ee);
                                }
                            }

                            world.children = 0;
                            world.impact = 0;
                            world.spawns = 0;
                            world.epoch++;
                            if (!seedList.isEmpty()) {
                                world.list = Population.create(world, seedList, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                                world.selection.makeNewList();
                            } else if (world.list.size() == 0) {
                                world.list = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                                world.selection.makeNewList();
                            }
                            world.prospectSeeds = new ArrayList<>();

                        }
                    }

                    ;


                });

    }
}

public class GARLTask extends Thread {

    String seed = null;
    final static int FPS = 32;

    public GARLTask(String seed) {
        if (seed != null) {
            this.seed = seed;
        }
    }


    public static void main(String[] args) {
        String seed = null;

        if (args.length > 0) {
            seed = args[0];
        }

        GARLTask task = new GARLTask(seed);
        task.start();
    }

    Selection selection = new Selection();
    JFrame frame = new JFrame("Genetic Based Multi-Agent Reinforcement Learning");

    World world = null;

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
        frame.add(world);
        ;//, BorderLayout.CENTER);
        //4. Size the frame.

        //5. Show it.
        frame.setVisible(true);

        ThinkTask think = new ThinkTask(frame, world, width, height);
        SelectionTask selection = new SelectionTask(frame, world, width, height);
        ReplicationTask replication = new ReplicationTask(frame, world, width, height);

        Timer timer = new Timer();
        TimerTask paint = new TimerTask() {
            int ctr = 0;
            @Override
            public void run() {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ctr++;
                            if(ctr >100)
                            {
                                //System.out.println("FPS");
                                ctr = 0;
                            }
                            world.repaint();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
            }
        };

        timer.scheduleAtFixedRate(paint, 0, 1000 / FPS);
        timer.scheduleAtFixedRate(think, 0, 250);
        timer.scheduleAtFixedRate(selection, 0, 250);
        timer.scheduleAtFixedRate(replication, 100, 250);

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
