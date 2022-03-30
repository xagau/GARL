package garl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        System.out.println("Key Pressed");
        if (world.selected != null) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                System.out.println("Key Right");
                world.selected.process(Action.MOVE_RIGHT, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                System.out.println("Key Left");
                world.selected.process(Action.MOVE_LEFT, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                System.out.println("Key Up");
                world.selected.process(Action.MOVE_UP, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                System.out.println("Key Down");
                world.selected.process(Action.MOVE_DOWN, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_O) {
                System.out.println("Key O");
                World.offset += 1;
                System.out.println("offset:" + World.offset);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_D) {
                System.out.println("Key D");
                World.offset -= 1;
                System.out.println("offset:" + World.offset);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
                System.out.println("Key S");
                world.selected.process(Action.STOP, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_K) {
                System.out.println("Key K");
                for (int i = 0; i < world.list.size(); i++) {
                    Entity e = (Entity) world.list.get(i);
                    e.die();
                }
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    KeyHandler(World world) {
        this.world = world;
    }

    World world = null;
}
