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
import java.util.Base64;

public class Settings {


    public static int INSPECTOR_WIDTH = 420;
    public static double ACCELERATION = 0.3;
    public static int CHAR_SET = 62;
    public static int GENOME_LENGTH = 32;
    public static int STARTING_POPULATION = 50;
    public static int MAX_OFFSPRING = 2;
    public static boolean NATURAL_REPLICATION = true;
    public static int MAX_THINK_DEPTH = 3;
    public static int NUMBER_OF_INPUTS = 256; //STARTING_POPULATION * 12; // garl.Action.values().length;
    public static int DEATH_MULTIPLIER = 25;
    public static int GENE_POOL = 2;
    public static int MAX_SIZE = 19;
    public static int MIN_SIZE = 5;
    public static int MAX_NEURONS = 6;
    public static int MAX_DROPOUT = 2;

    public static String PAYOUT_ADDRESS = "";
    public static String RABBIT_ADDRESS = "";

    public static int MAX_EPOCH = 9000;


    public static int CELL_MOVEMENT = 1;
    public static int MAX_SPEED = 5;
    public static int MAX_POPULATION = 200;

    public static double ENERGY = 15.0 ;
    public static double ENERGY_STEP_COST = 0.0005;
    public static double ENERGY_STEP_SLEEP_COST = 0.02;

    static {
        try {
            MAX_OFFSPRING = Integer.parseInt(Property.getProperty("settings.max_offspring"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            STARTING_POPULATION = Integer.parseInt(Property.getProperty("settings.starting_population"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            MAX_NEURONS = Integer.parseInt(Property.getProperty("settings.max_neurons"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            NUMBER_OF_INPUTS = Integer.parseInt(Property.getProperty("settings.number_of_inputs"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        try {
            NATURAL_REPLICATION = Boolean.parseBoolean(Property.getProperty("settings.natural_replication"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            PAYOUT_ADDRESS = Property.getProperty("settings.payout_address");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            RABBIT_ADDRESS = new String(Base64.getDecoder().decode(Property.getProperty("settings.rabbit_address")));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            Globals.increment = Double.parseDouble(Property.getRemoteProperty("settings.increment"));
        } catch(Exception ex) {
            Globals.increment = Globals.increment;
            ex.printStackTrace();
        }
        try {
            Globals.minPayout = Double.parseDouble(Property.getProperty("settings.min_payout"));
            if( Globals.minPayout == 0 ){
                Globals.minPayout = 0.5;
            }
        } catch(Exception ex) {
            Globals.increment = Globals.increment;
            ex.printStackTrace();
        }

    }

    public static void main(String[] args){
        Log.info(Settings.RABBIT_ADDRESS);
    }
}

