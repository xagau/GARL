package garl.iaf;

import garl.ann.NeuralLayer;

public class Sigmoid implements IActivationFunction {
    private double a = 1.0;

    public void setLayer(NeuralLayer layer) {

    }

    public Sigmoid(double _a) {
        this.a = _a;
    }

    @Override
    public double calc(double x) {
        double b =  1.0 / (1.0 + Math.exp(-a * x));
        if(Double.isNaN(b)){
            return a;
        }
        else {
            return b;
        }
    }
}

