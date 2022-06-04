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

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Utility {
    public synchronized static long checksum(String in) {
        return getCRC32Checksum(in.getBytes());
    }

    public synchronized static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    public synchronized static double flatten(long v, long max) {
        double d = (double) ((double) v % (double) max);
        return d;
    }

    public synchronized static boolean isOdd(int in) {
        return in % 2 == 1;
    }

    public synchronized static char flatten(int c, int max) {
        c = c % max;
        char ch = Long.toHexString((long) c).charAt(0);
        return ch;
    }


    public synchronized static boolean precision(double a, double b, double epsilon)
    {
        //double epsilon = 0.00001d;
        if( Math.abs(a - b) < epsilon  ){
            return true;
        }
        return false;
    }
    public synchronized static double flatten(char c) {

        double i = (c - 'a') / 25d;
        return i;
    }

    public static void main(String[] args) {
        //double c = flatten('o');
        //DecimalFormat df = new DecimalFormat("0.00000000");
        //Log.info(df.format(c));
        //System.out.println(precision(0.00001, 0.00001, 0.00001 ));
        CullingStrategy.cleanup();
    }

    public synchronized static double flatten(double v, double max) {
        v = v % max;
        return v;
    }

    public synchronized static String getMACAddress() {

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();

            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            String macAddress = String.join("-", hexadecimal);
        } catch(Exception ex) {
            if( Globals.verbose ) {
                Log.info("NPE:" + ex.getMessage());
                ex.printStackTrace();
            }
        } finally {
            return "NONE-NO-MAC";
        }
    }

}

