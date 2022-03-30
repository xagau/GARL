package garl.iaf;

import garl.ann.NeuralLayer;

public class NegateFunction implements IActivationFunction {
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
