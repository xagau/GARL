package garl.iaf;

import garl.ann.NeuralLayer;

public class ReluFunction implements IActivationFunction {
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


