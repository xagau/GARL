package garl;

import javax.swing.*;
import java.util.TimerTask;

public class EntityTask extends TimerTask {

    World world = null;
    NNCanvas canvas= null;
    int width, height;
    JFrame frame = null;
    public EntityTask(JFrame frame, NNCanvas canvas, World world, int width, int height){
        this.canvas = canvas;
        this.world = world;
        this.height = height;
        this.width = width;
        this.frame = frame;
    }
    int ctr = 0;
    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            Globals.semaphore.acquire();
            ctr++;
            if (ctr > 100) {
                ctr = 0;
            }
            canvas.repaint();
            long end = System.currentTimeMillis();

        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        } finally {
            Globals.semaphore.release();
        }

    }
}
