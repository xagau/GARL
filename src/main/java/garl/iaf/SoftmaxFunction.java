package garl.iaf;

import garl.ann.NeuralLayer;
import garl.ann.Neuron;

import java.util.ArrayList;

public class SoftmaxFunction implements IActivationFunction {

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

