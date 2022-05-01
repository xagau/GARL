package garl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a wrapper class for logging.
 *
 * @author xagau
 * @version 1.4
 */
public class Log {
    private static Property pm = new Property();
    private static boolean debug = false;
    private static boolean log = false;
    private static boolean payment = true;

    static {
        try {
            debug = pm.getProperty("logger.debug") == null ? false : Boolean.parseBoolean(pm.getProperty("logger.debug"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            log = pm.getProperty("logger.verbose") == null ? false : Boolean.parseBoolean(pm.getProperty("logger.verbose"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            payment = pm.getProperty("logger.payment") == null ? false : Boolean.parseBoolean(pm.getProperty("logger.payment"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Suppress the default public constructor to prevent instances of this
     * class.
     */
    private Log() {}

    /**
     * garl.Log the text to the logger.
     *
     * @param ex the text to be logged
     */
    public static void info(Exception ex) {
        if(Globals.verbose ) {
            ex.printStackTrace();
        }
        info(ex.toString(), Level.ALL);
    }

    /**
     * garl.Log the text to the logger.
     *
     */
    public static void info() {
        info("\n", Level.ALL);
    }
    public static void info(String text) {
        info(text, Level.ALL);
    }
    public static void info(int num) {
        info("" + num, Level.ALL);
    }

    /**
     * This method will allow the logger to be run in debug mode (output) or in
     * regular mode (no output). You can specify the level, with Level
     *
     * @see Level
     *
     * @param text the text to be logged
     * @param level the log level
     */
    static DecimalFormat df = new DecimalFormat("0.00");
    static SimpleDateFormat sdf =new SimpleDateFormat("YYYYMMDD'T'HHmmSS");

    public static void info(String text, Level level) {
        if (isDebug()) {
            Logger.getAnonymousLogger().info(text + "\n");
        }

        try {
            try {

                long fm = Runtime.getRuntime().freeMemory();
                long tm = Runtime.getRuntime().totalMemory();
                double dfm = (double)fm;
                double dtm = (double)tm;

                double G = 10000000;
                Date dt = new Date(System.currentTimeMillis());
                String logline = df.format(fm/G) + "G/" + df.format(tm/G) + "G:" + sdf.format(dt) + ":L:" + text + "\n";
                System.out.println(logline);

                if( log ) {
                    FileWriter logger = new FileWriter(new File("./output.log"), true);
                    BufferedWriter bw = new BufferedWriter(logger);
                    bw.write(logline + "\n");
                    bw.flush();
                    //Closing BufferedWriter Stream
                    bw.close();
                }
            } catch (Exception ex) {ex.printStackTrace();}
        } catch (Error err) {err.printStackTrace();}
    }

    public static void payment(String text, Level level) {
        if (isDebug()) {
            Logger.getAnonymousLogger().info(text + "\n");
        }

        try {
            try {

                long fm = Runtime.getRuntime().freeMemory();
                long tm = Runtime.getRuntime().totalMemory();
                double dfm = (double)fm;
                double dtm = (double)tm;

                double G = 10000000;
                Date dt = new Date(System.currentTimeMillis());
                String logline = df.format(fm/G) + "G/" + df.format(tm/G) + "G:" + sdf.format(dt) + ":L:" + text + "\n";
                System.out.println(logline);

                if( payment ) {
                    FileWriter logger = new FileWriter(new File("./output.log"), true);
                    BufferedWriter bw = new BufferedWriter(logger);
                    bw.write(logline + "\n");
                    bw.flush();
                    //Closing BufferedWriter Stream
                    bw.close();
                }
            } catch (Exception ex) {ex.printStackTrace();}
        } catch (Error err) {err.printStackTrace();}
    }
    /**
     * @return true if debug mode set to true, false if not.
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @param debug a boolean value, true to set debug mode on, to turn debug
     * off use false.
     */
    public static void setDebug(boolean debug) {
        Log.debug = debug;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
