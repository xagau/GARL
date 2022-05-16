package garl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {

    public static ArrayList<Entity> create(World world, int individuals, int width, int height) {

        Log.info("Creating population from random");
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        for (int i = 0; i < individuals; i++) {
            try {
                Entity e = new Entity(world);
                e.location.x = rand.nextInt(width);
                e.location.y = rand.nextInt(height);
                if( !world.selection.isTouching(e)) {
                    entities.add(e);
                } else {
                    // HACK. Some will be lost.
                    e.location.x = rand.nextInt(world.width);
                    e.location.y = rand.nextInt(world.height);
                    entities.add(e);
                }
            } catch(Exception ex) {}
        }

        return entities;
    }

    public static ArrayList<Entity> create(World world, ArrayList seedList, int individuals, int width, int height) throws IOException {


        Log.info("Create population from seed list");
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        world.children = 0;

        Comparator<Seed> comparator = new Comparator<Seed>() {
            @Override
            public int compare(Seed e1, Seed e2) {
                if (e1.generation == e2.generation) {
                    return 0;
                } else if (e1.generation < e2.generation) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };
        try {
            Collections.sort(seedList, comparator);
        } catch (Exception ex) {
            Log.info(ex);
            ex.printStackTrace();
        }

        individuals = Settings.STARTING_POPULATION; // ? , individuals);

        String fileName = "./genomes/" + System.currentTimeMillis() + "-" + world.epoch + "-epoch.json";
        Log.info(fileName);
        FileWriter writer = new FileWriter(new File(fileName));
        writer.write("[");
        for (int i = 0; i <= individuals; i++) {
            try {
                if( i < seedList.size() ) {
                    Seed seed = (Seed) seedList.get(i);

                    Entity e = new Entity(world);
                    e.genome = new Genome(seed.genome);
                    e.generation = seed.generation;
                    e.epoch = world.epoch;
                    e.brain = new Brain(e.genome);
                    e.location.x = rand.nextInt(world.width);
                    e.location.y = rand.nextInt(world.height);


                    //Entity rep = e.replicate();
                    e.genome.setOwner(e);
                    if( !world.selection.isTouching(e)) {
                        entities.add(e);
                    } else {
                        // HACK. Some will be lost.
                        e.location.x = rand.nextInt(world.width);
                        e.location.y = rand.nextInt(world.height);
                        entities.add(e);
                    }
                    if (Globals.verbose) {
                        writer.write("{ \"epoch\":" + world.epoch + ", \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                        if (i + 1 != individuals) {
                            writer.write(",\n");
                        }
                    }
                }
            } catch (Exception ex) {
                Log.info("Exception:" +ex);
                if(Globals.verbose) {
                    ex.printStackTrace();
                }
            }

        }

        /*
        Log.info("Adjusted Individuals:" + adjustedIndividuals);

        if (adjustedIndividuals > 0) {
            for (int i = 0; i < adjustedIndividuals; i++) {
                try {
                    Entity e = new Entity(world);

                    e.location.x = rand.nextInt(world.width);
                    e.location.y = rand.nextInt(world.height);
                    if( !world.selection.isTouching(e)) {
                        entities.add(e);
                    } else {
                        // HACK. Some will be lost.
                        e.location.x = rand.nextInt(world.width);
                        e.location.y = rand.nextInt(world.height);
                        entities.add(e);
                    }

                    if (Globals.verbose) {
                        writer.write("{ \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                        if (i + 1 != adjustedIndividuals) {
                            writer.write(",\n");
                        }
                    }
                } catch (Exception ex) {
                    Log.info("Exception:" + ex);
                    ex.printStackTrace();
                }
            }
        }

         */
        writer.flush();
        writer.write("]");
        writer.flush();
        writer.close();

        Log.info("Entities added (H):" + entities.size());

        return entities;
    }
}

