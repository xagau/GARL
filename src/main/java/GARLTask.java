import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

enum Action {
    NONE,
    COMMIT,
    MOVE_RIGHT,
    MOVE_LEFT,
    MOVE_UP,
    MOVE_DOWN,
    MOVE_UP_RIGHT,
    MOVE_UP_LEFT,
    MOVE_DOWN_LEFT,
    MOVE_DOWN_RIGHT,
    SCAN,
    CYCLE,
    TARGET,
    IF,
    RECODE,
    JUMP,
    COS,
    SIN,
    TAN,
    SLOW,
    FASTER,
    STOP,
    KILL,
    DIRECTION,
    CONTINUE,
    APPEND,
    SAVE,
    GOAL,
    DEATH,
    DELETE
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
            if (name.equals("output")) {
                SoftmaxFunction sf = new SoftmaxFunction(Action.values().length);
                sf.setLayer(this);
                activationFnc = sf;

            } else {
                activationFnc = new ReluFunction(owner.owner.read(owner.owner.index));
            }
            owner.owner.advance();
            double bias = owner.owner.read(owner.owner.index());
            try {
                neuron.get(i).setActivationFunction(activationFnc);
                neuron.get(i).init();
            } catch (IndexOutOfBoundsException iobe) {
                neuron.add(new Neuron(numberOfInputs, activationFnc, bias));
                neuron.get(i).init();
            }

        }
    }

    boolean neuralLayerDebug = false;

    protected void calc() {
        if (neuralLayerDebug) {
            System.out.println(this.name + " calc() " + numberOfNeuronsInLayer);
        }
        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            if (previousLayer != null) {
                if (neuralLayerDebug) {
                    System.out.println("input-" + name + ":" + previousLayer.input);
                }
                neuron.get(i).setInputs(previousLayer.input);
                neuron.get(i).calc();
            } else {
                if (neuralLayerDebug) {
                    System.out.println("input-" + name + ":" + input);
                }
                neuron.get(i).setInputs(input);
                neuron.get(i).calc();
            }
            try {
                output.set(i, neuron.get(i).getOutput());
            } catch (IndexOutOfBoundsException iobe) {
                output.add(neuron.get(i).getOutput());
            }
        }
        if (nextLayer != null) {
            if (neuralLayerDebug) {
                System.out.println("Compute next Layer:");
            }
            nextLayer.input = output;
            nextLayer.calc();
        }
    }

    public void setNeuralNet(NeuralNet net) {
        owner = net;
    }

    public NeuralNet getNeuralNet() {
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

    double gene = 0;

    public SinusoidFunction(double gene) {
        this.gene = gene;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calc(double summedInput) {
        return Math.sin(summedInput + gene);
    }


    public void setLayer(NeuralLayer layer) {
    }

    public double derivative(double net) {
        return Math.cos(net + gene);
    }

}


class CosusoidFunction implements IActivationFunction {
    double gene = 0;

    public CosusoidFunction(double gene) {
        this.gene = gene;
    }

    public void setLayer(NeuralLayer layer) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calc(double summedInput) {
        return Math.cos(summedInput + gene);
    }

    public double derivative(double net) {
        return Math.cos(net + gene);
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
    private double bias = 0;

    LinearCombinationFunction(double bias) {
        this.bias = bias;
    }

    public void setLayer(NeuralLayer layer) {
        setLayer(layer);
    }

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

    public void setLayer(NeuralLayer layer) {

    }

    public double calc(double summedInput) {
        if (summedInput >= threshold) {
            return yAbove;
        } else {
            return yBellow;
        }
    }


    public double derivative(double input) {
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

    public void setLayer(NeuralLayer layer) {

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

    public void setLayer(NeuralLayer layer) {

    }

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

    public void setLayer(NeuralLayer layer) {

    }

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


    public void setLayer(NeuralLayer layer) {
    }


    @Override
    public double calc(double x) {
        if (x >= a) {
            return Math.tanh(x);
        }
        return x;
    }
}


class NegateFunction implements IActivationFunction {
    private double a = 0.0;

    public NegateFunction(double _a) {
        this.a = _a;
    }


    public void setLayer(NeuralLayer layer) {
    }

    @Override
    public double calc(double x) {
        if (x < a) {
            x = -x;
            return x;
        }
        return -x;
    }
}

class SoftmaxFunction implements IActivationFunction {

    private double max = 1.0;

    public SoftmaxFunction(double max) {
        this.max = max;
    }

    NeuralLayer layer = null;

    public void setLayer(NeuralLayer layer) {
        this.layer = layer;
    }

    @Override
    public double calc(double netInput) {
        double totalLayerInput = 0;
        // add max here for numerical stability - find max netInput for all neurons in this layer
        double max = netInput;

        if (layer == null) {
            System.out.println("Layer is null:");
            return netInput;
        }
        ArrayList<Neuron> list = layer.neuron;
        for (int i = 0; i < list.size(); i++) {
            Neuron neuron = (Neuron) list.get(i);
            totalLayerInput += Math.exp(neuron.getOutput() - max);
        }

        double output = Math.exp(netInput - max) / totalLayerInput;
        return output;
    }

}

enum ActivationFunction {
    //LINEAR,
    SIGMOID,
    //HYPERTAN,
    RELU,
    SOFTMAX,
    //COSUSMOID,
    //SINUSMOID,
    //STEP,
    //NEGATE,
    //LINEAR_COMBINATION
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
    protected Double bias = 0d; // get from gene? OR TD(0)?
    private IActivationFunction activationFunction;

    public Neuron(int numberofinputs, IActivationFunction iaf, Double bias) {
        this.bias = bias;
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
                    double v = 0;
                    try {
                        v = (i == numberOfInputs ? bias : input.get(i));
                    } catch (Exception ex) {
                        v = bias;
                    }
                    outputBeforeActivation += v * weight.get(i);
                }
            }
        }
        output = activationFunction.calc(outputBeforeActivation);
    }

    public void setInputs(ArrayList<Double> input) {
        this.input = new ArrayList<Double>();
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
                Sigmoid sigmoid = new Sigmoid(gene);
                return sigmoid;
            //case LINEAR:
            //    Linear linear = new Linear(gene);
            //    return linear;
            //case STEP:
            //    StepFunction st = new StepFunction((double) gene);
            //    return st;
            //case HYPERTAN:
            //    HTANFunction ht = new HTANFunction((double) gene);
            //    return ht;
            case RELU:
                ReluFunction rl = new ReluFunction((double) gene);
                return rl;
            case SOFTMAX:
                SoftmaxFunction smx = new SoftmaxFunction((double) gene);
                return smx;
            //case NEGATE:
            //    NegateFunction nl = new NegateFunction((double) gene);
            //    return nl;
            //case LINEAR_COMBINATION:
            //    LinearCombinationFunction lcb = new LinearCombinationFunction((double) gene);
            //    return lcb;
            //case SINUSMOID:
            //    SinusoidFunction sinusmoid = new SinusoidFunction((double) gene);
            //    return sinusmoid;
            //case COSUSMOID:
            //    CosusoidFunction cosusmoid = new CosusoidFunction((double) gene);
            //    return cosusmoid;
            default:
                ReluFunction smax = new ReluFunction((double) gene);
                return smax;
        }
    }
}

class Globals {
    static GARLRectangle spawn = new GARLRectangle();
    static GARLRectangle control = new GARLRectangle();

    static boolean verbose = true;
    static long threshold = 250;
    static Semaphore semaphore = new Semaphore(1);

    static boolean debug = false;

}

class NeuralNet {
    InputLayer input = null;
    HiddenLayer dense = null;
    HiddenLayer hidden = null;
    HiddenLayer dropout = null;
    OutputLayer output = null;
    Genome owner = null;

    public NeuralNet(Genome g) {
        owner = g;
        int numInputs = Settings.NUMBER_OF_INPUTS;
        int numDense = Math.min(Settings.MAX_NEURONS, Math.max(2, (int) g.read(Gene.DENSE)) );
        int numHidden = Math.min(Settings.MAX_NEURONS, Math.max(2,(int) g.read(Gene.HIDDEN)) );
        int numDropout = 2;

        SoftmaxFunction softmax = new SoftmaxFunction(Action.values().length);
        try {
            output = new OutputLayer(1, softmax, numHidden);
            output.setNeuralNet(this);
            output.name = "output";
            softmax.setLayer(output);

        } catch (Exception ex) {
        }

        try {
            ReluFunction relu = new ReluFunction(g.read(Gene.ACTIVATION_FUNCTION_1));
            dropout = new HiddenLayer(numDropout, relu, numHidden);
            dropout.setNeuralNet(this);
            dropout.nextLayer = output;
            dropout.name = "dropout";
        } catch (Exception ex) {
        }

        try {
            ReluFunction relu = new ReluFunction(g.read(Gene.ACTIVATION_FUNCTION_0));
            hidden = new HiddenLayer(numHidden, relu, numDense);
            hidden.setNeuralNet(this);
            hidden.nextLayer = dropout;
            hidden.name = "hidden";
        } catch (Exception ex) {
        }

        try {
            ReluFunction relu = new ReluFunction(g.read(Gene.ACTIVATION_FUNCTION_1));
            dense = new HiddenLayer(numHidden, relu, numInputs);
            dense.setNeuralNet(this);
            dense.nextLayer = hidden;
            dense.name = "dense";
        } catch (Exception ex) {
        }


        IActivationFunction iaf2 = ActivationFactory.create(g.read(Gene.ACTIVATION_FUNCTION_2));
        try {
            ReluFunction relu = new ReluFunction(g.read(Gene.ACTIVATION_FUNCTION_INPUT));
            input = new InputLayer(numInputs, relu, numInputs);
            input.setNeuralNet(this);
            input.name = "input";
            input.nextLayer = dense;
        } catch (Exception ex) {
        }

        try {
            input.previousLayer = null;
            dense.previousLayer = input;
            hidden.previousLayer = dense;
            dropout.previousLayer = hidden;
            output.previousLayer = dropout;

            input.init();
            dense.init();
            hidden.init();
            dropout.init();
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

            return a;
        } catch (Exception e) {
            return Action.COMMIT;
        }
    }

}

class Coords {
    double x = 0, y = 0;
    double vx = 0.0, vy = 0.0;
}

class GenomeFactory {
    public static String create(int numSequence) {

        if (numSequence <= Settings.GENOME_LENGTH) {
            numSequence = Settings.GENOME_LENGTH;
        }
        String code = "";
        String str = "";
        for (int i = 0; i <= Settings.GENOME_LENGTH; i++) {
            Random r = new Random();
            char c = (char) (r.nextInt(Settings.CHAR_SET) + 'a');
            str += c;
        }
        for (int i = 0; i < numSequence; i++) {
            code += str;
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
    boolean control = false;
    Color color = Color.pink;
    String name = "wall";

    public String getName() {
        if( spawner ){
            return "spawner";
        } else if( control ){
            return "control";
        } else {
            return name;
        }
    }

    Color getColor() {
        if (spawner) {
            return Color.green;
        } else if (control) {
            return Color.magenta;
        }
        return color;
    }

}

class Genome {
    static String DEAD = GenomeFactory.create(Settings.GENOME_LENGTH * Settings.GENOME_LENGTH, '-');
    Entity owner = null;
    String code = null;
    int numAppends = 0;
    int numDeletions = 0;
    int numRecodes = 0;

    public Genome(Entity owner) {
        code = GenomeFactory.create(Settings.GENOME_LENGTH);
        this.owner = owner;
    }

    public Genome(String code) {
        this.code = code;
    }

    public void setOwner(Entity e) {
        this.owner = e;
    }

    char last = 0;

    public synchronized char last() {
        return last;
    }

    public synchronized char read(int loc) {
        if (loc < 0) {
            loc = Settings.GENOME_LENGTH + (int) read(Settings.GENOME_LENGTH + Gene.DECISION);
        }
        if (loc < code.length()) {
            last = code.charAt(loc);
            return last;
        } else if (loc >= code.length()) {
            try {
                int more = code.length() - loc;
                return read(more);
            } catch (Exception ex) {
            }
        }
        char c = code.charAt(Settings.GENOME_LENGTH + (int) read(Settings.GENOME_LENGTH + Gene.DECISION));
        last = c;
        return c;
    }

    public void recode(int loc, char c) {

        if (loc + Settings.GENOME_LENGTH > code.length()) {
            return;
        }
        char[] g = code.toCharArray();
        g[Settings.GENOME_LENGTH + loc] = c; //Utility.flatten(c, 26);
        code = String.valueOf(g);
        numRecodes++;
    }

    public void jump(int loc) {
        if (index + loc < code.length()) {
            index += loc;
        }
    }

    int index = 0;

    public void advance() {
        index++;
        if (index >= code.length()) {
            index = Settings.GENOME_LENGTH + 1;
        }
        if (index < Settings.GENOME_LENGTH) {
            index = Settings.GENOME_LENGTH + 1;
        }
    }

    public void reverse() {
        index--;
        if (index <= Settings.GENOME_LENGTH) {
            index = Settings.GENOME_LENGTH + 1;
        }
    }


    public int index() {
        advance();
        return index;
    }

    public synchronized void mutate() {
        Random rand = new Random();
        char[] c = code.toCharArray();
        int index = c.length - 1;
        int mutations = (int) Math.min((c[Gene.GENE_MUTATION_PROBABILITY] * 0.005 * c[Gene.GENE_MUTATION_MULTIPLIER]), Settings.GENOME_LENGTH / 2);
        for (int j = 0; j <= mutations; j++) {
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

        c[Gene.KIN] = KinFactory.create(time.charAt(0));
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

    public static char flatten(int c, int max) {
        c = c % max;
        char ch = Long.toHexString((long) c).charAt(0);
        return ch;
    }

    public static void main(String[] args) {
        char c = flatten('*', 16);
    }

    public static double flatten(double v, double max) {
        v = v % max;
        return v;
    }

}

class Brain {
    Entity entity = null;
    Genome genome = null;

    public Brain(Genome genome) {
        ann = new NeuralNet(genome);
        this.genome = genome;
    }

    public void setOwner(Entity o) {
        entity = o;
    }

    public Brain(Entity owner, Genome genome) {
        ann = new NeuralNet(genome);
        this.genome = genome;
        this.entity = owner;
    }

    NeuralNet ann = null;

    public static void main(String args[]) {
        World world = new World(1848, 1016);
        Entity e = new Entity(world);
        e.age = 100;
        Action a = e.brain.evaluate(world);
        e.brain.ann.input.calc();
        double d = e.brain.getOutput();

    }

    public synchronized void input(Entity e, World world) {
        ArrayList<Double> list = new ArrayList<>();
        try {
            if (e == null) {
                System.out.println("Genome is null");
            }
            list.add((double) world.getWidth());
            list.add((double) world.getHeight());
            list.add((double) Globals.spawn.x);
            list.add((double) Globals.spawn.y);
            list.add((double) Globals.spawn.width);
            list.add((double) Globals.spawn.height);
            list.add((double) Globals.spawn.getColor().getRGB());
            list.add((double) Globals.control.x);
            list.add((double) Globals.control.y);
            list.add((double) Globals.control.width);
            list.add((double) Globals.control.height);
            list.add((double) Globals.control.getColor().getRGB());
            list.add((double) (1d));
            list.add((double) e.location.x);
            list.add((double) e.location.y);
            list.add(e.location.vy);
            list.add(e.location.vx);
            list.add(e.distanceX);
            list.add(e.distanceY);
            list.add((double) e.age);
            list.add((double) e.getEnergy());
            list.add((double) Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE));

            list.add((double) (e.fertile ? 1d : 0d));
            list.add((double) (e.isTrajectoryGoal() ? 1d : 0d));
            list.add((double) (e.isTrajectoryDeath() ? 1d : 0d));
            list.add((double) (e.walls));
            list.add((double) e.size);
            list.add(e.getEnergy());
            list.add((double) KinFactory.create(e.genome.read(Gene.KIN)));
            list.add((double) e.genome.index);
            list.add((double) e.genome.read(e.genome.index));
            list.add((double) e.location.x - Globals.spawn.x);
            list.add((double) e.location.y - Globals.spawn.y);
            list.add((double) e.location.x - Globals.spawn.width);
            list.add((double) e.location.y - Globals.spawn.width);
            list.add((double) world.getLivingCount());
            long mlcs = Utility.checksum(e.genome.code);
            double mld = Utility.flatten(mlcs, Action.values().length);
            list.add(mld);
            for (int i = 0; i < world.selection.rlist.size(); i++) {
                list.add((double) world.selection.rlist.get(i).x);
                list.add((double) world.selection.rlist.get(i).y);
                list.add((double) world.selection.rlist.get(i).width);
                list.add((double) world.selection.rlist.get(i).height);
                list.add((double) (world.selection.rlist.get(i).kill ? 1d : 0d));
                list.add((double) world.selection.rlist.get(i).getColor().getRGB());
                list.add((double) (0d));
            }

            ArrayList<Entity> m = e.sampleForward();

            for (int i = 0; i < m.size(); i++) {
                try {
                    list.add((double) m.get(i).location.x);
                    list.add((double) m.get(i).location.y);
                    list.add((double) m.get(i).location.vx);
                    list.add((double) m.get(i).location.vy);
                    list.add((double) m.get(i).age);
                    list.add((double) Settings.DEATH_MULTIPLIER * m.get(i).genome.read(Gene.AGE));
                    list.add((double) (m.get(i).fertile ? 1d : 0d));
                    list.add((double) m.get(i).size);

                    list.add((double) (m.get(i).alive ? 1d : 0d));
                    list.add((double) m.get(i).getEnergy());
                    list.add((double) (KinFactory.create(m.get(i).genome.read(Gene.KIN))));
                    list.add((double) (m.get(i).walls));
                    list.add((double) (m.get(i).reachedGoal ? 1d : 0d));
                } catch (Exception ex) {
                }
            }


            for (int i = 0; i < world.list.size(); i++) {
                try {
                    list.add((double) world.list.get(i).location.x);
                    list.add((double) world.list.get(i).location.y);
                    list.add((double) world.list.get(i).location.vx);
                    list.add((double) world.list.get(i).location.vy);
                    list.add((double) world.list.get(i).age);
                    list.add((double) Settings.DEATH_MULTIPLIER * world.list.get(i).genome.read(Gene.AGE));
                    list.add((double) (world.list.get(i).fertile ? 1d : 0d));
                    list.add((double) world.list.get(i).size);

                    list.add((double) (world.list.get(i).alive ? 1d : 0d));
                    list.add((double) world.list.get(i).getEnergy());
                    list.add((double) (KinFactory.create(world.list.get(i).genome.read(Gene.KIN))));
                    list.add((double) (world.list.get(i).walls));
                    list.add((double) (world.list.get(i).reachedGoal ? 1d : 0d));
                } catch (Exception ex) {
                }

            }
        } catch (Exception ex) {

        } finally {
            try {
                e.brain.ann.input.input = list;
            } catch (Exception ex) {
            }
        }
    }

    public double getOutput() {
        return getOutput(entity);
    }


    public double getOutput(Entity e) {
        NeuralLayer d = e.brain.ann.output;
        ArrayList<Neuron> n = d.neuron;
        Neuron nn = d.neuron.get(0);
        double r = 0;
        if (nn == null) {
            System.out.println("nn is null");
        } else {
            r = nn.getOutput();
        }
        return r;
    }

    Action last = null;

    public boolean isOdd(int in) {
        return in % 2 == 1;
    }

    public synchronized Action evaluate(World world) {

        try {

            if (entity != null) {
                if ( entity.target &&  entity.isTrajectoryGoal() && entity.walls <= 1) {
                    entity.location.vx = entity.targetvx;
                    entity.location.vy = entity.targetvy;
                    return Action.FASTER;
                } else if( entity.target && !entity.isTrajectoryGoal()) {
                    entity.process(Action.SLOW, world, 0);
                    entity.process(Action.SCAN, world, 0);
                } else {
                    input(entity, world);
                    try {
                        long s = System.currentTimeMillis();
                        entity.brain.ann.input.calc();
                        long e = System.currentTimeMillis();
                    } catch (Exception ex) {
                        return Action.CONTINUE;
                    }
                    long s = System.currentTimeMillis();
                    double d = entity.brain.getOutput();
                    double loc = entity.genome.index + (int) d;
                    double floc = 0;
                    char cfloc = 0;

                    if (isOdd((int) loc)) {
                        cfloc = entity.genome.read((int) loc);
                    } else {
                        entity.genome.advance();
                        cfloc = entity.genome.read((int) entity.genome.index());
                    }
                    if (cfloc == entity.genome.last()) {
                        entity.process(Action.RECODE, world, 1);
                    }
                    entity.input = floc;
                    Action a = ActionFactory.create((double) cfloc);
                    entity.genome.advance();
                    long e = System.currentTimeMillis();

                    if (entity.last != a) {
                        entity.last = a;
                        return a;
                    } else {
                        if( !entity.isTrajectoryGoal() ) {
                            return Action.SLOW;
                        }
                        long ss = System.currentTimeMillis();
                        input(entity, world);
                        entity.brain.ann.input.calc();
                        double dd = entity.brain.getOutput();
                        dd = Utility.flatten(dd, (double) Action.values().length);
                        a = ActionFactory.create(entity.genome.read(entity.genome.index + (int) dd));
                        entity.genome.advance();
                        entity.last = a;
                        long ee = System.currentTimeMillis();

                        return a;
                    }
                }
            }
        } catch (Exception ex) {
            return Action.SCAN;
        }
        return Action.SCAN;
    }

    public Action evaluate(Entity entity, World world) {

        try {

            if (entity != null) {
                input(entity, world);
                entity.brain.ann.input.calc();
                double d = entity.brain.getOutput();
                Action a = ActionFactory.create(entity.genome.read(entity.genome.index + (int) d));
                entity.genome.advance();
                if (entity.last != a) {
                    entity.last = a;
                    return a;
                } else {
                    d = Utility.flatten(d, (double) Action.values().length);
                    a = ActionFactory.create(entity.genome.read(entity.genome.index + (int) d));
                    entity.genome.advance();
                    entity.last = a;
                    return a;
                }
            } else {
                entity.genome.advance();
                entity.last = Action.IF;
                return Action.IF;
            }
        } catch (Exception ex) {
            return Action.IF;
        }

    }
}

class Entity {
    Brain brain = null;
    Coords location = new Coords();
    Genome genome = null;
    int generation = 0;
    boolean fertile = false;
    private double energy = Settings.ENERGY;
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

    public static GARLRectangle closest(ArrayList<GARLRectangle> list, Entity e) {
        int distX = Integer.MAX_VALUE;
        int distY = Integer.MAX_VALUE;
        GARLRectangle closest = new GARLRectangle();
        closest.x = Integer.MAX_VALUE - 2;
        closest.y = Integer.MAX_VALUE - 2;
        closest.width = Integer.MAX_VALUE - 1;
        closest.height = Integer.MAX_VALUE - 1;


        for (int i = 0; i < list.size(); i++) {
            GARLRectangle g = list.get(i);

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
            System.out.println("Closest:" + closest.x + "-" + closest.y + " w:" + closest.width + " h:" + closest.height + " spawn:" + closest.spawner);
            System.out.println("Entity:" + e.location.x + "-" + e.location.y);
        }
        if (closest != null) {
            return closest;
        }

        return null;
    }

    public boolean isTrajectoryGoal() {
        ArrayList<GARLRectangle> mwalls = sampleForward(this);

        GARLRectangle first = closest(mwalls, this);
        if (walls == 0) {
            return false;
        }
        double direction = degree;
        int _xs = (int) ((int) (location.x + (size / 2)) + (size * world.getWidth() * Math.cos(direction * ((Math.PI) / 360d)))); //);
        int _ys = (int) ((int) (location.y + (size / 2)) - (size * world.getHeight() * Math.sin(direction * ((Math.PI) / 360d)))); //);

        int x = (int) location.x + ((size / 2) / 2);
        int y = (int) location.y + ((size / 2) / 2);

        Line2D line = new Line2D.Double((double) x, (double) y, (double) _xs, (double) _ys);


        GARLRectangle goal = Globals.spawn;
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

    public ArrayList<GARLRectangle> sampleForward(Entity e) {
        double direction = e.degree;
        int _xs = (int) ((int) (e.location.x + (e.size / 2)) + (e.size * world.getWidth() * Math.cos(direction * ((Math.PI) / 360d)))); //);
        int _ys = (int) ((int) (e.location.y + (e.size / 2)) - (e.size * world.getHeight() * Math.sin(direction * ((Math.PI) / 360d)))); //);

        int x = (int) e.location.x + ((e.size / 2) / 2);
        int y = (int) e.location.y + ((e.size / 2) / 2);

        Line2D line = new Line2D.Double((double) x, (double) y, (double) _xs, (double) _ys);

        ArrayList<GARLRectangle> walls = new ArrayList<>();
        for (int i = 0; i < world.selection.rlist.size(); i++) {
            try {
                GARLRectangle wall = world.selection.rlist.get(i);
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

        ArrayList<GARLRectangle> walls = sampleForward(this);
        // check to see if wall with closest trajectory is spawn or not.
        GARLRectangle first = closest(walls, this);

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
            ArrayList<GARLRectangle> list = world.selection.rlist;
            for (int i = 0; i < list.size(); i++) {
                GARLRectangle rect = list.get(i);
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
        e.energy = Settings.ENERGY;
        e.degree = Math.random() * 360;
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

    public Action think(World world, long start) {


        age++;
        consume();
        Action action = null;
        try {
            ArrayList<GARLRectangle> list = sampleForward(this);
            GARLRectangle closest = Entity.closest(list, this);
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
                    location.vy = location.vy+(ey/ex);
                    process(Action.FASTER, world, 0);

                }
            }
            if( target && isTrajectoryGoal() ){
                return Action.FASTER;
            } else {
                action = brain.evaluate(world);
                long intermediate = System.currentTimeMillis();
            }

        } catch (Exception ex) {
        }

        int depth = 0;
        process(action, world, depth);
        long intermediate = System.currentTimeMillis();

        world.setState(action);

        return action;
    }


    double register = 0;
    double goal = 0;
    double direction = 1;
    double cycle = 0;

    double anglex = 0;
    double angley = 0;

    public void process(Action action, World world, int depth) {

        depth++;
        if (depth > Settings.MAX_THINK_DEPTH) {
            if (action != last) {
                last = Action.SCAN;
                process(Action.SCAN, world, depth);
                return;
            }
        }

        if (brain != null) {

            switch (action) {
                case NONE:
                case SIN:

                    anglex = Math.sin(anglex);
                    angley = Math.sin(angley);
                    break;
                case COS:
                    anglex = Math.cos(anglex);
                    angley = Math.cos(angley);
                    break;
                case TAN:
                    anglex = Math.tan(anglex);
                    angley = Math.tan(angley);
                    break;

                case STOP:
                    // if we're stopped - and we're touching someone, lets move.
                    if(!target ) {
                        if (isTouching()) {
                            doKill(Action.KILL);
                        } else {
                            location.vx = 0;
                            location.vy = 0;
                        }
                    } else if(  !isTrajectoryGoal() ){
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
                        location.vx = targetvx;
                        location.vy = targetvy;
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
                        if (target == false) {

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
                    location.vy = location.vy * (1 + Settings.ACCELERATION);
                    location.vx = location.vx * (1 + Settings.ACCELERATION);
                    break;
                /*
                case ADJUST:

                    double adjustmentvx = Double.NaN;
                    double adjustmentvy = Double.NaN;

                    adjustmentvx = Math.sqrt(Math.pow(location.x,2) + Math.pow(targetx,2));
                    adjustmentvy = Math.sqrt(Math.pow(location.y,2) + Math.pow(targety,2));

                    location.vy = location.vy * adjustmentvy;
                    location.vx = location.vx * adjustmentvx;
                    break;
                 */
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


    }

    boolean target = false;

    double targetvx = Double.NaN;
    double targetvy = Double.NaN;

    double targetx = Double.NaN;
    double targety = Double.NaN;


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
    final static int RECODE_PREFERENCE = 29;
    final static int MAX_APPENDS = 30;
    final static int MAX_DELETIONS = 31;

}

class KinFactory {
    public static char create(char c) {
        if (c < 'a') {
            return 'f';
        } else if (c < 'e') {
            return 'c';
        } else if (c < 'm') {
            return 'g';
        } else if (c > 'w') {
            return 't';
        }
        return 'z';
    }
}

class World extends JLabel {
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
    double increment = 0.0001000;
    int mx = 0;
    int my = 0;
    static Entity selected = null;


    int spawns = 0;
    int impact = 0;
    int epoch = 1;

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
        for (int i = 0; i < list.size(); i++) {
            Entity e = (Entity) list.get(i);
            if (e.alive) {
                livingCount++;
            }
        }
        return livingCount;
    }

    public int getDeadCount() {
        int deadCount = 0;
        for (int i = 0; i < list.size(); i++) {
            Entity e = (Entity) list.get(i);
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
        Graphics2D g2 = (Graphics2D) g;
        DecimalFormat df = new DecimalFormat("0.00000000");

        step++;
        if (totalSpawns > totalControls) {
            phl += increment;
        }

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        ArrayList<GARLRectangle> rlist = selection.rlist;

        for (int j = 0; j < rlist.size(); j++) {
            try {
                GARLRectangle rect = rlist.get(j);
                Point2D point = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
                Color[] colors = {Color.pink, Color.pink, Color.pink};
                float[] dist = {0.0f, 0.5f, 1.0f};
                Point2D center = new Point2D.Float(0.5f * rect.width, 0.5f * rect.height);

                RadialGradientPaint p =
                        new RadialGradientPaint(center, 0.5f * rect.width, dist, colors);
                //RadialGradientPaint rgp = new RadialGradientPaint(point, (float)rect.width, (float)rect.height, rect.color);
                g2.setPaint(p);
                if( rect.spawner ){
                    g2.setColor(rect.getColor());
                }
                if( rect.control ){
                    g2.setColor(rect.getColor());
                }
                //g2.setColor(rect.getColor());
                if (rect != null) {
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                }
            } catch(Exception ex) {}
        }

        int livingCount = getLivingCount();
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);

            int r = (int) Math.ceil((double) e.size / 2);


            if (e.alive) {
                g2.setColor(e.color);
            } else {
                e.color = Color.BLUE;
                g2.setColor(Color.BLUE);
            }
            Point p = new Point((int) e.location.x + (r / 2), (int) e.location.y + (r / 2));
            //if (r >= 1) {
            Color kin = Color.yellow;
            try {
                int k = KinFactory.create(e.genome.read(Gene.KIN));
                kin = new Color(k, 128 % (k % 256), 128 % (k % 256));
            } catch (Exception ex) {

            }
            drawVisibilityCircle(g2, kin, p, r, e.color, e);
            //}

            if (e == selected) {
                g2.drawOval((int) e.location.x - (r / 2), (int) e.location.y - (r / 2), r * 2, r * 2);
            }

            double direction = e.degree;
            int xs = (int) ((int) (e.location.x + r) + (e.size * Math.cos(direction * ((Math.PI) / 360d)))); //);
            int ys = (int) ((int) (e.location.y + r) - (e.size * Math.sin(direction * ((Math.PI) / 360d)))); //);
            int _xs = (int) ((int) (e.location.x + r) + (e.size * getWidth() * Math.cos(direction * ((Math.PI) / 360d)))); //);
            int _ys = (int) ((int) (e.location.y + r) - (e.size * getHeight() * Math.sin(direction * ((Math.PI) / 360d)))); //);

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

        }


        drawPopup(g2, selected, mx, my);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, getHeight() - 24, getWidth(), getHeight());
        g2.setColor(Color.YELLOW);
        g2.drawString("Think:" + step + " population:" + livingCount + " killed:" + (list.size() - livingCount) + " " + df.format(phl) + " PHL " + getWidth() + " x " + getHeight() + " epoch:" + epoch + " children:" + children + " impact death:" + impact + " controls:" + controls + " spawns:" + spawns + " total spawns:" + totalSpawns + " total controls:" + totalControls + " best seed:" + bestSpawn, 10, (getHeight() - 10));

        g2.dispose();
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
            DecimalFormat df = new DecimalFormat("0.00");
            g.setColor(Color.white);
            g.fillRect(mx, my, popupWidth, popupHeight);
            g.setColor(Color.BLACK);

            g.drawString("Position: X " + df.format(e.location.x) + "-Y " + df.format(e.location.y), mx + spacing, my + spacing * 1);
            g.drawString("Size:" + e.size, mx + spacing, my + spacing * 2);
            g.drawString("Age:" + e.age, mx + spacing, my + spacing * 3);
            g.drawString("Energy:" + df.format(e.getEnergy()), mx + spacing, my + spacing * 4);
            g.drawString("Degree: " + df.format(Math.abs(e.degree)), mx + spacing, my + spacing * 5);
            g.drawString("VX: " + df.format(e.location.vx), mx + spacing, my + spacing * 6);
            g.drawString("VY: " + df.format(e.location.vy), mx + spacing, my + spacing * 7);
            g.drawString("Alive: " + e.alive, mx + spacing, my + spacing * 8);
            g.drawString("Reproductive Number: " + (int) e.genome.read(Gene.RR), mx + spacing, my + spacing * 9);
            g.drawString("Kill Gene: " + (int) e.genome.read(Gene.KILL), mx + spacing, my + spacing * 10);

            try {
                g.drawString("Thought: " + e.last.toString() + " " + df.format(e.input), mx + spacing, my + spacing * 11);
            } catch (Exception ex) {
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
            //g.drawString("Genome Length: " + e.genome.code.length() / sz, mx + spacing, my + spacing * 20);
            g.drawString("KIN: " + KinFactory.create(e.genome.read(Gene.KIN)) + ":" + e.genome.read(Gene.KIN), mx + spacing, my + spacing * 21);
            g.drawString("Fertile: " + e.fertile, mx + spacing, my + spacing * 22);
            g.drawString("Read Position: " + e.genome.index(), mx + spacing, my + spacing * 23);
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

class Population {

    public static ArrayList<Entity> create(World world, int individuals, int width, int height) {

        System.out.println("Creating population from random");
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

    public static ArrayList<Entity> create(World world, ArrayList seedList, int individuals, int width, int height) throws IOException {


        System.out.println("Create population from seed list");
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        world.children = 0;

        Comparator<Entity> comparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                if (e1.generation == e2.generation) {
                    return 0;
                } else if (e1.generation < e2.generation) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        try {
            Collections.sort(seedList, comparator);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int adjustedIndividuals = individuals - seedList.size();

        String fileName = System.currentTimeMillis() + "-" + world.epoch + "-epoch.json";
        System.out.println(fileName);
        FileWriter writer = new FileWriter(new File(fileName));
        writer.write("[");
        for (int i = 0; i < individuals; i++) {
            try {
                Entity seed = (Entity) seedList.get(i);
                Entity e = seed.replicate();
                e.location.x = rand.nextInt(width);
                e.location.y = rand.nextInt(height);
                entities.add(e);
                if (Globals.verbose) {
                    writer.write("{ \"epoch\":" + world.epoch + ", \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                    if (i + 1 != individuals) {
                        writer.write(",\n");
                    }
                }
            } catch (Exception ex) {
            }

        }


        if (adjustedIndividuals > 0) {
            for (int i = 0; i < adjustedIndividuals; i++) {
                try {
                    Entity e = new Entity(world);
                    e.location.x = rand.nextInt(width);
                    e.location.y = rand.nextInt(height);
                    entities.add(e);
                    if (Globals.verbose) {
                        writer.write("{ \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                        if (i + 1 != adjustedIndividuals) {
                            writer.write(",\n");
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
        writer.flush();
        writer.write("]");
        writer.flush();
        writer.close();

        return entities;
    }
}

class Settings {
    static int INSPECTOR_WIDTH = 400;
    static double ACCELERATION = 1.0;
    static int CHAR_SET = 62;
    static int GENOME_LENGTH = 32;
    static int STARTING_POPULATION = 100;
    static int MAX_OFFSPRING = 4;
    static boolean NATURAL_REPLICATION = true;
    static int MAX_THINK_DEPTH = 4;
    static int NUMBER_OF_INPUTS = 32; //STARTING_POPULATION * 12; // Action.values().length;
    static int DEATH_MULTIPLIER = 25;
    static int GENE_POOL = 2;
    static int MAX_SIZE = 18;
    static int MIN_SIZE = 5;
    static int MAX_NEURONS = 4;


    static int CELL_MOVEMENT = 1;
    static int MAX_SPEED = 6;
    static int MAX_POPULATION = 500;

    static double ENERGY = 10.0;
    static double ENERGY_STEP_COST = 0.00001;
    static double ENERGY_STEP_SLEEP_COST = 0.01;


}

class Selection {

    public static ArrayList<GARLRectangle> rlist = new ArrayList<>();
    World world = null;

    Selection(World world) {
        this.world = world;
        makeNewList();

    }

    public void makeNewList() {
        GARLRectangle[] list = new GARLRectangle[16];
        rlist = new ArrayList<>();

        list[0] = new GARLRectangle();
        list[0].x = world.getWidth() - 20;
        list[0].y = 0;
        list[0].width = 20;
        list[0].height = world.getHeight() - 20;

        list[1] = new GARLRectangle();
        list[1].x = 100;
        list[1].y = 100;
        list[1].width = 400;
        list[1].height = 20;

        list[2] = new GARLRectangle();
        list[2].x = 0;
        list[2].y = -10;
        list[2].width = world.getWidth() - 20;
        list[2].height = 30;

        list[3] = new GARLRectangle();
        list[3].x = 0;
        list[3].y = world.getHeight() - 20;
        list[3].width = world.getWidth() - 20;
        list[3].height = 20;


        list[4] = new GARLRectangle();
        list[4].x = (int) (200 * Math.random());
        list[4].y = (int) (200 * Math.random());
        list[4].width = (int) (20 * Math.random());
        list[4].height = (int) (400 * Math.random());

        list[5] = new GARLRectangle();
        list[5].x = (int) (0);
        list[5].y = (int) (world.height - 80);
        list[5].width = (int) (world.width);
        list[5].height = (int) (80);


        list[6] = new GARLRectangle();
        list[6].x = (int) (200 * Math.random());
        list[6].y = (int) (500 * Math.random());
        list[6].width = (int) (20 * Math.random());
        list[6].height = (int) (430 * Math.random());

        list[7] = new GARLRectangle();
        list[7].x = (int) 0;
        list[7].y = (int) 300;
        list[7].width = (int) 20;
        list[7].height = (int) (630 * Math.random());

        list[8] = new GARLRectangle();
        list[8].x = (int) (700 * Math.random());
        list[8].y = (int) (200 * Math.random());
        list[8].width = (int) 20;
        list[8].height = (int) (730 * Math.random());


        list[9] = new GARLRectangle();
        list[9].x = (int) (990 * Math.random());
        list[9].y = (int) 0;
        list[9].width = (int) 20;
        list[9].height = (int) (700 * Math.random());


        list[10] = new GARLRectangle();
        list[10].x = (int) (1440 * Math.random());
        list[10].y = (int) 500;
        list[10].width = (int) 20;
        list[10].height = (int) (430 * Math.random());


        list[11] = new GARLRectangle();
        list[11].x = (int) (1400 * Math.random());
        list[11].y = (int) (500 * Math.random());
        list[11].width = (int) (100 * Math.random());
        list[11].height = (int) 20;


        list[12] = new GARLRectangle();
        list[12].x = (int) 0;
        list[12].y = (int) (1000 * Math.random());
        list[12].width = (int) (1800 * Math.random());
        list[12].height = (int) 20;

        list[13] = new GARLRectangle();
        list[13].x = (int) (1400 * Math.random());
        list[13].y = (int) 100;
        list[13].width = (int) 100;
        list[13].height = (int) (300 * Math.random());


        list[14] = new GARLRectangle();
        list[14].x = (int) ((world.width - 100) * Math.random());
        list[14].y = (int) ((world.height - 100) * Math.random());
        list[14].control = true;
        list[14].width = (int) (100 * Math.random()) + 30;
        list[14].height = (int) (100 * Math.random()) + 30;
        list[14].kill = true;

        list[15] = new GARLRectangle();
        list[15].x = (int) ((world.width - 100) * Math.random());
        list[15].y = (int) ((world.height - 100) * Math.random());
        list[15].width = (int) (100 * Math.random()) + 30;
        list[15].height = (int) (100 * Math.random()) + 30;
        list[15].spawner = true;
        list[15].kill = false;


        Globals.spawn = list[15];
        Globals.control = list[14];

        for (int i = 0; i < list.length; i++) {
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

class KeyHandler implements KeyListener {

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        System.out.println("Key Pressed");
        if (world.selected != null) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                System.out.println("Key Right");
                world.selected.process(Action.MOVE_RIGHT, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                System.out.println("Key Left");
                world.selected.process(Action.MOVE_LEFT, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                System.out.println("Key Up");
                world.selected.process(Action.MOVE_UP, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println("Key Down");
                world.selected.process(Action.MOVE_DOWN, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_O) {
                System.out.println("Key O");
                World.offset += 1;
                System.out.println("offset:" + World.offset);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_D) {
                System.out.println("Key D");
                World.offset -= 1;
                System.out.println("offset:" + World.offset);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
                System.out.println("Key S");
                world.selected.process(Action.STOP, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_K) {
                System.out.println("Key K");
                for (int i = 0; i < world.list.size(); i++) {
                    Entity e = (Entity) world.list.get(i);
                    e.die();
                }
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    KeyHandler(World world) {
        this.world = world;
    }

    World world = null;
}

class MouseHandler implements MouseMotionListener, MouseListener {

    World world = null;

    public MouseHandler(World world) {
        this.world = world;
    }

    int startx = -1;
    int starty = -1;
    int endx = -1;
    int endy = -1;

    boolean dragging = false;
    GARLRectangle current = null;

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

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        Globals.control.x = mouseEvent.getX();
        Globals.control.y = mouseEvent.getY();

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

    public void save(int epoch, int generation, Genome g) {
        try {
            FileWriter writer = new FileWriter(new File("./" + System.currentTimeMillis() + "-genome-" + GARLTask.run.toString() + ".json"));
            writer.write("{ \"epoch\":" + epoch + ",\"generation\":" + generation + ", \"genome\":\"" + g.code + "\" }");
            writer.flush();
            writer.close();

        } catch (Exception ex) {

        }
    }


    @Override
    public void run() {
        // Perform Selection.
        try {
            Globals.semaphore.acquire();

            long start = System.currentTimeMillis();
            Selection selection = world.selection;

            ArrayList<GARLRectangle> rlist = world.selection.rlist;

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

                            if (selection.insideRect(rect, (int) e.location.x, (int) e.location.y)) {
                                if (rect.spawner) {
                                    Globals.spawn = rect;
                                    save(world.epoch, e.generation, e.genome);
                                    for (int k = 0; k < Settings.MAX_OFFSPRING; k++) {
                                        Entity n = e.clone();
                                        n.location.x = rand.nextInt(frame.getWidth());
                                        n.location.y = rand.nextInt(frame.getHeight());
                                        n.alive = true;
                                        world.list.add(n);
                                        world.spawns++;
                                        world.totalSpawns++;

                                        world.prospectSeeds.add(e.clone());
                                        if (world.spawns >= world.bestSpawn) {
                                            world.bestSeeds.add(e.clone());
                                            world.bestSpawn = world.spawns;
                                        }
                                        long intermediate = System.currentTimeMillis();
                                        if (intermediate - start > Globals.threshold) {
                                            return;
                                        }
                                    }
                                    e.reachedGoal = true;
                                    e.die();
                                } else if (rect.kill) {
                                    if (rect.control) {
                                        world.controls += Settings.MAX_OFFSPRING;
                                        world.totalControls += Settings.MAX_OFFSPRING;
                                    }
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

                long end = System.currentTimeMillis();
            }
        } catch (Exception ex) {

        } finally {
            Globals.semaphore.release();
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

        long start = System.currentTimeMillis();

        try {
            Globals.semaphore.acquire();
        } catch (Exception ex) {
            return;
        }
        int livingCount = world.getLivingCount();
        if (Settings.NATURAL_REPLICATION) {
            for (int i = 0; i < world.list.size(); i++) {

                Entity e = world.list.get(i);

                if (e.fertile && e.alive) {
                    int min = Math.max(128, e.genome.read(Gene.MATURITY) * 2);
                    if (e.alive && (e.age > min)) {
                        if (Math.random() > 0.8) {
                            int n = e.genome.read(Gene.RR) % Settings.MAX_OFFSPRING;
                            if (livingCount > Settings.MAX_POPULATION) {
                                n = Math.min(2, n);
                            }
                            final int nn = n;

                            for (int j = 0; j <= nn; j++) {

                                Entity a = e.replicate();
                                world.list.add(a);
                                world.prospectSeeds.add(a);
                                world.children++;
                            }

                            e.die();

                        }
                    }
                }
            }
        }

        long end = System.currentTimeMillis();
        Globals.semaphore.release();
    }
}


class ThinkTask extends TimerTask {
    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;
    int chunk = 0;

    public ThinkTask(JFrame frame, World world, int width, int height, int chunk) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
        this.chunk = chunk;
    }

    long start = 0;

    @Override
    public void run() {

        start = System.currentTimeMillis();

        try {
            Globals.semaphore.acquire();

            int livingCount = world.getLivingCount();

            for (int i = 0; i < world.list.size(); i++) {
                try {
                    Entity e = world.list.get(i);
                    if (e.alive) {
                        //e.process(Action.CYCLE, world, 0);

                        e.think(world, start);
                    }
                } catch (Exception ex) {
                }
            }

            if (livingCount <= Settings.GENE_POOL || livingCount >= Settings.MAX_POPULATION) {
                try {

                    ArrayList<Seed> list = GARLTask.load();

                    System.out.println("Recreate population");
                    world.list = new ArrayList<>();
                    ArrayList<Entity> seedList = GARLTask.load(list, world);
                    for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
                        try {
                            Entity a = seedList.get(i);
                            if (a.alive) {
                                world.list.add(a);
                            }
                        } catch (Exception ex) {
                        }

                    }


                    world.children = 0;
                    world.impact = 0;
                    world.spawns = 0;
                    world.controls = 0;

                    world.epoch++;
                    if (world.epoch == 900) {
                        System.out.println("total controls:" + world.totalControls);
                        System.out.println("total spawns:" + world.totalSpawns);
                        System.exit(-1);
                    }
                    if (livingCount <= Settings.GENE_POOL && seedList.isEmpty()) {
                        try {
                            world.list = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                            world.selection.makeNewList();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (livingCount <= Settings.GENE_POOL && !seedList.isEmpty()) {

                        try {
                            world.list = Population.create(world, seedList, Math.max(Settings.STARTING_POPULATION, Math.min(seedList.size(), Settings.STARTING_POPULATION)), frame.getWidth(), frame.getHeight());
                            world.selection.makeNewList();
                            System.out.println("Using seed list:" + seedList.size());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                } catch (Exception ex) {
                }

            }
        } catch (Exception ex) {
        } finally {
            Globals.semaphore.release();
        }

        long end = System.currentTimeMillis();
    }
}

class Seed {
    int epoch;
    int generation;
    String genome;
}

class SeedList {
    Seed[] seeds = null;
}

class NNCanvas extends Canvas {

    Entity entity = null;
    World world = null;

    public NNCanvas(World world) {
        this.world = world;
    }

    public void setSelected(Entity e) {
        entity = e;
    }

    boolean NNdebug = true;

    public void paint(Graphics g) {

        try {
            if (NNdebug) {
                return;
            }
            entity = world.selected;
            if (entity == null) {
                return;
            }
            if (!entity.alive) {
                return;
            }
            int inputs = entity.brain.ann.input.numberOfNeuronsInLayer;
            int dense = entity.brain.ann.dense.numberOfNeuronsInLayer;
            int hidden = entity.brain.ann.hidden.numberOfNeuronsInLayer;
            int dropout = entity.brain.ann.dropout.numberOfNeuronsInLayer;
            int output = entity.brain.ann.output.numberOfNeuronsInLayer;

            int numLayers = 5;
            int circle = 3;
            int space = circle * 2;
            int startingPos = 10;
            int pos = 3;
            int initPos = pos;
            int hpos = 10;
            g.setColor(Color.BLACK);
            int maxHeight = 0;
            int offset = 0;
            for (int i = 1; i <= inputs; i++) {

                try {
                    int f = space * i;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                    maxHeight = pos;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            offset = maxHeight / dense / 2;
            space = (maxHeight / dense);

            int startingPosX = startingPos;
            int startingPosY = startingPos;

            startingPosX += circle;
            for (int j = 1; j <= inputs; j++) {
                for (int k = 1; k <= dense; k++) {
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset * j );
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset  + space *j);
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset + space + space *j);
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset + space + space + space *j);
                }
                startingPosY = (j+initPos);
                System.out.println("Going down:" + startingPosY + "j" + j + "*pos" + pos);

            }


            for (int i = 1; i <= dense; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            space = maxHeight / hidden;
            offset = maxHeight / hidden / 2;
            for (int i = 1; i <= hidden; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            space = maxHeight / dropout;
            offset = maxHeight / dropout / 2;

            for (int i = 1; i <= dropout; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            space = maxHeight / output;

            offset = maxHeight / 2;
            for (int i = 1; i <= output; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            //g.drawString("" + dense, 10, 20);
            //g.drawString("" + hidden, 10, 30);
            //g.drawString("" + dropout, 10, 40);
            //g.drawString("" + output, 10, 50);
        } catch (Exception ex) {
            g.drawString("Selected is null", 10, 10);
        }

    }
}

public class GARLTask extends Thread {

    public static UUID run = UUID.randomUUID();
    ArrayList<Seed> list = null;
    final static int FPS = 64;

    public GARLTask(ArrayList<Seed> list) {
        if (list != null) {
            this.list = list;
        }
    }


    public static ArrayList<Seed> load() throws IOException {
        ArrayList<Seed> list = new ArrayList<>();
        String seed = "./";
        Gson gson = new Gson(); //null;
        // create a reader
        File dir = new File(seed);
        //File[] listFiles = dir.listFiles();
        File[] listFiles = dir.listFiles();
        File[] files = dir.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        int ctr = 0;
        for (int i = files.length - 1; i >= 0; i--) {
            File f = files[i];
            if (f.getName().contains("genome")) {
                String fName = f.getName();
                System.out.println("Using Entity:" + f.getName());
                Reader reader = Files.newBufferedReader(Paths.get(fName));
                try {
                    Seed lseed = (Seed) gson.fromJson(reader, Seed.class);
                    list.add(lseed);
                    ctr++;
                } catch (Exception ex) {
                }
            }
            if (ctr >= Settings.STARTING_POPULATION) {
                break;
            }
            // convert JSON string to User object
        }
        return list;
    }

    public static ArrayList<Entity> load(ArrayList<Seed> seeds, World world) throws IOException {
        ArrayList<Seed> list = seeds;
        ArrayList<Entity> ents = new ArrayList<>();

        for (int i = 0; i < Math.min(list.size() > Settings.STARTING_POPULATION ? list.size() : Settings.STARTING_POPULATION, Settings.STARTING_POPULATION); i++) {
            if (list.get(i).genome.contains("-")) {

                continue;
            }
            try {
                String genome = list.get(i).genome;
                System.out.println("Adding:" + i + ":" + genome);
                Genome g = new Genome(genome);
                Brain brain = new Brain(g);
                Entity e = new Entity(world);
                brain.setOwner(e);
                e.location.x = (int) (Math.random() * world.getWidth());
                e.location.y = (int) (Math.random() * world.getHeight());
                g.setOwner(e);
                e.brain = brain;

                e.genome = g;
                ents.add(e);
                System.out.println("Added:" + i + " at " + e.location.x + " " + e.location.y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ents;
    }


    public static void main(String[] args) throws IOException {


        ArrayList<Seed> list = new ArrayList<>();
        if (args.length > 0) {
            list = load();
        }

        GARLTask task = new GARLTask(list);
        task.start();

    }

    JFrame frame = new JFrame("Genetic Based Multi-Agent Reinforcement Learning");

    JPanel inspector = new JPanel();
    World world = null;
    Selection selection = null;

    public void run() {

        //1. Create the frame.

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int width = 1848;
        int height = 1016;
        int inspectorPanelWidth = Settings.INSPECTOR_WIDTH;
        world = new World(width - inspectorPanelWidth, height);
        selection = new Selection(world);
        frame.setSize(width, height);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        ArrayList<Entity> population = new ArrayList<>();
        if (list == null) {
            population = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth() - inspectorPanelWidth, frame.getWidth());
        } else {
            System.out.println("Loading from seed list:" + list.size());
            for (int i = 0; i < Math.min(list.size(), Settings.STARTING_POPULATION); i++) {
                if (list.get(i).genome.contains("-")) {

                    continue;
                }
                try {
                    String genome = list.get(i).genome;
                    System.out.println("Adding:" + i + ":" + genome);
                    Genome g = new Genome(genome);
                    Brain brain = new Brain(g);
                    Entity e = new Entity(world);
                    brain.setOwner(e);
                    e.location.x = (int) (Math.random() * width - inspectorPanelWidth);
                    e.location.y = (int) (Math.random() * height);
                    g.setOwner(e);
                    e.brain = brain;

                    e.genome = g;
                    population.add(e);
                    System.out.println("Added:" + i + " at " + e.location.x + " " + e.location.y);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        world.setPopulation(population);
        world.setSelection(selection);
        MouseHandler mouseHandler = new MouseHandler(world);
        KeyHandler keyHandler = new KeyHandler(world);
        world.addMouseMotionListener(mouseHandler);
        world.addMouseListener(mouseHandler);
        frame.addKeyListener(keyHandler);

        world.setMaximumSize(new Dimension(width - inspectorPanelWidth, height));
        world.setMinimumSize(new Dimension(width - inspectorPanelWidth, height));
        world.setPreferredSize(new Dimension(width - inspectorPanelWidth, height));

        inspector.setMaximumSize(new Dimension(inspectorPanelWidth, height));
        inspector.setMinimumSize(new Dimension(inspectorPanelWidth, height));
        inspector.setPreferredSize(new Dimension(inspectorPanelWidth, height));

        GridLayout gridLayout = new GridLayout(14, 2);
        inspector.setLayout(gridLayout);
        inspector.add(new JLabel("Starting Population"));
        JTextField startingPopulation = new JTextField("" + Settings.STARTING_POPULATION);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String text = startingPopulation.getText();
                    Integer value = Integer.parseInt(text);
                    Settings.STARTING_POPULATION = value;
                    System.out.println("Set starting population to:" + value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        startingPopulation.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Integer.parseInt(startingPopulation.getText()) <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number bigger than 0", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = startingPopulation.getText();
                    try {
                        Integer value = Integer.parseInt(text);
                        Settings.STARTING_POPULATION = value;
                        System.out.println("Set starting population to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive integers allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        DecimalFormat ddf = new DecimalFormat("0.00000");
        startingPopulation.addActionListener(al);
        inspector.add(startingPopulation);
        inspector.add(new JLabel("Minimum Gene Pool"));
        JTextField genePool = new JTextField("" + Settings.GENE_POOL);
        genePool.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Integer.parseInt(genePool.getText()) < 0) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number bigger 0 or larger", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = genePool.getText();
                    try {
                        Integer value = Integer.parseInt(text);
                        Settings.GENE_POOL = value;
                        System.out.println("Set GENE POOL to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive integers allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(genePool);
        inspector.add(new JLabel("Minimum Size"));
        JTextField minSize = new JTextField("" + Settings.MIN_SIZE);
        minSize.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Integer.parseInt(minSize.getText()) <= 3) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number bigger 3 or larger", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = minSize.getText();
                    try {
                        Integer value = Integer.parseInt(text);
                        Settings.MIN_SIZE = value;
                        System.out.println("Set min size to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive integers allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(minSize);
        inspector.add(new JLabel("Maximum Size"));
        JTextField maxSize = new JTextField("" + Settings.MAX_SIZE);
        maxSize.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Integer.parseInt(maxSize.getText()) >= 30) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number 30 or less", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = maxSize.getText();
                    try {
                        Integer value = Integer.parseInt(text);
                        Settings.MAX_SIZE = value;
                        System.out.println("Set max size to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive integers allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(maxSize);
        inspector.add(new JLabel("Starting Energy"));
        JTextField initialEnergy = new JTextField("" + ddf.format(Settings.ENERGY));
        initialEnergy.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Double.parseDouble(initialEnergy.getText()) >= 100) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number 100 or less", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = initialEnergy.getText();
                    try {
                        Double value = Double.parseDouble(text);
                        Settings.ENERGY = value;
                        System.out.println("Set initial energy to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive doubles allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(initialEnergy);
        inspector.add(new JLabel("Energy per Step"));
        JTextField energyStepCost = new JTextField("" + ddf.format(Settings.ENERGY_STEP_COST));
        energyStepCost.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Double.parseDouble(energyStepCost.getText()) >= 0.1) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number 0.1 or less", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = energyStepCost.getText();
                    try {
                        Double value = Double.parseDouble(text);
                        Settings.ENERGY_STEP_COST = value;
                        System.out.println("Set energy step cost to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive doubles allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(energyStepCost);
        inspector.add(new JLabel("Energy per Sleep"));
        JTextField energyStepCostSleep = new JTextField("" + ddf.format(Settings.ENERGY_STEP_SLEEP_COST));
        energyStepCostSleep.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Double.parseDouble(energyStepCostSleep.getText()) >= 0.1) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number 0.1 or less", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = energyStepCostSleep.getText();
                    try {
                        Double value = Double.parseDouble(text);
                        Settings.ENERGY_STEP_SLEEP_COST = value;
                        System.out.println("Set energy step sleep cost to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive doubles allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(energyStepCostSleep);
        inspector.add(new JLabel("Maximum Offspring"));
        JTextField maximumOffstring = new JTextField("" + Settings.MAX_OFFSPRING);
        maximumOffstring.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Integer.parseInt(maximumOffstring.getText()) >= 2) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number 2 or more", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = maximumOffstring.getText();
                    try {
                        Integer value = Integer.parseInt(text);
                        Settings.MAX_OFFSPRING = value;
                        System.out.println("Set max offspring to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive integers allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(maximumOffstring);
        inspector.add(new JLabel("Neurons in Layer (0)"));
        JTextField neuronsInBaseLayer = new JTextField("" + Settings.NUMBER_OF_INPUTS);
        neuronsInBaseLayer.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (Integer.parseInt(neuronsInBaseLayer.getText()) >= 8) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number 8 or more", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = neuronsInBaseLayer.getText();
                    try {
                        Integer value = Integer.parseInt(text);
                        Settings.NUMBER_OF_INPUTS = value;
                        System.out.println("Set neurons in base layer to:" + value);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only positive integers allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        });
        inspector.add(neuronsInBaseLayer);

        inspector.add(new JLabel("Reset"));
        JButton reset = new JButton("Clear");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (int i = 0; i < world.list.size(); i++) {
                    Entity e = (Entity) world.list.get(i);
                    e.die();
                }
            }
        });
        inspector.add(reset);
        inspector.add(new JLabel("Earnings"));
        JButton payout = new JButton("Payout");
        ActionListener payoutActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        };
        payout.addActionListener(payoutActionListener);
        inspector.add(payout);
        inspector.add(new JLabel("Address"));
        String address = "FB1nwpSEjAp86a8JaKrZfKY3XtNnVfbxRk";
        JTextField payoutAddress = new JTextField();
        Font font = new Font("Courier", Font.BOLD,10);
        payoutAddress.setFont( font );

        //payoutAddress.setFont(new Font(Font.MONOSPACED, 8, Font.PLAIN));
        payoutAddress.setText(address);
        inspector.add(payoutAddress);
        inspector.add(new JLabel("Selected Agent ANN"));

        JPanel inspectorContainer = new JPanel();
        inspectorContainer.setLayout(new BorderLayout());
        inspectorContainer.add(new JPanel(), BorderLayout.NORTH);
        inspectorContainer.add(new JPanel(), BorderLayout.EAST);
        inspectorContainer.add(new JPanel(), BorderLayout.WEST);

        JPanel selectedInspector = new JPanel();
        NNCanvas canvas = new NNCanvas(world);
        canvas.setMinimumSize(new Dimension(inspectorPanelWidth, inspectorPanelWidth));
        canvas.setMaximumSize(new Dimension(inspectorPanelWidth, inspectorPanelWidth));
        canvas.setPreferredSize(new Dimension(inspectorPanelWidth, inspectorPanelWidth));
        selectedInspector.add(canvas);
        inspectorContainer.add(selectedInspector, BorderLayout.SOUTH);

        inspectorContainer.add(inspector, BorderLayout.CENTER);


        frame.add(world, BorderLayout.CENTER);
        frame.add(inspectorContainer, BorderLayout.EAST);

        //4. Size the frame.

        //5. Show it.
        frame.setVisible(true);

        ThinkTask think = new ThinkTask(frame, world, width - inspectorPanelWidth, height, 5);
        SelectionTask selection = new SelectionTask(frame, world, width - inspectorPanelWidth, height);
        ReplicationTask replication = new ReplicationTask(frame, world, width - inspectorPanelWidth, height);

        Timer timer = new Timer(true);
        TimerTask paint = new TimerTask() {
            int ctr = 0;

            @Override
            public void run() {

                try {
                    long start = System.currentTimeMillis();
                    Globals.semaphore.acquire();
                    ctr++;
                    if (ctr > 100) {
                        ctr = 0;
                    }
                    world.repaint();
                    canvas.repaint();
                    long end = System.currentTimeMillis();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                } finally {
                    Globals.semaphore.release();
                }
            }
        };

        long taskTime = 120;
        timer.scheduleAtFixedRate(paint, 0, 1000 / FPS);
        timer.scheduleAtFixedRate(think, 0, taskTime);
        timer.scheduleAtFixedRate(selection, 50, taskTime);
        timer.scheduleAtFixedRate(replication, 100, taskTime);

    }

}

