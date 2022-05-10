package garl;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class SelectionTask extends TimerTask {

    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;

    public SelectionTask(JFrame frame, World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
    }

    public void save(int epoch, int generation, Genome g) {
        try {
            FileWriter writer = new FileWriter(new File(Property.getProperty("settings.genomes") + System.currentTimeMillis() + "-genome-" + GARLTask.run.toString() + ".json"));
            writer.write("{ \"epoch\":" + epoch + ",\"generation\":" + generation + ", \"genome\":\"" + g.code + "\" }");
            writer.flush();
            writer.close();

        } catch (Exception ex) {

        }
    }


    @Override
    public void run() {
        // Perform garl.Selection.
        try {
            Globals.semaphore.acquire();

            long start = System.currentTimeMillis();
            Selection selection = world.selection;

            ArrayList<Obstacle> rlist = world.selection.rlist;

            for (int i = 0; i < world.list.size(); i++) {
                Entity e = world.list.get(i);
                if (e.location.y == 0) {
                    e.die();
                } else if (e.location.x == 0) {
                    e.die();
                }
                Random rand = new Random();
                for (int j = 0; j < rlist.size(); j++) {
                    Obstacle rect = rlist.get(j);
                    if (rect != null) {
                        if (e.alive) {

                            if (selection.insideRect(rect, (int) e.location.x, (int) e.location.y)) {
                                if (rect.spawner) {
                                    Globals.spawn = rect;
                                    save(world.epoch, e.generation, e.genome);
                                    for (int k = 0; k < Settings.MAX_OFFSPRING; k++) {
                                        Entity n = e.clone();
                                        n.location.x = rand.nextInt(frame.getWidth());
                                        n.location.y = rand.nextInt(frame.getHeight());
                                        n.alive = true;
                                        world.list.add(n);
                                        world.spawns++;
                                        world.totalSpawns++;

                                        world.prospectSeeds.add(e.clone());
                                        if (world.spawns >= world.bestSpawn) {
                                            world.bestSeeds.add(e.clone());
                                            world.bestSpawn = world.spawns;
                                        }
                                        long intermediate = System.currentTimeMillis();
                                        if (intermediate - start > Globals.threshold) {
                                            return;
                                        }
                                    }
                                    e.reachedGoal = true;
                                    e.die();
                                } else if (rect.kill) {
                                    if (rect.control) {
                                        world.controls += Settings.MAX_OFFSPRING;
                                        world.totalControls += Settings.MAX_OFFSPRING;
                                    }
                                    world.impact++;
                                    e.die();
                                }
                            }
                        }
                    }
                }

                if (e.getEnergy() <= 0) {
                    e.die();
                }

                long end = System.currentTimeMillis();
            }
        } catch (Exception ex) {

        } finally {
            Globals.semaphore.release();
        }
    }
}
