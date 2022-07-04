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

import java.lang.reflect.Field;

public class ActivationFactory {
        public static IActivationFunction create(int gene, NeuralLayer layer) {
            int index = gene % ActivationFunction.values().length;
            Field[] fl = ActivationFunction.class.getDeclaredFields();
            Field f = fl[index];
            ActivationFunction af = ActivationFunction.valueOf(f.getName());

            switch (af) {
                case SIGMOID:
                    Sigmoid sigmoid = new Sigmoid(gene);
                    sigmoid.setLayer(layer);
                    return sigmoid;
                case LINEAR:
                    Linear linear = new Linear(gene);
                    linear.setLayer(layer);
                    return linear;
                case STEP:
                    StepFunction st = new StepFunction((double) gene);
                    st.setLayer(layer);
                    return st;
                case HYPERTAN:
                    HTANFunction ht = new HTANFunction((double) gene);
                    ht.setLayer(layer);
                    return ht;
                case RELU:
                    ReluFunction rl = new ReluFunction((double) gene);
                    rl.setLayer(layer);
                    return rl;
                case LEAKY_RELU:
                    LeakyReluFunction lrl = new LeakyReluFunction((double) gene);
                    lrl.setLayer(layer);
                    return lrl;
                case SOFTMAX:
                    SoftmaxFunction smx = new SoftmaxFunction((double) gene);
                    smx.setLayer(layer);
                    return smx;
                case NEGATE:
                    NegateFunction nl = new NegateFunction((double) gene);
                    nl.setLayer(layer);
                    return nl;
                case LINEAR_COMBINATION:
                    LinearCombinationFunction lcb = new LinearCombinationFunction((double) gene);
                    lcb.setLayer(layer);
                    return lcb;
                case SINUSMOID:
                    SinusoidFunction sinusmoid = new SinusoidFunction((double) gene);
                    sinusmoid.setLayer(layer);
                    return sinusmoid;
                case COSUSMOID:
                    CosusoidFunction cosusmoid = new CosusoidFunction((double) gene);
                    cosusmoid.setLayer(layer);
                    return cosusmoid;
                default:
                    ReluFunction smax = new ReluFunction((double) gene);
                    smax.setLayer(layer);
                    return smax;
            }
        }
    }