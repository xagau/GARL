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
        int sz = (int) (Math.random() * 30 );
        sz += 10;

        Obstacle[] list = new Obstacle[sz];
        rlist = new ArrayList<>();

        int i = 0;
        for(i = 0; i < sz-3; i++ ) {
            list[i] = new Obstacle();
            list[i].x = (int)(Math.random() * world.width) - 20;
            list[i].y = (int)(Math.random() * world.height) - 20;
            if( Math.random() > 0.5 ) {
                list[i].width = (int) (Math.random() * world.width / 2) + 20;
                list[i].height = (int) (Math.random() ) + 20;
            } else {
                list[i].width = (int) (Math.random() ) + 20;
                list[i].height = (int) (Math.random() * world.height / 2) + 20;
            }
        }

        /*
        list[1] = new Obstacle();
        list[1].x = 100;
        list[1].y = 100;
        list[1].width = 400;
        list[1].height = 20;

        list[2] = new Obstacle();
        list[2].x = 0;
        list[2].y = -10;
        list[2].width = world.getWidth() - 20;
        list[2].height = 30;

        list[3] = new Obstacle();
        list[3].x = 0;
        list[3].y = world.getHeight() - 20;
        list[3].width = world.getWidth() - 20;
        list[3].height = 20;


        list[4] = new Obstacle();
        list[4].x = (int) (200 * Math.random());
        list[4].y = (int) (200 * Math.random());
        list[4].width = (int) (20 * Math.random());
        list[4].height = (int) (400 * Math.random());

        list[5] = new Obstacle();
        list[5].x = (int) (0);
        list[5].y = (int) (world.height - 80);
        list[5].width = (int) (world.width);
        list[5].height = (int) (80);


        list[6] = new Obstacle();
        list[6].x = (int) (200 * Math.random());
        list[6].y = (int) (500 * Math.random());
        list[6].width = (int) (20 * Math.random());
        list[6].height = (int) (430 * Math.random());

        list[7] = new Obstacle();
        list[7].x = (int) 0;
        list[7].y = (int) 300;
        list[7].width = (int) 20;
        list[7].height = (int) (630 * Math.random());

        list[8] = new Obstacle();
        list[8].x = (int) (700 * Math.random());
        list[8].y = (int) (200 * Math.random());
        list[8].width = (int) 20;
        list[8].height = (int) (730 * Math.random());


        list[9] = new Obstacle();
        list[9].x = (int) (990 * Math.random());
        list[9].y = (int) 0;
        list[9].width = (int) 20;
        list[9].height = (int) (700 * Math.random());


        list[10] = new Obstacle();
        list[10].x = (int) (1040 * Math.random());
        list[10].y = (int) 500;
        list[10].width = (int) 20;
        list[10].height = (int) (430 * Math.random());


        list[11] = new Obstacle();
        list[11].x = (int) (1100 * Math.random());
        list[11].y = (int) (500 * Math.random());
        list[11].width = (int) (100 * Math.random());
        list[11].height = (int) 20;


        list[12] = new Obstacle();
        list[12].x = (int) 0;
        list[12].y = (int) (1000 * Math.random());
        list[12].width = (int) (700 * Math.random());
        list[12].height = (int) 20;

        list[13] = new Obstacle();
        list[13].x = (int) (1400 * Math.random());
        list[13].y = (int) 100;
        list[13].width = (int) 100;
        list[13].height = (int) (300 * Math.random());
         */


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
            rlist.add(list[j]);
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

    public synchronized boolean insideRect(Obstacle rect, int x, int y) {
        if (x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height) {
            return true;
        } else {
            return false;
        }
    }


}


