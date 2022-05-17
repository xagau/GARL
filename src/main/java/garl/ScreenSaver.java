package garl;

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


    /*
    public static ArrayList<Seed> load() throws IOException {
        ArrayList<Seed> list = new ArrayList<>();
        String seed = Property.getProperty("settings.genomes"); //"./genomes/";
        if( seed == null ){
            seed = "./genomes/";
        }
        Gson gson = new Gson();
        // create a reader
        File dir = new File(seed);

        File[] listFiles = dir.listFiles();
        File[] files = dir.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        int ctr = 0;
        for (int i = files.length - 1; i >= 0; i--) {
            File f = files[i];
            if (f.getName().contains("genome")) {
                String fName = f.getName();
                Log.info("Using entity:" + f.getName());
                Reader reader = Files.newBufferedReader(Paths.get(seed + fName));
                try {
                    Seed lseed = (Seed) gson.fromJson(reader, Seed.class);
                    list.add(lseed);
                    ctr++;
                } catch (Exception ex) {
                }
            }
            if (ctr >= Settings.STARTING_POPULATION) {
                break;
            }
            // convert JSON string to User object
        }
        return list;
    }

    public static ArrayList<Entity> load(ArrayList<Seed> seeds, World world) throws IOException {
        ArrayList<Seed> list = seeds;
        ArrayList<Entity> ents = new ArrayList<>();

        for (int i = 0; i < Math.min(list.size() > Settings.STARTING_POPULATION ? list.size() : Settings.STARTING_POPULATION, Settings.STARTING_POPULATION); i++) {
            if (list.get(i).genome.contains("-")) {

                continue;
            }
            try {
                String genome = list.get(i).genome;
                Log.info("Adding:" + i + ":" + genome);
                Genome g = new Genome(genome);
                Brain brain = new Brain(g);
                Entity e = new Entity(world);
                brain.setOwner(e);
                e.location.x = (int) (Math.random() * world.getWidth());
                e.location.y = (int) (Math.random() * world.getHeight());
                g.setOwner(e);
                e.brain = brain;

                e.genome = g;
                ents.add(e);
                Log.info("Added:" + i + " at " + e.location.x + " " + e.location.y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ents;
    }
    */

    static GARLFrame frame = new GARLFrame();

    static JPanel inspector = new JPanel();
    public static World world = null;
    static Selection selection = null;
    public static JPanel inspectorContainer = new JPanel();

    public static NNCanvas canvas = null;


    public static final void main(final String[] args)  {
        try {
            Utility.cleanup();

            try {
                World.increment = Double.parseDouble(Property.getRemoteProperty("settings.increment"));
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            Log.info("Running in Screen Saver Mode:");
            Globals.screenSaverMode = true;
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            ArrayList<Seed> list = new ArrayList<>();
            if (args.length >= 0) {
                list = SeedLoader.load();
                if (list != null) {
                    Log.info(list.size());
                } else {
                    Log.info("List is null");
                }
            }

            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            if (list == null ) {
                population = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
            } else if( list.size() < Settings.STARTING_POPULATION) {
                population = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth(), frame.getHeight());
            } else {
                Log.info("Loading from seed list:" + list.size());

                for (int i = 0; i < Math.min(list.size(), Settings.MAX_POPULATION); i++) {
                    if (list.get(i).genome.contains("-")) {

                        continue;
                    }
                    try {
                        String genome = list.get(i).genome;
                        Log.info("Adding:" + i + ":" + genome);
                        Genome g = new Genome(genome);
                        Brain brain = new Brain(g);
                        Entity e = new Entity(world);
                        brain.setOwner(e);
                        e.location.x = (int) (Math.random() * width);
                        e.location.y = (int) (Math.random() * height);
                        g.setOwner(e);
                        e.brain = brain;

                        e.genome = g;
                        population.add(e);
                        Log.info("Added:" + i + " at " + e.location.x + " " + e.location.y);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            world.setPopulation(population);
            world.setSelection(selection);
            MouseHandler mouseHandler = new MouseHandler(world, canvas);
            KeyHandler keyHandler = new KeyHandler(world);
            world.addMouseMotionListener(mouseHandler);
            world.addMouseListener(mouseHandler);
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

            //PayoutTask payout = new PayoutTask(frame, world, width, height);
            ThinkTask think = new ThinkTask(frame, world, width, height, 5);
            SelectionTask selection = new SelectionTask(frame, world, width, height);
            ReplicationTask replication = new ReplicationTask(frame, world, width, height);
            EntityTask entityTask = new EntityTask(frame, canvas, world, width, height);

            java.util.Timer timer = new Timer(true);
            TimerTask paint = new TimerTask() {
                int ctr = 0;

                @Override
                public void run() {

                    try {
                        long start = System.currentTimeMillis();
                        Globals.semaphore.acquire();
                        ctr++;
                        if (ctr > Globals.cleanupTime) {
                            Runtime.getRuntime().gc();
                            ctr = 0;
                        }
                        world.repaint();
                        long end = System.currentTimeMillis();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } catch (Error e) {
                        e.printStackTrace();
                    } finally {
                        Globals.semaphore.release();
                    }
                }
            };

            TimerTask payoutTask = new TimerTask() {
                int ctr = 0;
                @Override
                public void run() {

                    try {
                        long start = System.currentTimeMillis();
                        Globals.semaphore.acquire();

                        double phl = world.phl;
                        world.phl = 0;
                        DecimalFormat df = new DecimalFormat("0.00000000");
                        Globals.mq.send(Settings.PAYOUT_ADDRESS, "" + df.format(phl));
                        if( ctr++ > Globals.cleanupTime ) {
                            Runtime.getRuntime().gc();
                            ctr = 0;
                        }

                        long end = System.currentTimeMillis();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } catch (Error e) {
                        e.printStackTrace();
                    } finally {
                        Globals.semaphore.release();
                    }
                }
            };


            try {
                timer.scheduleAtFixedRate(paint, 0, 1000 / Globals.FPS);
                timer.scheduleAtFixedRate(think, 0, Globals.taskTime);
                timer.scheduleAtFixedRate(selection, 50, 50);
                //timer.scheduleAtFixedRate(replication, 100, Globals.taskTime);
                timer.scheduleAtFixedRate(entityTask, 100, Globals.taskTime);
                timer.scheduleAtFixedRate(payoutTask, 100, Globals.HOUR);

            } catch(Exception ex) {
                ex.printStackTrace();
                Log.info(ex);
            } catch (Error e){
                e.printStackTrace();
                Log.info(e.getMessage());
            }

            ShutdownHook hook = new ShutdownHook(frame);
            Runtime.getRuntime().addShutdownHook(hook);
            frame.addWindowListener(new WindowListener() {

                @Override
                public void windowOpened(WindowEvent windowEvent) {

                }

                @Override
                public void windowClosing(WindowEvent windowEvent) {


                }

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    timer.purge();
                    frame.setVisible(false);
                    frame.dispose();
                    Runtime.getRuntime().exit(0);
                }

                @Override
                public void windowIconified(WindowEvent windowEvent) {

                }

                @Override
                public void windowDeiconified(WindowEvent windowEvent) {

                }

                @Override
                public void windowActivated(WindowEvent windowEvent) {

                }

                @Override
                public void windowDeactivated(WindowEvent windowEvent) {

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
