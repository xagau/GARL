package garl.iaf;

import garl.ann.NeuralLayer;

public class SinusoidFunction implements IActivationFunction {

    double gene = 0;

    public SinusoidFunction(double gene) {
        this.gene = gene;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calc(double summedInput) {
        return Math.sin(summedInput + gene);
    }


    public void setLayer(NeuralLayer layer) {
    }

    public double derivative(double net) {
        return Math.cos(net + gene);
    }

}



