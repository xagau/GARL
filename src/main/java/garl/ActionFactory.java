package garl;

import java.lang.reflect.Field;

public class ActionFactory {

    static Action create(double input) {
        int len = Action.class.getDeclaredFields().length;

        double o = (double) (input%1*len);

        int n = (int) Math.round(o);
        try {
            Field[] list = Action.class.getDeclaredFields();
            String name = list[n].getName();
            Action a = Action.valueOf(name);

            return a;
        } catch (Exception e) {
            return Action.COMMIT;
        }
    }

}