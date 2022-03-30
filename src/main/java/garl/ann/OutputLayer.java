package garl.ann;

import garl.iaf.IActivationFunction;

public class OutputLayer extends NeuralLayer {
    public OutputLayer(int numberofneurons, IActivationFunction iaf,
                       int numberofinputs) {
        this.numberOfInputs = numberofinputs;
        this.numberOfNeuronsInLayer = numberofneurons;
        this.activationFnc = iaf;
    }

    public IActivationFunction getActivationFunction() {
        return activationFnc;
    }
}
