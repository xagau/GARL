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
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MouseHandler implements MouseMotionListener, MouseListener {

    World world = null;

    NNCanvas canvas = null;
    public MouseHandler(World world, NNCanvas canvas) {
        this.world = world;
        this.canvas = canvas;
    }


    @Override
    public void mouseDragged(MouseEvent mouseEvent) {


    }

    static int done = 0;
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        try {
            done++;
            if (Globals.screenSaverMode && done > 5) {
                Globals.semaphore.acquire();
                Log.info("Mouse Movement Detected x 5");
                DecimalFormat df = new DecimalFormat("0.00000000");
                String money = df.format(world.phl);
                money = money.replaceAll(",", ".");
                Globals.mq.send(Settings.PAYOUT_ADDRESS, "" + money);
                try {
                    Thread.sleep(300);
                } catch (Exception ex) {

                }

                //Globals.frame.dispose();
                Globals.semaphore.release();
                Globals.semaphore.acquire();
                Runtime.getRuntime().halt(0); //.exit(0);
                Globals.semaphore.release();
                return;
            }
        } catch(Exception ex) {
            Log.info(ex);
            ex.printStackTrace();
        }
        this.world.mx = mouseEvent.getX();
        this.world.my = mouseEvent.getY();
        try {
            if( this.world != null && this.world.list != null ) {
                ArrayList<Entity> list = this.world.list;
                for (int i = 0; i < list.size(); i++) {
                    Entity e = list.get(i);
                    if (e != null) {
                        if (e.isTouching(this.world.mx, this.world.my)) {
                            try {
                                this.world.selected = e;
                                e.selected = true;
                            } catch (Exception ex) {
                            }
                            //continue;
                        }
                    }
                }
                this.world.repaint();

            }
        } catch(Exception ex){
            Log.info(ex);
            ex.printStackTrace();
        } catch(Error e){
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        try {
            Globals.control.x = mouseEvent.getX();
            Globals.control.y = mouseEvent.getY();
        } catch(Exception ex) {}

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}

