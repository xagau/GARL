package garl;

import javax.swing.*;
import java.util.TimerTask;

public class ReplicationTask extends TimerTask {

    World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;

    public ReplicationTask(JFrame frame, World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
    }

    int ctr =0;
    @Override
    public void run() {

        long start = System.currentTimeMillis();

        try {
            Globals.semaphore.acquire();
        } catch (Exception ex) {
            return;
        }
        int livingCount = world.getLivingCount();
        if (Settings.NATURAL_REPLICATION) {
            for (int i = 0; i < world.list.size(); i++) {

                Entity e = world.list.get(i);

                if (e.fertile && e.alive) {
                    int min = Math.max(128, e.genome.read(Gene.MATURITY) * 2);
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

        if( ctr++ > Globals.cleanupTime ) {
            Runtime.getRuntime().gc();
            ctr = 0;
        }


        long end = System.currentTimeMillis();
        Globals.semaphore.release();
    }
}

