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
