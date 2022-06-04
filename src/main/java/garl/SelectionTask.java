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


    int ctr = 0;
    @Override
    public void run() {

        long start = System.currentTimeMillis();
        try {
            Globals.semaphore.acquire();


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

                                    if( Globals.verbose ) {
                                        Log.info("Spawn:X" + Globals.spawn.x);
                                        Log.info("Spawn:Y" + Globals.spawn.y);
                                    }


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

                if( ctr++ > Globals.cleanupTime ) {
                    Runtime.getRuntime().gc();
                    ctr = 0;
                }
            }
        } catch (Exception ex) {

        } finally {
            Globals.semaphore.release();
            long end = System.currentTimeMillis();
            //System.out.println(end-start + " selection");

        }
    }
}
