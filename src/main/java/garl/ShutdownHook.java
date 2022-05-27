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
