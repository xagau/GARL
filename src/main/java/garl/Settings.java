package garl;

import java.util.Base64;

public class Settings {
    public static int INSPECTOR_WIDTH = 420;
    public static double ACCELERATION = 1.0;
    public static int CHAR_SET = 62;
    public static int GENOME_LENGTH = 32;
    public static int STARTING_POPULATION = 100;
    public static int MAX_OFFSPRING = 2;
    public static boolean NATURAL_REPLICATION = true;
    public static int MAX_THINK_DEPTH = 8;
    public static int NUMBER_OF_INPUTS = 32; //STARTING_POPULATION * 12; // garl.Action.values().length;
    public static int DEATH_MULTIPLIER = 25;
    public static int GENE_POOL = 2;
    public static int MAX_SIZE = 18;
    public static int MIN_SIZE = 5;
    public static int MAX_NEURONS = 4;
    public static String PAYOUT_ADDRESS = "";
    public static String RABBIT_ADDRESS = "";


    public static int CELL_MOVEMENT = 1;
    public static int MAX_SPEED = 6;
    public static int MAX_POPULATION = 500;

    public static double ENERGY = 15.0 ;
    public static double ENERGY_STEP_COST = 0.00001;
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
    }

    public static void main(String[] args){
        Log.info(Settings.RABBIT_ADDRESS);
    }
}

