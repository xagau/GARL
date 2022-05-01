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
            Entity e = new Entity(world);
            e.location.x = rand.nextInt(width);
            e.location.y = rand.nextInt(height);
            entities.add(e);
        }

        return entities;
    }

    public static ArrayList<Entity> create(World world, ArrayList seedList, int individuals, int width, int height) throws IOException {


        Log.info("Create population from seed list");
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        world.children = 0;

        Comparator<Entity> comparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
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
            ex.printStackTrace();
        }

        int adjustedIndividuals = individuals - seedList.size();

        String fileName = "./genomes/" + System.currentTimeMillis() + "-" + world.epoch + "-epoch.json";
        Log.info(fileName);
        FileWriter writer = new FileWriter(new File(fileName));
        writer.write("[");
        for (int i = 0; i < individuals; i++) {
            try {
                Entity seed = (Entity) seedList.get(i);
                Entity e = seed.replicate();
                e.location.x = rand.nextInt(width);
                e.location.y = rand.nextInt(height);
                entities.add(e);
                if (Globals.verbose) {
                    writer.write("{ \"epoch\":" + world.epoch + ", \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                    if (i + 1 != individuals) {
                        writer.write(",\n");
                    }
                }
            } catch (Exception ex) {
            }

        }


        if (adjustedIndividuals > 0) {
            for (int i = 0; i < adjustedIndividuals; i++) {
                try {
                    Entity e = new Entity(world);
                    e.location.x = rand.nextInt(width);
                    e.location.y = rand.nextInt(height);
                    entities.add(e);
                    if (Globals.verbose) {
                        writer.write("{ \"generation\":" + e.generation + ", \"position\":" + i + ", \"genome\": \"" + e.genome.code + "\"}");
                        if (i + 1 != adjustedIndividuals) {
                            writer.write(",\n");
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
        writer.flush();
        writer.write("]");
        writer.flush();
        writer.close();

        return entities;
    }
}

