package garl;

public class Settings {
    public static int INSPECTOR_WIDTH = 400;
    public static double ACCELERATION = 1.0;
    public static int CHAR_SET = 62;
    public static int GENOME_LENGTH = 32;
    public static int STARTING_POPULATION = 100;
    public static int MAX_OFFSPRING = 2;
    public static boolean NATURAL_REPLICATION = true;
    public static int MAX_THINK_DEPTH = 4;
    public static int NUMBER_OF_INPUTS = 32; //STARTING_POPULATION * 12; // garl.Action.values().length;
    public static int DEATH_MULTIPLIER = 25;
    public static int GENE_POOL = 2;
    public static int MAX_SIZE = 18;
    public static int MIN_SIZE = 5;
    public static int MAX_NEURONS = 4;
    public static String PAYOUT_ADDRESS = "";


    public static int CELL_MOVEMENT = 1;
    public static int MAX_SPEED = 6;
    public static int MAX_POPULATION = 500;

    public static double ENERGY = 15.0 ;
    public static double ENERGY_STEP_COST = 0.00001;
    public static double ENERGY_STEP_SLEEP_COST = 0.02;
}

