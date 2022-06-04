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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.TimerTask;

public class AWTThreadManager extends Thread {
    java.util.Timer timer = null;
    JFrame frame = null;
    World world = null;

    public  AWTThreadManager ( JFrame frame, World world ) {

        this.frame = frame;
        this.world = world;

    }

    public void run() {

        timer = new java.util.Timer(true);
        int width = world.getWidth();
        int height = world.getHeight();
        int inspectorPanelWidth = 0;
        if( !Globals.screenSaverMode ) {
            inspectorPanelWidth = Settings.INSPECTOR_WIDTH;;
        }

        ThinkTask think = new ThinkTask(frame, world, width - inspectorPanelWidth, height, 5);
        SelectionTask selection = new SelectionTask(frame, world, width - inspectorPanelWidth, height);
        java.util.Timer timer = new java.util.Timer(true);
        TimerTask paint = new TimerTask() {
            int ctr = 0;

            @Override
            public void run() {

                try {
                    long start = System.currentTimeMillis();
                    ctr++;
                    if (ctr > Globals.cleanupTime) {
                        Runtime.getRuntime().gc();
                        ctr = 0;
                    }
                    boolean b = Globals.semaphore.tryAcquire();
                    if( !b ){
                        return;
                    }

                    world.render();

                    long end = System.currentTimeMillis();
                } catch (Exception ex) {
                    if( Globals.verbose) {
                        ex.printStackTrace();
                    }
                } catch (Error e) {
                    if( Globals.verbose) {
                        e.printStackTrace();
                    }
                } finally {
                    Globals.semaphore.release();

                }
            }
        };

        TimerTask payoutTask = new TimerTask() {
            @Override
            public void run() {

                try {

                    long start = System.currentTimeMillis();
                    boolean b = Globals.semaphore.tryAcquire();
                    if( !b ){
                        return;
                    }


                    double phl = world.phl;
                    world.phl = 0;
                    DecimalFormat df = new DecimalFormat("0.00000000");
                    MoneyMQ moneyMQ = new MoneyMQ();
                    moneyMQ.send(Settings.PAYOUT_ADDRESS, df.format(phl));
                    Runtime.getRuntime().gc();
                    long end = System.currentTimeMillis();

                } catch (Exception ex) {
                    ex.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                } finally {
                    Globals.semaphore.release();

                }
            }
        };

        try {

            int fps = 1000 / Globals.FPS;
            timer.scheduleAtFixedRate(paint, 0, fps);
            timer.scheduleAtFixedRate(think, 0, Globals.thinkTime);
            timer.scheduleAtFixedRate(selection, 0, Globals.selectionTime);
            timer.scheduleAtFixedRate(payoutTask, 0, Globals.HOUR);


        } catch(Exception ex) {
            ex.printStackTrace();
            Log.info(ex.getMessage());
        } catch(Error er){
            er.printStackTrace();
            Log.info(er);
        }

        ShutdownHook hook = new ShutdownHook(frame);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                timer.purge();
                frame.setVisible(false);
                frame.dispose();
                Runtime.getRuntime().exit(0);
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });




    }


}
