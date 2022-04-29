package garl;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

        Log.info("Key Pressed");
        if (world.selected != null) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
                Log.info("Key Right");
                world.selected.process(Action.MOVE_RIGHT, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
                Log.info("Key Left");
                world.selected.process(Action.MOVE_LEFT, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                Log.info("Key Up");
                world.selected.process(Action.MOVE_UP, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                Log.info("Key Down");
                world.selected.process(Action.MOVE_DOWN, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_O) {
                Log.info("Key O");
                World.offset += 1;
                Log.info("offset:" + World.offset);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_D) {
                Log.info("Key D");
                World.offset -= 1;
                Log.info("offset:" + World.offset);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
                Log.info("Key S");
                world.selected.process(Action.STOP, world, 0);
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_K) {
                Log.info("Key K");
                for (int i = 0; i < world.list.size(); i++) {
                    Entity e = (Entity) world.list.get(i);
                    e.die();
                }
            } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                Log.info("Key K");
                System.exit(-1);
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
