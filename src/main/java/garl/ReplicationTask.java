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


        if( ctr++ > Globals.cleanupTime ) {
            Runtime.getRuntime().gc();
            ctr = 0;
        }


        long end = System.currentTimeMillis();
        Globals.semaphore.release();
    }
}

