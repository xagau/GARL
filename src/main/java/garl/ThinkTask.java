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

        //System.out.println("ThinkTask:" + start);
        try {
            Globals.semaphore.acquire();

            int livingCount = world.getLivingCount();

            for (int i = 0; i < world.list.size(); i++) {
                try {
                    Entity e = world.list.get(i);
                    if (e.alive) {
                        e.think(world, start);
                    }
                } catch (Exception ex) {
                }
            }

            if (livingCount <= Settings.GENE_POOL || livingCount >= Settings.MAX_POPULATION) {
                try {

                    ArrayList<Seed> list = GARLTask.load();

                    Log.info("Recreate population:" + list.size());
                    ArrayList entList = new ArrayList();
                    if( list.size() == 0 ){
                        entList = Population.create(world, Settings.STARTING_POPULATION, world.width, world.height);
                    } else {
                        entList = Population.create(world, list, Settings.MAX_POPULATION, width, height);
                    }
                    world.list = new ArrayList<>();
                    //ArrayList<Entity> seedList = GARLTask.load(list, world);
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
                    if (world.epoch == 900) {
                        Log.info("total controls:" + world.totalControls);
                        Log.info("total spawns:" + world.totalSpawns);
                        System.exit(-1);
                    }
                    if (livingCount <= Settings.GENE_POOL && list.isEmpty()) {
                        try {
                            world.list = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                            world.selection.makeNewList();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (livingCount <= Settings.GENE_POOL && !list.isEmpty()) {

                        try {
                            world.list = Population.create(world, list, Math.max(Settings.STARTING_POPULATION, Math.min(list.size(), Settings.STARTING_POPULATION)), frame.getWidth(), frame.getHeight());
                            world.selection.makeNewList();
                            Log.info("Using seed list:" + list.size());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                } catch (Exception ex) {
                }

            }
        } catch (Exception ex) {
        } finally {
            Globals.semaphore.release();
        }

        long end = System.currentTimeMillis();
        //System.out.println("ThinkTask:" + end);
    }
}
