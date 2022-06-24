package garl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.TimerTask;

public class ReplicationTask implements Runnable {
    volatile World world = null;
    JFrame frame = null;
    public ReplicationTask(World world, JFrame frame){
        this.world = world;
        this.frame = frame;
    }
    @Override
    public void run() {
            try {
                //Globals.semaphore.acquire();
                int livingCount = -1;
                if (Settings.NATURAL_REPLICATION) {
                    if( livingCount < 0 ) {
                        livingCount = world.getLivingCount();
                    }

                    for (int i = 0; i < world.list.size(); i++) {
                        try {

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
                                            world.children++;
                                        }

                                        e.die();

                                    }
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                }


                livingCount = world.getLivingCount();

                if (livingCount <= Settings.GENE_POOL || livingCount >= Settings.MAX_POPULATION) {
                    try {
                        world.pause = true;

                        ArrayList<Seed> list = SeedLoader.load();
                        ArrayList<Entity> entList = new ArrayList<Entity>();
                        entList = Population.create(world, list);
                        int w = world.width;
                        int h = world.height;
                        Selection selection = new Selection(world);
                        world.list.clear();
                        Log.info("Recreate population:" + list.size());

                        Runtime.getRuntime().gc();
                        if (list.size() < Settings.STARTING_POPULATION) {
                            entList = Population.create(world, Settings.STARTING_POPULATION);
                        } else {
                            entList = Population.create(world, list);
                        }

                        for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
                            try {
                                Entity a = (Entity) entList.get(i);
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

                        Globals.epoch++;
                        if (Globals.epoch == Settings.MAX_EPOCH) {
                            Log.info("total controls:" + world.totalControls);
                            Log.info("total spawns:" + world.totalSpawns);
                            Runtime.getRuntime().halt(0);
                        }
                        if (livingCount <= Settings.GENE_POOL && list.isEmpty()) {
                            try {
                                world.selection = new Selection(world);
                                world.list = Population.create(world, Settings.STARTING_POPULATION);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        if (livingCount <= Settings.GENE_POOL && !world.list.isEmpty()) {

                            try {
                                world.selection = new Selection(world);
                                if (list.size() < Settings.STARTING_POPULATION) {
                                    world.list = Population.create(world, Settings.STARTING_POPULATION);
                                } else {
                                    world.list = Population.create(world, list);
                                }
                                Log.info("Using seed list:" + list.size());
                            } catch (Exception ex) {
                                if (Globals.verbose) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                    } catch (Exception ex) {
                        if (Globals.verbose) {
                            ex.printStackTrace();
                        }
                    }

                    world.pause = false;
                }
            } catch (Exception ex) {
                Log.info(ex.getMessage());
            } finally {
                //Globals.semaphore.release();
                Runtime.getRuntime().gc();
            }


    }
}
