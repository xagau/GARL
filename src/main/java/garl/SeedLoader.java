package garl;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SeedLoader {

    public static ArrayList<Seed> load() throws IOException {
        ArrayList<Seed> list = new ArrayList<Seed>();
        String seed = "./genomes/";
        Gson gson = new Gson(); //null;
        // create a reader
        File dir = new File(seed);

        File[] listFiles = dir.listFiles();
        File[] files = dir.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        int ctr = 0;
        for (int i = files.length - 1; i >= 0; i--) {
            File f = files[i];
            if (f.getName().contains("genome")) {
                String fName = f.getName();
                Log.info("Using GARL Entity: [" + ctr + "] " + f.getName());

                    Reader reader = Files.newBufferedReader(Paths.get(seed + fName));
                    try {
                        Seed lseed = (Seed) gson.fromJson(reader, Seed.class);
                        lseed.seedName = f.getName();
                        list.add(lseed);
                        ctr++;
                    } catch (Exception ex) {
                        Log.info(ex);
                    }
            }
            if (ctr >= Settings.STARTING_POPULATION) {
                break;
            }
        }

        Log.info("list size:" + list.size());


        // CODE BELOW SELECTS MOST FIT INDIVIDUALS BASED ON METRICS - REQUIRES REFACTORING

        /*
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Seed s1 = (Seed)o1;
                Seed s2 = (Seed)o2;
                if( s1.generation>s2.generation){
                    return -1;
                } else if( s1.generation == s2.generation ){
                    return 0;
                }
                return 1;
            }
        };

         */
        /*
        Seed[] arr = new Seed[list.size()];
        for(int i=0; i < list.size(); i++ ){
            try {
                arr[i] = list.get(i);
            } catch(Exception ex){
                Log.info(ex);
                ex.printStackTrace();
            }
        }
        Arrays.sort(arr, comparator);
        for(int i =0; i < arr.length; i++ ){
            if( i > 20){
                break;
            }
            Log.info(arr[i].generation + " " + arr[i].genome);
        }
        list = new ArrayList<>();
        for(int i =0; i < Math.min(Settings.GENOME_PERSISTANCE, arr.length); i++ ){
            list.add(arr[i]);
        }

         */
        return list;
    }

    public static ArrayList<Entity> load(ArrayList<Seed> seeds, World world) throws IOException {
        ArrayList<Seed> list = seeds;
        ArrayList<Entity> ents = new ArrayList<>();

        for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
            if (list.get(i).genome.contains("-")) {
                continue;
            }
            try {
                String genome = list.get(i).genome;
                Log.info("Adding:" + i + ":" + genome);
                Genome g = new Genome(genome);
                Brain brain = new Brain(g);
                Entity e = new Entity(world);
                brain.setOwner(e);
                e.location.x = (int) (Math.random() * world.getWidth());
                e.location.y = (int) (Math.random() * world.getHeight());
                g.setOwner(e);
                e.brain = brain;

                e.genome = g;
                ents.add(e);
                Log.info("Added:" + i + " at " + e.location.x + " " + e.location.y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ents;
    }

}
