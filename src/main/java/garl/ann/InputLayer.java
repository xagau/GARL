package garl.ann;

import garl.iaf.IActivationFunction;

public class InputLayer extends NeuralLayer {
    public InputLayer(int numberofneurons, IActivationFunction iaf, int numberofinputs) {
        this.numberOfInputs = numberofinputs;
        this.numberOfNeuronsInLayer = numberofneurons;
        this.activationFnc = iaf;
    }
}

