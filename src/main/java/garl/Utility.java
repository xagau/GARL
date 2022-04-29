package garl;

import garl.iaf.Sigmoid;

import java.text.DecimalFormat;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Utility {
    public static long checksum(String in) {
        return getCRC32Checksum(in.getBytes());
    }

    public static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    public static double flatten(long v, long max) {
        double d = (double) ((double) v % (double) max);
        return d;
    }

    public static boolean isOdd(int in) {
        return in % 2 == 1;
    }

    public static char flatten(int c, int max) {
        c = c % max;
        char ch = Long.toHexString((long) c).charAt(0);
        return ch;
    }


    public static boolean precision(double a, double b, double epsilon)
    {
        //double epsilon = 0.00001d;
        if( Math.abs(a - b) < epsilon  ){
            return true;
        }
        return false;
    }
    public static double flatten(char c) {

        double i = (c - 'a') / 25d;
        return i;
    }

    public static void main(String[] args) {
        //double c = flatten('o');
        //DecimalFormat df = new DecimalFormat("0.00000000");
        //Log.info(df.format(c));
        System.out.println(precision(0.00001, 0.00001, 0.00001 ));
    }

    public static double flatten(double v, double max) {
        v = v % max;
        return v;
    }

}

