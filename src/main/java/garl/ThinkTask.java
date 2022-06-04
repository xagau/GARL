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
            boolean b = Globals.semaphore.tryAcquire();
            if( !b ){
                return;
            }


            int ctr = 0;
            int sz = world.list.size();

            for (int i = 0; i < sz; i++) {
                try {
                    ctr++;
                    if( ctr > 200 ){
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

            if (Settings.NATURAL_REPLICATION) {
                for (int i = 0; i < world.list.size(); i++) {

                    Entity e = world.list.get(i);

                    if (e.fertile && e.alive) {
                        int min = Math.max(32, e.genome.read(Gene.MATURITY) * 2);
                        if (e.alive && (e.age > min)) {
                            if (Math.random() > 0.8) {
                                int n = e.genome.read(Gene.RR) % Settings.MAX_OFFSPRING;
                                if (livingCount > Settings.MAX_POPULATION) {
                                    n = Math.min(2, n);
                                }
                                final int nn = n;

                                for (int j = 0; j <= nn; j++) {

                                    Entity a = e.replicate();
                                    world.list.add(a);
                                    world.prospectSeeds.add(a);
                                    world.children++;
                                }

                                e.die();

                            }
                        }
                    }
                }
            }



            if (livingCount <= Settings.GENE_POOL || livingCount >= Settings.MAX_POPULATION) {
                try {

                    ArrayList<Seed> list = SeedLoader.load();

                    world.reset();
                    Log.info("Recreate population:" + list.size());
                    ArrayList<Entity> entList = new ArrayList<Entity>();

                    Runtime.getRuntime().gc();
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

                    livingCount = world.getLivingCount();

                    world.epoch++;
                    if (world.epoch == Settings.MAX_EPOCH) {
                        Log.info("total controls:" + world.totalControls);
                        Log.info("total spawns:" + world.totalSpawns);
                        Runtime.getRuntime().exit(0);
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
            long end = System.currentTimeMillis();
            Globals.semaphore.release();
            System.out.println("Think:");
        }



    }
}
