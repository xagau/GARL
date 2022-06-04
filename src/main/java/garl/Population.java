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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population {

    public static ArrayList<Entity> create(World world, int individuals) {

        Log.info("Creating population from random");
        ArrayList<Entity> entities = new ArrayList<>();
        Random rand = new Random();
        world.impact = 0;
        Log.info("Add Individuals:");
        Log.info("World Width:" + world.width);
        Log.info("World Height:" + world.height);

        for (int i = 0; i < individuals; i++) {
            try {
                Log.info("Add Individuals: (" + i + ")");
                Entity e = new Entity(world);
                if( !Globals.screenSaverMode ){
                    e.location.x = rand.nextInt(world.width - Settings.INSPECTOR_WIDTH);
                    e.location.y = rand.nextInt(world.height);
                } else {
                    e.location.x = rand.nextInt(world.width);
                    e.location.y = rand.nextInt(world.height);
                }
                boolean okay = true;
                do {
                    if (!world.selection.isTouching(e)) {
                        entities.add(e);
                        okay = false;
                        break;
                    } else {

                        // HACK. Some will be lost.
                        if( !Globals.screenSaverMode ){
                            e.location.x = rand.nextInt(world.width - Settings.INSPECTOR_WIDTH);
                            e.location.y = rand.nextInt(world.height);
                        } else {
                            e.location.x = rand.nextInt(world.width);
                            e.location.y = rand.nextInt(world.height);
                        }
                    }
                } while(okay);
            } catch(Exception ex) {
                Log.info(ex.getMessage());
                ex.printStackTrace();
            }
        }

        Log.info("Size from random:" + entities.size() + " individuals: " + individuals);


        return entities;
    }

    public static ArrayList<Entity> create(World world, ArrayList seedList) throws IOException {


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

        int individuals = Settings.STARTING_POPULATION; // ? , individuals);

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

