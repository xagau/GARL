package garl.ann;

import garl.Action;
import garl.Utility;
import garl.iaf.IActivationFunction;
import garl.iaf.ReluFunction;
import garl.iaf.SoftmaxFunction;
import garl.Log;

import java.util.ArrayList;

public abstract class NeuralLayer {
    public NeuralNet owner;
    public String name;
    public int numberOfNeuronsInLayer;
    public ArrayList<Neuron> neuron = new ArrayList<>();
    public IActivationFunction activationFnc;
    public NeuralLayer previousLayer;
    public NeuralLayer nextLayer;
    public ArrayList<Double> input = new ArrayList<Double>();
    public ArrayList<Double> output = new ArrayList<Double>();
    protected int numberOfInputs;

    protected void init() {


        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            if (name.equals("output")) {
                SoftmaxFunction sf = new SoftmaxFunction(Action.values().length);
                sf.setLayer(this);
                activationFnc = sf;

            } else {
                activationFnc = new ReluFunction(owner.owner.read(owner.owner.index()));
            }
            owner.owner.advance();
            double bias = Utility.flatten(owner.owner.read(owner.owner.index()));
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

    public void calc() {
        if (neuralLayerDebug) {
            Log.info(this.name + " calc() " + numberOfNeuronsInLayer);
        }
        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            if (previousLayer != null) {
                if (neuralLayerDebug) {
                    Log.info("input-" + name + ":" + previousLayer.input);
                }
                neuron.get(i).setInputs(previousLayer.input);
                neuron.get(i).calc();
            } else {
                if (neuralLayerDebug) {
                    Log.info("input-" + name + ":" + input);
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
                Log.info("Compute next Layer:");
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


