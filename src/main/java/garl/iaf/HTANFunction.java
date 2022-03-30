package garl.iaf;

import garl.ann.NeuralLayer;

public class HTANFunction implements IActivationFunction {
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
