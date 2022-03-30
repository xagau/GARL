package garl.iaf;

import garl.ann.NeuralLayer;

/**
 * iaf.Linear combination activation function implementation, the output unit is
 * simply the weighted sum of its inputs plus a bias term.
 */
public class LinearCombinationFunction implements IActivationFunction {

    /**
     * Bias value
     */
    private double bias = 0;

    LinearCombinationFunction(double bias) {
        this.bias = bias;
    }

    public void setLayer(NeuralLayer layer) {
        setLayer(layer);
    }

    @Override
    public double calc(double summedInput) {
        return summedInput + bias;
    }

    public double derivative(double totalInput) {
        // TODO Auto-generated method stub
        return 0;
    }

}
