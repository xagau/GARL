package garl;

import java.util.TimerTask;

public class PaintTask extends TimerTask {

    public void run() {
        try {
            long start = System.currentTimeMillis();
            Globals.semaphore.acquire();
            Globals.world.render();
            long end = System.currentTimeMillis();
        } catch (Exception ex) {
            if (Globals.verbose) {
                ex.printStackTrace();
            }
        } catch (Error e) {
            if (Globals.verbose) {
                e.printStackTrace();
            }
        } finally {
            Globals.semaphore.release();
        }
    }
}

