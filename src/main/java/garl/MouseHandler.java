package garl;

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
            ex.printStackTrace();
        }
        this.world.mx = mouseEvent.getX();
        this.world.my = mouseEvent.getY();
        try {
            ArrayList<Entity> list = world.list;
            for (int i = 0; i < list.size(); i++) {
                Entity e = list.get(i);
                if (e != null) {
                    if (e.isTouching(this.world.mx, this.world.my)) {
                        try {
                            this.world.selected = e;
                            e.selected = true;
                            this.world.repaint();
                            //this.canvas.repaint();
                        } catch (Exception ex) {
                        }
                        return;
                    }
                }
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
        Globals.control.x = mouseEvent.getX();
        Globals.control.y = mouseEvent.getY();

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

