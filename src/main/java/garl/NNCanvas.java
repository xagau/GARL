package garl;

import java.awt.*;

class NNCanvas extends Canvas {

    Entity entity = null;
    World world = null;

    public NNCanvas(World world) {
        this.world = world;
    }

    public void setSelected(Entity e) {
        entity = e;
    }

    boolean NNdebug = false;

    public void paint(Graphics g) {

        try {
            if( Globals.img != null ) {
                System.out.println("img is not null");
                g.drawImage(Globals.img, 0, 0, GARLTask.world);
                g.dispose();
                repaint();
            }
            if (NNdebug) {
                return;
            }
            entity = world.selected;
            if (entity == null) {
                return;
            }
            if (!entity.alive) {
                return;
            }
            boolean b = true;
            if( b){return; }
            int inputs = entity.brain.ann.input.numberOfNeuronsInLayer;
            int dense = entity.brain.ann.dense.numberOfNeuronsInLayer;
            int hidden = entity.brain.ann.hidden.numberOfNeuronsInLayer;
            int dropout = entity.brain.ann.dropout.numberOfNeuronsInLayer;
            int output = entity.brain.ann.output.numberOfNeuronsInLayer;

            int numLayers = 5;
            int circle = 3;
            int space = circle * 2;
            int startingPos = 10;
            int pos = 3;
            int initPos = pos;
            int hpos = 10;
            g.setColor(Color.BLACK);
            int maxHeight = 0;
            int offset = 0;
            for (int i = 1; i <= inputs; i++) {

                try {
                    int f = space * i;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                    maxHeight = pos;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            offset = maxHeight / dense / 2;
            space = (maxHeight / dense);

            int startingPosX = startingPos;
            int startingPosY = startingPos;

            startingPosX += circle;
            for (int j = 1; j <= inputs; j++) {
                for (int k = 1; k <= dense; k++) {
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset * j );
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset  + space *j);
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset + space + space *j);
                    g.drawLine((startingPosX + ((circle) / 2)), startingPosY - ((circle) / 2), hpos , k + offset + space + space + space *j);
                }
                startingPosY = (j+initPos);
                System.out.println("Going down:" + startingPosY + "j" + j + "*pos" + pos);

            }


            for (int i = 1; i <= dense; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            space = maxHeight / hidden;
            offset = maxHeight / hidden / 2;
            for (int i = 1; i <= hidden; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            space = maxHeight / dropout;
            offset = maxHeight / dropout / 2;

            for (int i = 1; i <= dropout; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            hpos += (Settings.INSPECTOR_WIDTH / numLayers);


            space = maxHeight / output;

            offset = maxHeight / 2;
            for (int i = 1; i <= output; i++) {

                try {
                    int f = (space * i) - offset;
                    g.fillOval(hpos + circle, circle + f, circle + circle, circle + circle);
                    pos += space;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        } catch (Exception ex) {
            g.drawString("Selected is null", 10, 10);
        }

    }
}