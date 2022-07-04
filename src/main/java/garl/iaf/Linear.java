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
 * This class represents the pure linear activation function, implementing the
 * interface iaf.IActivationFunction
 *
 * @author Alan de Souza, Fábio Soares, Sean Beecroft
 * @version 0.2
 */
public class Linear implements IActivationFunction {
    private double a = 1.0;

    public Linear() {

    }

    NeuralLayer layer = null;
    public void setLayer(NeuralLayer layer) {
        this.layer = layer;
    }


    public Linear(double value) {
        this.setA(value);
    }

    public void setA(double value) {
        this.a = value;
    }

    @Override
    public double calc(double x) {
        return a * x;
    }

    public double derivative(double x) {
        return a;
    }
}
