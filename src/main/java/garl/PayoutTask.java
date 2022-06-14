package garl;

import java.text.DecimalFormat;
import java.util.TimerTask;

class PayoutTask extends TimerTask {
    @Override
    public void run() {

        try {


            long start = System.currentTimeMillis();
            Globals.semaphore.acquire();

            double phl = Globals.world.phl;
            if (phl > Globals.minPayout) {

                Globals.world.phl = 0;
                DecimalFormat df = new DecimalFormat("0.00000000");
                MoneyMQ moneyMQ = new MoneyMQ();
                moneyMQ.send(Settings.PAYOUT_ADDRESS, df.format(phl));
                moneyMQ = null;
                long end = System.currentTimeMillis();
            }

            Runtime.getRuntime().gc();

        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        } finally {
            Globals.semaphore.release();

        }

    }
};
