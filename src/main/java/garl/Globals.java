package garl;

import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;

public class Globals {

    public static Obstacle spawn = new Obstacle();
    public static Obstacle control = new Obstacle();

    public static boolean verbose = true;
    public static long threshold = 250;
    public static Semaphore semaphore = new Semaphore(1);

    public static BufferedImage img = null;
    public static boolean debug = false;

}

