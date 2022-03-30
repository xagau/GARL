package garl;

import java.awt.*;

public class Obstacle extends Rectangle {
    boolean kill = true;
    boolean spawner = false;
    boolean control = false;
    Color color = Color.pink;
    String name = "wall";

    public String getName() {
        if( spawner ){
            return "spawner";
        } else if( control ){
            return "control";
        } else {
            return name;
        }
    }

    Color getColor() {
        if (spawner) {
            return Color.green;
        } else if (control) {
            return Color.magenta;
        }
        return color;
    }

}
