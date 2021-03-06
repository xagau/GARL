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
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Timer;

public final class ScreenSaver {

    public static UUID run = UUID.randomUUID();
    ArrayList<Seed> list = null;



    static GARLFrame frame = new GARLFrame();

    static JPanel inspector = new JPanel();
    public static World world = null;
    static Selection selection = null;
    public static JPanel inspectorContainer = new JPanel();



    public static final void main(final String[] args)  {

        try {
            CullingStrategy.cleanup();

            try {
                Globals.increment = Double.parseDouble(Property.getRemoteProperty("settings.increment"));
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            Log.info("Running in Screen Saver Mode:");
            Globals.screenSaverMode = true;
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


            ArrayList<Seed> list = new ArrayList<>();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width;
            int height = screenSize.height;
            //int inspectorPanelWidth = Settings.INSPECTOR_WIDTH;
            world = new World(width, height);
            Globals.world = world;

            selection = new Selection(world);
            frame.setSize(width, height);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //3. Create components and put them in the frame.
            //...create emptyLabel...
            ArrayList<Entity> population = new ArrayList<>();

            try {
                list = SeedLoader.load(Settings.STARTING_POPULATION);
            } catch(Exception ex) {}
            if (list == null ) {
                population = Population.create(world, Settings.STARTING_POPULATION);
            } else if( list.size() < Settings.STARTING_POPULATION) {
                population = Population.create(world, Settings.STARTING_POPULATION);
            } else {
                Log.info("Loading from seed list:" + list.size());
                try {
                    population = SeedLoader.load(list, world);
                } catch(Exception ex) {
                    Log.info("Exception occurred:" + ex.getMessage());
                    population = Population.create(world, Settings.STARTING_POPULATION);
                }
            }
            world.setPopulation(population);
            world.setSelection(selection);
            KeyHandler keyHandler = new KeyHandler(world);
            frame.addKeyListener(keyHandler);

            world.setMaximumSize(new Dimension(width, height));
            world.setMinimumSize(new Dimension(width, height));
            world.setPreferredSize(new Dimension(width, height));


            frame.add(world, BorderLayout.CENTER);
            //4. Size the frame.

            //5. Show it.
            frame.setDefaultCloseOperation(
                    WindowConstants.EXIT_ON_CLOSE);

            frame.setUndecorated(true);
            frame.setResizable(false);
            //frame.add(new JLabel("This is a Java Screensaver!",
            //        SwingConstants.CENTER), BorderLayout.CENTER);
            //screenSaverFrame.validate();
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .setFullScreenWindow(frame);

            frame.setVisible(true);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    AWTThreadManager tm = new AWTThreadManager( frame, world );
                    tm.start();

                }
            });



        } catch(Exception ex) {
            Log.info(ex);
            ex.printStackTrace();
        } catch(Error e) {
            Log.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
