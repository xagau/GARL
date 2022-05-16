package garl;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

public class Globals {

    public static boolean screenSaverMode = false;
    public static Obstacle spawn = new Obstacle();
    public static Obstacle control = new Obstacle();

    public static boolean verbose = true;
    public static long threshold = 250;
    public static Semaphore semaphore = new Semaphore(1);

    public static BufferedImage img = null;
    public static boolean debug = false;
    final static int FPS = 32;
    public static double maxPayout = 5.00;
    public static boolean installed = false;

    public static World world = null;

    public static GARLFrame frame = null;
    public static int cleanupTime = 100;
    public static int taskTime = 120;
    public static long HOUR = 1000 * 60 * 60;
    static {




    }

}

