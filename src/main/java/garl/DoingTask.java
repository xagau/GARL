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

public class DoingTask implements Runnable {
    volatile World world = null;

    int width = 0;
    int height = 0;
    JFrame frame = null;
    int chunk = 0;

    public DoingTask(JFrame frame, World world, int width, int height, int chunk) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.frame = frame;
        this.chunk = chunk;
    }

    long start = 0;
    long snap = System.currentTimeMillis();
    @Override
    public void run() {

        start = System.currentTimeMillis();


            try {
                //Globals.semaphore.acquire();
                for (int i = 0; i < world.list.size(); i++) {
                        Entity e = world.list.get(i);
                        if (e.alive) {
                            e.act(world, start);
                        } else {
                            e.age++;
                            e.consume();
                            e.size = e.calculateSize();
                        }
                }

                long end = System.currentTimeMillis();
                if( Globals.benchmark ) {
                    Log.info("doing diff:" + (end - start));
                }
                //Runtime.getRuntime().gc();

            } catch (Exception ex) {
            } finally {
                //Globals.semaphore.release();

            }

    }
}
