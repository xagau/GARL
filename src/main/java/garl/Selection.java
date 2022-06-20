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
import java.util.ArrayList;

public class Selection  {

    public static volatile ArrayList<Obstacle> rlist = new ArrayList<>();
    volatile World world = null;

    Selection(World world) {
        this.world = world;
        makeNewList();

    }

    public void makeNewList() {
        int sz = (int) (Math.random() * 70 );
        sz += 10;

        Obstacle[] list = new Obstacle[sz];
        world.selection.rlist = new ArrayList<>();

        int i = 0;
        int obstacleWidth = 5;
        int obstacleHeight = 4;
        int additionalObstacles = 3;
        for(i = 0; i < sz-additionalObstacles; i++ ) {
            list[i] = new Obstacle();
            list[i].x = (int)(Math.random() * world.width) - obstacleWidth;
            list[i].y = (int)(Math.random() * world.height) - obstacleWidth;
            if( Math.random() > 0.5 ) {
                list[i].width = (int) (Math.random() * world.width / obstacleHeight) + obstacleWidth;
                list[i].height = (int) (Math.random() ) + obstacleWidth;
            } else {
                list[i].width = (int) (Math.random() ) + obstacleWidth;
                list[i].height = (int) (Math.random() * world.height / obstacleHeight) + obstacleWidth;
            }
        }

        list[i] = new Obstacle();
        list[i].x = (int) ((world.width - 200) * Math.random());
        list[i].y = (int) ((world.height - 200) * Math.random());
        list[i].control = true;
        list[i].width = (int) (100 * Math.random()) + 30;
        list[i].height = (int) (100 * Math.random()) + 30;
        list[i].kill = true;
        Globals.control = list[i];
        i++;

        list[i] = new Obstacle();
        list[i].x = (int) ((world.width - 100) * Math.random());
        list[i].y = (int) ((world.height - 100) * Math.random());
        list[i].width = (int) (100 * Math.random()) + 30;
        list[i].height = (int) (100 * Math.random()) + 30;
        list[i].spawner = true;
        list[i].kill = false;
        Globals.spawn = list[i];
        i++;

        list[i] = new Obstacle();
        list[i].x = (int) ((world.width - 100) * Math.random());
        list[i].y = (int) ((world.height - 100) * Math.random());
        list[i].width = (int) (100 * Math.random()) + 30;
        list[i].height = (int) (100 * Math.random()) + 30;
        list[i].spawner = false;
        list[i].kill = false;
        list[i].push = true;
        Globals.push = list[i];
        i++;

        for (int j = 0; j < list.length; j++) {
            world.selection.rlist.add(list[j]);
        }
    }

    public synchronized boolean isTouching(Entity e){
        try {
            for (int i = 0; i < world.selection.rlist.size(); i++) {
                if( insideRect(world.selection.rlist.get(i), (int)e.location.x, (int)e.location.y) ){
                    return true;
                }
            }
            return false;
        } catch(Exception ex) { }
        return false;
    }

    public synchronized boolean insideRect(Obstacle rect, Entity e) {
        if (e.location.x + e.size >= rect.x && e.location.x < rect.x + rect.width && e.location.y + e.size > rect.y && e.location.y < rect.y + rect.height) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean insideRect(Obstacle rect, int x, int y) {
        if (x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean notInBounds(Entity e, World world) {
        if (e.location.x <= 0 || e.location.x + e.size >= world.width || e.location.y <= 0 || e.location.y + e.size >= world.height ) {
            return true;
        } else {
            return false;
        }
    }


}


