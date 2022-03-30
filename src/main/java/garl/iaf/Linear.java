package garl.iaf;

import garl.ann.NeuralLayer;

/**
 * This class represents the pure linear activation function, implementing the
 * interface iaf.IActivationFunction
 *
 * @author Alan de Souza, Fábio Soares, Sean Beecroft
 * @version 0.2
 */
public class Linear implements IActivationFunction {
    private double a = 1.0;

    public Linear() {

    }

    public void setLayer(NeuralLayer layer) {

    }


    public Linear(double value) {
        this.setA(value);
    }

    public void setA(double value) {
        this.a = value;
    }

    @Override
    public double calc(double x) {
        return a * x;
    }

    public double derivative(double x) {
        return a;
    }
}
