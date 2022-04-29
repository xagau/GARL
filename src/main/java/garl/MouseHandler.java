package garl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
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
                            this.canvas.repaint();
                        } catch (Exception ex) {
                        }
                        return;
                    }
                }
            }
        } catch(Exception ex){}
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

