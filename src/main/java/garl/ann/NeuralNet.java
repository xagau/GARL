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
import garl.*;
import garl.iaf.ActivationFactory;
import garl.iaf.IActivationFunction;
import garl.iaf.ReluFunction;
import garl.iaf.SoftmaxFunction;


public class NeuralNet {
    public InputLayer input = null;
    public HiddenLayer dense = null;
    public HiddenLayer hidden = null;
    public HiddenLayer dropout = null;
    public OutputLayer output = null;
    public Genome owner = null;

    public NeuralNet(Genome g) {
        owner = g;
        g.jump(Settings.GENOME_LENGTH);
        int numInputs = Settings.NUMBER_OF_INPUTS;
        int numDense = Settings.MAX_NEURONS; //, Math.max(8, (int) g.read(Gene.DENSE)) );
        int numHidden = Settings.MAX_NEURONS; //, Math.max(8,(int) g.read(Gene.HIDDEN)) );
        int numDropout = Settings.MAX_DROPOUT;

        SoftmaxFunction softmax = new SoftmaxFunction(Action.values().length);
        try {
            output = new OutputLayer(1, softmax, numHidden);
            output.setNeuralNet(this);
            output.name = "output";
            softmax.setLayer(output);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            ReluFunction relu = new ReluFunction(g.read());
            hidden = new HiddenLayer(numHidden, relu, numDense);
            hidden.setNeuralNet(this);
            hidden.nextLayer = output;
            hidden.name = "hidden";
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            ReluFunction relu = new ReluFunction(g.read());
            dense = new HiddenLayer(numHidden, relu, numInputs);
            dense.setNeuralNet(this);
            dense.nextLayer = dropout;
            dense.name = "dense";
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            ReluFunction relu = new ReluFunction(g.read());
            dropout = new HiddenLayer(numDropout, relu, numHidden);
            dropout.setNeuralNet(this);
            dropout.nextLayer = hidden;
            dropout.name = "dropout";
        } catch (Exception ex) {
            ex.printStackTrace();
        }




        try {
            ReluFunction relu = new ReluFunction(g.read());
            input = new InputLayer(numInputs, relu, numInputs);
            input.setNeuralNet(this);
            input.name = "input";
            input.nextLayer = dense;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            input.previousLayer = null;
            dense.previousLayer = input;
            dropout.previousLayer = dense;
            hidden.previousLayer = dropout;
            output.previousLayer = hidden;

            input.init();
            dense.init();
            dropout.init();
            hidden.init();
            output.init();

            try {
                if(Globals.verbose) {
                    Log.info("input:" + input.neuron.size());
                    Log.info("dense:" + dense.neuron.size());
                    for (int i = 0; i < dense.neuron.size(); i++) {
                        Log.info("dense:" + dense.neuron.get(i).getActivationFunction().getClass().getSimpleName());
                    }
                    Log.info("dropout:" + dropout.neuron.size());
                    for (int i = 0; i < dropout.neuron.size(); i++) {
                        Log.info("dropout:" + dropout.neuron.get(i).getActivationFunction().getClass().getSimpleName());
                    }
                    Log.info("hidden:" + hidden.neuron.size());
                    for (int i = 0; i < hidden.neuron.size(); i++) {
                        Log.info("hidden:" + hidden.neuron.get(i).getActivationFunction().getClass().getSimpleName());
                    }
                    Log.info("output:" + output.neuron.size());
                }
            } catch(Exception ex) {
                Log.info(ex);
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            Log.info("Unable to build Neural Network:" + ex);
            ex.printStackTrace();
        }

    }


}