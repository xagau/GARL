package garl.iaf;

import garl.ann.NeuralLayer;

public class StepFunction implements IActivationFunction {

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

