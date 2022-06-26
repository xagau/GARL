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
