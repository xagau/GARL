package garl;
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
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

public class Globals {

    public static int minor = 41;
    public static int major = 1;

    public static String title = "Genetic Based Multi-Agent Reinforcement Learning " + major + "." + minor;
    public static boolean screenSaverMode = false;
    public static volatile Obstacle spawn = new Obstacle();
    public static volatile Obstacle control = new Obstacle();

    public static boolean verbose = false;
    public static Semaphore semaphore = new Semaphore(100);

    public static MoneyMQ mq = new MoneyMQ();

    static double increment = 0.00001000;
    final static int FPS = 32;
    public static long threshold = 1000/FPS;
    public static double maxPayout = 5.00;
    public static double minPayout = 0.001;
    public static boolean installed = false;

    public static volatile World world = null;

    public static GARLFrame frame = null;
    public static int cleanupTime = 100;
    public static int thinkTime = 80;
    public static int selectionTime = 50;
    public static int replicationTime = 200;
    public static long HOUR = 1000 * 60 * 60;
    static {




    }

}

