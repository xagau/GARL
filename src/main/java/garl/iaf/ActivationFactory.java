package garl.iaf;

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