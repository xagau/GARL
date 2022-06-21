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
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.TimerTask;

public class AWTThreadManager extends Thread {
    JFrame frame = null;
    World world = null;

    public static java.util.Timer timer = new java.util.Timer();

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

        ReplicationTask replication = new ReplicationTask(world, frame);
        ThinkTask think = new ThinkTask(frame, world, width - inspectorPanelWidth, height, 5);
        DoingTask doing = new DoingTask(frame, world, width - inspectorPanelWidth, height, 5);
        SelectionTask selection = new SelectionTask(frame, world, width - inspectorPanelWidth, height);

        PaintTask paint = new PaintTask();
        PayoutTask payout = new PayoutTask();


        TimerTask threadUpdate = new TimerTask() {
            int ctr = 0;
            @Override
            public void run() {
                ctr++;
                if( ctr > Globals.ATC ) {
                    ThreadGroup g = Thread.currentThread().getThreadGroup();
                    if( Globals.benchmark ) {
                        Log.info("Active threads:" + g.activeCount());
                    }
                }
            }
        };


        try {



            Log.info("Time logging tasks");
            Thread t = new Thread() {
                public void run() {

                    boolean running = true;
                    int ctr = 0;
                    int thinkCtr = 0;
                    while( running ) {
                        try {
                            ctr++;
                            thinkCtr++;
                            paint.run();
                            doing.run();
                            selection.run();

                            if( thinkCtr >= Settings.THINK_RATE){
                                think.run();
                                thinkCtr = 0;
                            }
                            if (ctr >= Settings.FRAME_RATE) {
                                replication.run();
                                payout.run();
                                ctr = 0;
                            }
                        } catch(Exception ex) {}
                    }
                }
            };
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();

            timer.schedule( threadUpdate, 5, 1000);


            Log.info("Done logging tasks");



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
                Runtime.getRuntime().halt(0);
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

        Log.info("Shutdown hook added");

    }


}
