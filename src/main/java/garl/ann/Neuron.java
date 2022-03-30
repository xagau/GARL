package garl.ann;

import garl.iaf.IActivationFunction;

import java.util.ArrayList;
import java.util.Random;

public class Neuron {
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
