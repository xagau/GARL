package garl;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TimerTask;

public class ThinkTask extends TimerTask {
    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;
    int chunk = 0;

    public ThinkTask(JFrame frame, World world, int width, int height, int chunk) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
        this.chunk = chunk;
    }

    long start = 0;

    @Override
    public void run() {

        start = System.currentTimeMillis();



        try {
            Globals.semaphore.acquire();

            int ctr = 0;
            for (int i = 0; i < world.list.size(); i++) {
                try {
                    ctr++;
                    if( ctr > 20 ){
                        ctr = 0;
                        Runtime.getRuntime().gc();
                    }
                    Entity e = world.list.get(i);
                    if (e.alive) {
                        e.think(world, start);
                    }
                } catch (Exception ex) {
                    Log.info(ex);
                }
            }


            int livingCount = world.getLivingCount();


            if (livingCount <= Settings.GENE_POOL || livingCount >= Settings.MAX_POPULATION) {
                try {

                    ArrayList<Seed> list = SeedLoader.load();

                    Log.info("Recreate population:" + list.size());
                    ArrayList entList = new ArrayList();
                    if( list.size() < Settings.STARTING_POPULATION ){
                        entList = Population.create(world, Settings.STARTING_POPULATION, world.width, world.height);
                    } else {
                        entList = Population.create(world, list, Settings.STARTING_POPULATION, world.width, world.height);
                    }
                    world.list = new ArrayList<>();
                    Runtime.getRuntime().gc();

                    for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
                        try {
                            Entity a = (Entity)entList.get(i);
                            if (a.alive) {
                                world.list.add(a);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.info(ex);
                        }

                    }

                    world.children = 0;
                    world.impact = 0;
                    world.spawns = 0;
                    world.controls = 0;

                    world.epoch++;
                    if (world.epoch == Settings.MAX_EPOCH) {
                        Log.info("total controls:" + world.totalControls);
                        Log.info("total spawns:" + world.totalSpawns);
                        System.exit(-1);
                    }
                    if (livingCount <= Settings.GENE_POOL && list.isEmpty()) {
                        try {
                            world.selection.makeNewList();
                            world.list = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (livingCount <= Settings.GENE_POOL && !list.isEmpty()) {

                        try {
                            world.selection.makeNewList();
                            if( list.size() < Settings.STARTING_POPULATION ) {
                                world.list = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                            } else {
                                world.list = Population.create(world, list, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                            }
                            Log.info("Using seed list:" + list.size());
                        } catch (Exception ex) {
                            if(Globals.verbose) {
                                ex.printStackTrace();
                            }
                        }
                    }

                } catch (Exception ex) {
                    if(Globals.verbose) {
                        ex.printStackTrace();
                    }
                }

            }
        } catch (Exception ex) {
        } finally {
            Runtime.getRuntime().gc();
            Globals.semaphore.release();
        }

        long end = System.currentTimeMillis();

    }
}
