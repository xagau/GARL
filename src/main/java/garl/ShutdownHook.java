package garl;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;

public class ShutdownHook extends Thread {
    JFrame frame = null;
    public ShutdownHook(JFrame frame)
    {
        this.frame = frame;
    }
    public void run() {
        try {
            Log.info("Shutdown called");
            MoneyMQ mq = new MoneyMQ();
            double phl = Globals.world.phl;

            DecimalFormat df = new DecimalFormat("0.00000000");
            mq.send(Settings.PAYOUT_ADDRESS, df.format(phl));

        } catch (Exception ex) {
            Log.info("Exception during shutdown:" + ex);
            ex.printStackTrace();
        } catch (Error e) {
            Log.info("Error during shutdown:" + e);
            e.printStackTrace();
        } finally {
            frame.dispose();
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            Iterator itr = threadSet.iterator();
            while( itr.hasNext()){
                try {
                    Thread t = (Thread) itr.next();
                    t.stop();
                    Runtime.getRuntime().gc();
                } catch(Exception ex){}
            }

        }
    }
}
