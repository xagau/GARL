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
import java.util.UUID;

public class SelectionTask implements Runnable {

    volatile World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;

    public SelectionTask(JFrame frame, World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
    }

    public void save(int epoch, Entity e) {
        try {
            FileWriter writer = new FileWriter(new File(Property.getProperty("settings.genomes") + System.currentTimeMillis() + "-genome-" + UUID.randomUUID().toString() + ".json"));
            writer.write("{ \"epoch\":" + epoch + ",\"generation\":" + e.generation + ", \"genome\":\"" + e.genome.code + "\", \"reward\":\"" + e.reward + "\" }");
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
                //Globals.semaphore.acquire();




                Selection selection = world.selection;

                ArrayList<Obstacle> rlist = world.selection.rlist;

                //world.fixNaN();
                world.killNaN();

                for (int i = 0; i < world.list.size(); i++) {
                    Entity e = world.list.get(i);
                    Random rand = new Random();
                    for (int j = 0; j < rlist.size(); j++) {
                        Obstacle rect = rlist.get(j);
                        if (rect != null) {
                            if (e.alive) {


                                if( selection.notInBounds(e, world)){
                                    world.impact++;
                                    e.die();
                                } else if (selection.insideRect(rect, e)) {
                                    if (rect.spawner) {
                                        Globals.spawn = rect;

                                        if (Globals.verbose) {
                                            Log.info("Spawn:X" + Globals.spawn.x);
                                            Log.info("Spawn:Y" + Globals.spawn.y);
                                        }

                                        e.reward++;
                                        save(Globals.epoch, e);
                                        for (int k = 0; k < Settings.MAX_SPAWN_OFFSPRING; k++) {
                                            Entity n = e.clone();
                                            n.location.x = rand.nextInt(world.width);
                                            n.location.y = rand.nextInt(world.height);
                                            n.alive = true;
                                            world.list.add(n);
                                            world.spawns++;
                                            world.totalSpawns++;
                                        }

                                        e.die();

                                    } else if (rect.kill) {
                                        if (rect.control) {
                                            world.controls += Settings.MAX_SPAWN_OFFSPRING;
                                            world.totalControls += Settings.MAX_SPAWN_OFFSPRING;
                                        }
                                        if( rect.isVisible()) {
                                            world.impact++;
                                            e.die();
                                        }

                                    } else if (rect.push) {
                                        if (rect.push) {
                                            for(int k = 0; k < world.selection.rlist.size(); k++){
                                                Obstacle o = world.selection.rlist.get(k);
                                                if( !o.control && !o.push && !o.spawner ){
                                                    if( o.isVisible() ) {
                                                        o.setVisible(false);
                                                    } else {
                                                        o.setVisible(true);
                                                    }
                                                }
                                            }
                                        }
                                        world.impact++;
                                        e.die();

                                    }
                                }
                            }
                        }
                    }

                    if (e.getEnergy() <= Settings.MIN_ENERGY) {
                        e.die();
                    }
                }
            } catch (Exception ex) {

                Log.info(ex.getMessage());
            } finally {
                //Globals.semaphore.release();
                long end = System.currentTimeMillis();
                if( Globals.benchmark ) {
                    Log.info("selection:" + (end - start));
                }
                Runtime.getRuntime().gc();


            }



    }
}
