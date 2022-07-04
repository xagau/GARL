package garl.ann;

/** Copyright (c) 2019-2022 placeh.io,
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author xagau
 * @email seanbeecroft@gmail.com
 *
 */
import garl.Action;
import garl.Utility;
import garl.iaf.*;
import garl.Log;

import java.util.ArrayList;

public abstract class NeuralLayer {
    public NeuralNet owner;
    public String name;
    public int numberOfNeuronsInLayer;
    public ArrayList<Neuron> neuron = new ArrayList<>();
    public IActivationFunction activationFnc;
    public NeuralLayer previousLayer;
    public NeuralLayer nextLayer;
    public ArrayList<Double> input = new ArrayList<Double>();
    public ArrayList<Double> output = new ArrayList<Double>();
    protected int numberOfInputs;

    protected void init() {


        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            if (name.equals("output")) {
                SoftmaxFunction sf = new SoftmaxFunction(Action.values().length);
                sf.setLayer(this);
                activationFnc = sf;

            } else {
                activationFnc = new ReluFunction(owner.owner.read());
            }
            owner.owner.advance();
            double bias = Utility.flatten(owner.owner.read());
            if( neuron != null && !neuron.isEmpty() && i < neuron.size()) {
                Neuron n = neuron.get(i);
                try {
                    IActivationFunction af = ActivationFactory.create((int)owner.owner.read(), this);
                    if( af instanceof SoftmaxFunction ){
                        ((SoftmaxFunction) af).setLayer(this);
                    }
                    n.setActivationFunction(af);

                    n.bias = bias;
                    n.init(false, this);
                } catch (IndexOutOfBoundsException iobe) {
                    IActivationFunction af = ActivationFactory.create((int)owner.owner.read(), this);
                    if( af instanceof SoftmaxFunction ){
                        ((SoftmaxFunction) af).setLayer(this);
                    }
                    Neuron nn = new Neuron(numberOfInputs, af, bias, this);
                    neuron.add(nn);
                    nn.init(false, this);
                }
            } else {
                if( neuron != null ){
                    IActivationFunction af = ActivationFactory.create((int)owner.owner.read(), this);
                    if( af instanceof SoftmaxFunction ){
                        ((SoftmaxFunction) af).setLayer(this);
                    }
                    Neuron nn = new Neuron(numberOfInputs, af, bias, this);
                    neuron.add(nn);
                    nn.init(false, this);
                } else {
                    Log.info(name + " layer neuron array is null");
                }
            }

        }
    }

    boolean neuralLayerDebug = false;

    public void calc() {
        if (neuralLayerDebug) {
            Log.info(this.name + " calc() " + numberOfNeuronsInLayer);
        }
        for (int i = 0; i < numberOfNeuronsInLayer; i++) {
            if(neuron != null && i < neuron.size()) {
                Neuron n = neuron.get(i);

                if (previousLayer != null) {
                    if (neuralLayerDebug) {
                        Log.info("input-" + name + ":" + previousLayer.input);
                    }
                    n.setInputs(previousLayer.input);
                    n.calc();

                } else {
                    if (neuralLayerDebug) {
                        Log.info("input-" + name + ":" + input);
                    }
                    n.setInputs(input);
                    n.calc();
                }
                try {
                    output.set(i, n.getOutput());
                } catch (IndexOutOfBoundsException iobe) {
                    output.add(n.getOutput());
                }
            }
        }
        if (nextLayer != null) {
            if (neuralLayerDebug) {
                Log.info("Compute next Layer:");
            }
            nextLayer.input = output;
            nextLayer.calc();
        }
    }

    public void setNeuralNet(NeuralNet net) {
        owner = net;
    }

    public NeuralNet getNeuralNet() {
        return owner;
    }
}


