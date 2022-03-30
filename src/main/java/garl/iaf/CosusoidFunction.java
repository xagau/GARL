package garl.iaf;

import garl.ann.NeuralLayer;

public class CosusoidFunction implements IActivationFunction {
    double gene = 0;

    public CosusoidFunction(double gene) {
        this.gene = gene;
    }

    public void setLayer(NeuralLayer layer) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calc(double summedInput) {
        return Math.cos(summedInput + gene);
    }

    public double derivative(double net) {
        return Math.cos(net + gene);
    }

}
