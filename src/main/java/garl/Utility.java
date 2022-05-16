package garl;

import garl.iaf.Sigmoid;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
        //System.out.println(precision(0.00001, 0.00001, 0.00001 ));
        cleanup();
    }

    public static double flatten(double v, double max) {
        v = v % max;
        return v;
    }

    public static String getMACAddress() {

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
            Log.info(ex);
        } finally {
            return "NONE-NO-MAC";
        }
    }

    public static void cleanup() {

        String genomePath = Property.getProperty("settings.genomes");
        try {
            File dir = new File(genomePath);
            boolean f = dir.isDirectory();
            File[] list = dir.listFiles();

            ArrayList<Seed> slist = SeedLoader.load();
            if(slist.isEmpty()){
                return;
            }
            int limit = Settings.MAX_ENTITIES;
            int count = 0;
            Log.info(f + " " + genomePath + " " + list.length);
            Log.info(f + " " + genomePath + " " + slist.size());
            for(int i = 0; i < list.length; i++ ) {
                String name = list[i].getName();
                for(int k = 0; k < slist.size(); k++){
                    if(!slist.get(k).seedName.equals(name)){
                        if( count++ > limit ) {
                            // TODO Filter by fitness.
                            list[i].delete();
                        }
                    }
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            Log.info(ex);
        } catch(Error e){
            e.printStackTrace();
            Log.info(e);
        }

    }


}

