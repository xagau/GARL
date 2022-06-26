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
import java.lang.reflect.Field;

public class ActivationFactory {
        public static IActivationFunction create(int gene) {
            int index = gene % ActivationFunction.values().length;
            Field[] fl = ActivationFunction.class.getDeclaredFields();
            Field f = fl[index];
            ActivationFunction af = ActivationFunction.valueOf(f.getName());

            switch (af) {
                case SIGMOID:
                    Sigmoid sigmoid = new Sigmoid(gene);
                    return sigmoid;
                //case LINEAR:
                //    iaf.Linear linear = new iaf.Linear(gene);
                //    return linear;
                //case STEP:
                //    iaf.StepFunction st = new iaf.StepFunction((double) gene);
                //    return st;
                //case HYPERTAN:
                //    iaf.HTANFunction ht = new iaf.HTANFunction((double) gene);
                //    return ht;
                case RELU:
                    ReluFunction rl = new ReluFunction((double) gene);
                    return rl;
                case SOFTMAX:
                    SoftmaxFunction smx = new SoftmaxFunction((double) gene);
                    return smx;
                //case NEGATE:
                //    iaf.NegateFunction nl = new iaf.NegateFunction((double) gene);
                //    return nl;
                //case LINEAR_COMBINATION:
                //    iaf.LinearCombinationFunction lcb = new iaf.LinearCombinationFunction((double) gene);
                //    return lcb;
                //case SINUSMOID:
                //    iaf.SinusoidFunction sinusmoid = new iaf.SinusoidFunction((double) gene);
                //    return sinusmoid;
                //case COSUSMOID:
                //    iaf.CosusoidFunction cosusmoid = new iaf.CosusoidFunction((double) gene);
                //    return cosusmoid;
                default:
                    ReluFunction smax = new ReluFunction((double) gene);
                    return smax;
            }
        }
    }