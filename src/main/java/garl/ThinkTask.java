package garl;

import javax.swing.*;
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

                    System.out.println("Recreate population");
                    world.list = new ArrayList<>();
                    ArrayList<Entity> seedList = GARLTask.load(list, world);
                    for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
                        try {
                            Entity a = seedList.get(i);
                            if (a.alive) {
                                world.list.add(a);
                            }
                        } catch (Exception ex) {
                        }

                    }


                    world.children = 0;
                    world.impact = 0;
                    world.spawns = 0;
                    world.controls = 0;

                    world.epoch++;
                    if (world.epoch == 900) {
                        System.out.println("total controls:" + world.totalControls);
                        System.out.println("total spawns:" + world.totalSpawns);
                        System.exit(-1);
                    }
                    if (livingCount <= Settings.GENE_POOL && seedList.isEmpty()) {
                        try {
                            world.list = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
                            world.selection.makeNewList();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (livingCount <= Settings.GENE_POOL && !seedList.isEmpty()) {

                        try {
                            world.list = Population.create(world, seedList, Math.max(Settings.STARTING_POPULATION, Math.min(seedList.size(), Settings.STARTING_POPULATION)), frame.getWidth(), frame.getHeight());
                            world.selection.makeNewList();
                            System.out.println("Using seed list:" + seedList.size());
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
    }
}
