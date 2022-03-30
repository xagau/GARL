package garl.ann;

import garl.Action;
import garl.Gene;
import garl.Genome;
import garl.Settings;
import garl.iaf.ActivationFactory;
import garl.iaf.IActivationFunction;
import garl.iaf.ReluFunction;
import garl.iaf.SoftmaxFunction;


public class NeuralNet {
    public InputLayer input = null;
    public HiddenLayer dense = null;
    public HiddenLayer hidden = null;
    public HiddenLayer dropout = null;
    public OutputLayer output = null;
    public Genome owner = null;

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