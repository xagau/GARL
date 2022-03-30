package garl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class MouseHandler implements MouseMotionListener, MouseListener {

    World world = null;

    public MouseHandler(World world) {
        this.world = world;
    }

    int startx = -1;
    int starty = -1;
    int endx = -1;
    int endy = -1;

    boolean dragging = false;
    Obstacle current = null;

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {


    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        this.world.mx = mouseEvent.getX();
        this.world.my = mouseEvent.getY();
        ArrayList<Entity> list = world.list;
        for (int i = 0; i < list.size(); i++) {
            Entity e = list.get(i);
            if (e != null) {
                if (e.isTouching(this.world.mx, this.world.my)) {
                    this.world.selected = e;
                    e.selected = true;
                    this.world.repaint();
                    return;
                }
            }
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

