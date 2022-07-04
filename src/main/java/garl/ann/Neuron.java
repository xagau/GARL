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
import garl.Globals;
import garl.Log;
import garl.Utility;
import garl.iaf.IActivationFunction;

import java.util.ArrayList;
import java.util.Random;

public class Neuron {
    protected ArrayList<Double> weight = new ArrayList<>();
    private ArrayList<Double> input = new ArrayList<>();
    private Double output = 0d;
    private Double outputBeforeActivation = 0d;
    private int numberOfInputs = 0;
    protected Double bias = 0d; // get from gene? OR TD(0)?
    private IActivationFunction activationFunction;
    private NeuralLayer layer = null;

    public Neuron(int numberofinputs, IActivationFunction iaf, Double bias, NeuralLayer parent) {
        this.bias = bias;
        if(this.bias.isNaN()){
            this.bias = Math.random();
        }
        if( parent != null ) {
            layer = parent;
        } else {
            Log.info("Parent layer is null for " );
        }
        numberOfInputs = numberofinputs;
        weight = new ArrayList<>(numberofinputs + 1);
        input = new ArrayList<>(numberofinputs);
        activationFunction = iaf;
    }

    public void setActivationFunction(IActivationFunction iaf) {
        activationFunction = iaf;
    }

    public IActivationFunction getActivationFunction(){
        return activationFunction;
    }

    public void init(boolean random, NeuralLayer layer) {
        if( random ) {
            Random rand = new Random();
            for (int i = 0; i <= numberOfInputs; i++) {
                double newWeight = rand.nextDouble();
                try {
                    this.weight.set(i, newWeight);
                } catch (IndexOutOfBoundsException iobe) {
                    this.weight.add(newWeight);
                }
            }
        } else {
            for (int i = 0; i <= numberOfInputs; i++) {
                double newWeight = Math.random();
                try {

                    newWeight = Utility.flatten(this.layer.owner.owner.read());
                    this.weight.set(i, newWeight);
                } catch (IndexOutOfBoundsException iobe) {
                    Globals.neuronIobe++;
                    this.weight.add(newWeight);
                }
            }
        }
    }

    public void calc() {

        outputBeforeActivation = 0.0;
        if (numberOfInputs > 0) {
            if (input != null && weight != null) {
                for (int i = 0; i <= numberOfInputs; i++) {
                    double v = 0;
                    try {
                        v = (i == numberOfInputs ? bias : input.get(i));
                    } catch (Exception ex) {
                        v = bias;
                    }
                    outputBeforeActivation += v * weight.get(i);
                }
            }
        }
        output = activationFunction.calc(outputBeforeActivation);
    }

    public void setInputs(ArrayList<Double> input) {
        this.input = new ArrayList<Double>();
        this.input.addAll(input);
    }

    public Double getOutput() {
        if( output.isNaN() || output.isInfinite() ){
            return bias;
        }
        return output;
    }

}
