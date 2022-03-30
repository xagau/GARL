package garl;

import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

public class Globals {

    static Obstacle spawn = new Obstacle();
    static Obstacle control = new Obstacle();

    static boolean verbose = true;
    static long threshold = 250;
    static Semaphore semaphore = new Semaphore(1);

    public static BufferedImage img = null;
    static boolean debug = false;

}

