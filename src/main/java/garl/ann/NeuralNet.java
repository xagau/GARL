package garl.ann;

import garl.Action;
import garl.Gene;
import garl.Genome;
import garl.Settings;
import garl.iaf.ActivationFactory;
import garl.iaf.IActivationFunction;
import garl.iaf.ReluFunction;
import garl.iaf.SoftmaxFunction;
import garl.Log;


public class NeuralNet {
    public InputLayer input = null;
    public HiddenLayer dense = null;
    public HiddenLayer hidden = null;
    public HiddenLayer dropout = null;
    public OutputLayer output = null;
    public Genome owner = null;

    public NeuralNet(Genome g) {
        owner = g;
        g.jump(Settings.GENOME_LENGTH);
        int numInputs = Settings.NUMBER_OF_INPUTS;
        int numDense = Settings.MAX_NEURONS; //, Math.max(8, (int) g.read(Gene.DENSE)) );
        int numHidden = Settings.MAX_NEURONS; //, Math.max(8,(int) g.read(Gene.HIDDEN)) );
        int numDropout = Settings.MAX_DROPOUT;

        SoftmaxFunction softmax = new SoftmaxFunction(Action.values().length);
        try {
            output = new OutputLayer(1, softmax, numHidden);
            output.setNeuralNet(this);
            output.name = "output";
            softmax.setLayer(output);

        } catch (Exception ex) {
        }

        try {
            ReluFunction relu = new ReluFunction(g.read());
            hidden = new HiddenLayer(numHidden, relu, numDense);
            hidden.setNeuralNet(this);
            hidden.nextLayer = output;
            hidden.name = "hidden";
        } catch (Exception ex) {
        }

        try {
            ReluFunction relu = new ReluFunction(g.read());
            dense = new HiddenLayer(numHidden, relu, numInputs);
            dense.setNeuralNet(this);
            dense.nextLayer = dropout;
            dense.name = "dense";
        } catch (Exception ex) {
        }

        try {
            ReluFunction relu = new ReluFunction(g.read());
            dropout = new HiddenLayer(numDropout, relu, numHidden);
            dropout.setNeuralNet(this);
            dropout.nextLayer = hidden;
            dropout.name = "dropout";
        } catch (Exception ex) {
        }




        IActivationFunction iaf2 = ActivationFactory.create(g.read());
        try {
            ReluFunction relu = new ReluFunction(g.read());
            input = new InputLayer(numInputs, relu, numInputs);
            input.setNeuralNet(this);
            input.name = "input";
            input.nextLayer = dense;
        } catch (Exception ex) {
        }

        try {
            input.previousLayer = null;
            dense.previousLayer = input;
            dropout.previousLayer = dense;
            hidden.previousLayer = dropout;
            output.previousLayer = hidden;

            input.init();
            dense.init();
            dropout.init();
            hidden.init();
            output.init();
        } catch (Exception ex) {
            Log.info("Unable to build Neural Network:" + ex);
        }

    }


}