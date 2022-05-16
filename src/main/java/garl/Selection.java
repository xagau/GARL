package garl;

import java.util.ArrayList;

public class Selection  {

    public static ArrayList<Obstacle> rlist = new ArrayList<>();
    World world = null;

    Selection(World world) {
        this.world = world;
        makeNewList();

    }

    public void makeNewList() {
        Obstacle[] list = new Obstacle[16];
        rlist = new ArrayList<>();

        list[0] = new Obstacle();
        list[0].x = world.getWidth() - 20;
        list[0].y = 0;
        list[0].width = 20;
        list[0].height = world.getHeight() - 20;

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
        list[10].x = (int) (1440 * Math.random());
        list[10].y = (int) 500;
        list[10].width = (int) 20;
        list[10].height = (int) (430 * Math.random());


        list[11] = new Obstacle();
        list[11].x = (int) (1400 * Math.random());
        list[11].y = (int) (500 * Math.random());
        list[11].width = (int) (100 * Math.random());
        list[11].height = (int) 20;


        list[12] = new Obstacle();
        list[12].x = (int) 0;
        list[12].y = (int) (1000 * Math.random());
        list[12].width = (int) (1800 * Math.random());
        list[12].height = (int) 20;

        list[13] = new Obstacle();
        list[13].x = (int) (1400 * Math.random());
        list[13].y = (int) 100;
        list[13].width = (int) 100;
        list[13].height = (int) (300 * Math.random());


        list[14] = new Obstacle();
        list[14].x = (int) ((world.width - 100) * Math.random());
        list[14].y = (int) ((world.height - 100) * Math.random());
        list[14].control = true;
        list[14].width = (int) (100 * Math.random()) + 30;
        list[14].height = (int) (100 * Math.random()) + 30;
        list[14].kill = true;

        list[15] = new Obstacle();
        list[15].x = (int) ((world.width - 100) * Math.random());
        list[15].y = (int) ((world.height - 100) * Math.random());
        list[15].width = (int) (100 * Math.random()) + 30;
        list[15].height = (int) (100 * Math.random()) + 30;
        list[15].spawner = true;
        list[15].kill = false;


        Globals.spawn = list[15];
        Globals.control = list[14];

        for (int i = 0; i < list.length; i++) {
            rlist.add(list[i]);
        }
    }

    public boolean isTouching(Entity e){
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

    public boolean insideRect(Obstacle rect, int x, int y) {
        if (x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height) {
            return true;
        } else {
            return false;
        }
    }


}


