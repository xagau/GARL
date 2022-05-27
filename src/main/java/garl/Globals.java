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

    public static int minimum = 1;
    public static int major = 37;

    public static String title = "Genetic Based Multi-Agent Reinforcement Learning " + minimum + "." + major;
    public static boolean screenSaverMode = false;
    public static Obstacle spawn = new Obstacle();
    public static Obstacle control = new Obstacle();

    public static boolean verbose = false;
    public static long threshold = 250;
    public static Semaphore semaphore = new Semaphore(1);

    public static MoneyMQ mq = new MoneyMQ();

    public static BufferedImage img = null;
    public static boolean debug = false;
    final static int FPS = 32;
    public static double maxPayout = 5.00;
    public static boolean installed = false;

    public static World world = null;

    public static GARLFrame frame = null;
    public static int cleanupTime = 100;
    public static int taskTime = 120;
    public static int selectionTime = 90;
    public static long HOUR = 1000 * 60 * 60;
    static {




    }

}

