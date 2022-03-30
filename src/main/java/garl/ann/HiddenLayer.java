package garl.ann;

import garl.iaf.IActivationFunction;

public class HiddenLayer extends NeuralLayer {
    public HiddenLayer(int numberofneurons, IActivationFunction iaf, int numberofinputs) {
        this.numberOfInputs = numberofinputs;
        this.numberOfNeuronsInLayer = numberofneurons;
        this.activationFnc = iaf;
    }
}
