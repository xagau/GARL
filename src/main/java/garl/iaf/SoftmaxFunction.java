package garl.iaf;

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
import garl.Log;
import garl.ann.NeuralLayer;
import garl.ann.Neuron;

import java.util.ArrayList;

public class SoftmaxFunction implements IActivationFunction {

    private double max = 1.0;

    public SoftmaxFunction(double max) {
        this.max = max;
    }

    NeuralLayer layer = null;
    public void setLayer(NeuralLayer layer) {
        this.layer = layer;
    }
    @Override
    public double calc(double netInput) {
        double totalLayerInput = 0;
        // add max here for numerical stability - find max netInput for all neurons in this layer
        double max = netInput;

        if (layer == null) {
            Log.info("Layer is null:");
            return netInput;
        }
        ArrayList<Neuron> list = layer.neuron;
        for (int i = 0; i < list.size(); i++) {
            Neuron neuron = (Neuron) list.get(i);
            totalLayerInput += Math.exp(neuron.getOutput() - max);
        }

        double output = Math.exp(netInput - max) / totalLayerInput;
        if( new Double(output).isNaN() ){
            return 0;
        }
        return output;
    }

}

