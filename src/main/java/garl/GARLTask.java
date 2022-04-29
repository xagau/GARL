package garl;

import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.*;


public class GARLTask extends Thread {

    public static UUID run = UUID.randomUUID();
    ArrayList<Seed> list = null;
    final static int FPS = 64;

    public GARLTask(ArrayList<Seed> list) {
        if (list != null) {
            this.list = list;
        }
    }


    public static ArrayList<Seed> load() throws IOException {
        ArrayList<Seed> list = new ArrayList<>();
        String seed = "./genomes/";
        Gson gson = new Gson(); //null;
        // create a reader
        File dir = new File(seed);
        //File[] listFiles = dir.listFiles();
        File[] listFiles = dir.listFiles();
        File[] files = dir.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        int ctr = 0;
        for (int i = files.length - 1; i >= 0; i--) {
            File f = files[i];
            if (f.getName().contains("genome")) {
                String fName = f.getName();
                Log.info("Using garl.Entity:" + f.getName());
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


    public static void main(String[] args) throws IOException {


        ArrayList<Seed> list = new ArrayList<>();
        if (args.length > 0) {
            list = load();
        }

        GARLTask task = new GARLTask(list);
        task.start();

    }

    static JFrame frame = new JFrame("Genetic Based Multi-Agent Reinforcement Learning");

    JPanel inspector = new JPanel();
    public static World world = null;
    Selection selection = null;
    public static JPanel inspectorContainer = new JPanel();

    public static NNCanvas canvas = null;

    public void run() {

        //1. Create the frame.

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int width = 1800;
        int height = 1000;
        int inspectorPanelWidth = Settings.INSPECTOR_WIDTH;
        world = new World(width - inspectorPanelWidth, height);
        selection = new Selection(world);
        frame.setSize(width, height);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        ArrayList<Entity> population = new ArrayList<>();
        if (list == null) {
            population = Population.create(world, Settings.STARTING_POPULATION, frame.getWidth() - inspectorPanelWidth, frame.getWidth());
        } else {
            Log.info("Loading from seed list:" + list.size());
            for (int i = 0; i < Math.min(list.size(), Settings.STARTING_POPULATION); i++) {
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
                    e.location.x = (int) (Math.random() * width - inspectorPanelWidth);
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

        world.setMaximumSize(new Dimension(width - inspectorPanelWidth, height));
        world.setMinimumSize(new Dimension(width - inspectorPanelWidth, height));
        world.setPreferredSize(new Dimension(width - inspectorPanelWidth, height));

        inspector.setMaximumSize(new Dimension(inspectorPanelWidth, height));
        inspector.setMinimumSize(new Dimension(inspectorPanelWidth, height));
        inspector.setPreferredSize(new Dimension(inspectorPanelWidth, height));

        GridLayout gridLayout = new GridLayout(14, 2);
        inspector.setLayout(gridLayout);
        inspector.add(new JLabel("Starting GARL Population"));
        JTextField startingPopulation = new JTextField("" + Settings.STARTING_POPULATION);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String text = startingPopulation.getText();
                    Integer value = Integer.parseInt(text);
                    Settings.STARTING_POPULATION = value;
                    Log.info("Set starting population to:" + value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        startingPopulation.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Integer.parseInt(startingPopulation.getText()) <= 0) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number bigger than 0", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = startingPopulation.getText();
                        try {
                            Integer value = Integer.parseInt(text);
                            Settings.STARTING_POPULATION = value;
                            Log.info("Set starting population to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive integers allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });
        DecimalFormat ddf = new DecimalFormat("0.00000");
        startingPopulation.addActionListener(al);
        inspector.add(startingPopulation);
        inspector.add(new JLabel("Minimum garl.Gene Pool"));
        JTextField genePool = new JTextField("" + Settings.GENE_POOL);
        genePool.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Integer.parseInt(genePool.getText()) < 0) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number bigger 0 or larger", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = genePool.getText();
                        try {
                            Integer value = Integer.parseInt(text);
                            Settings.GENE_POOL = value;
                            Log.info("Set GENE POOL to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive integers allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });
        inspector.add(genePool);
        inspector.add(new JLabel("Minimum Size"));
        JTextField minSize = new JTextField("" + Settings.MIN_SIZE);
        minSize.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Integer.parseInt(minSize.getText()) <= 3) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number bigger 3 or larger", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = minSize.getText();
                        try {
                            Integer value = Integer.parseInt(text);
                            Settings.MIN_SIZE = value;
                            Log.info("Set min size to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive integers allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) {ex.printStackTrace();}
            }
        });
        inspector.add(minSize);
        inspector.add(new JLabel("Maximum Size"));
        JTextField maxSize = new JTextField("" + Settings.MAX_SIZE);
        maxSize.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Integer.parseInt(maxSize.getText()) >= 30) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number 30 or less", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = maxSize.getText();
                        try {
                            Integer value = Integer.parseInt(text);
                            Settings.MAX_SIZE = value;
                            Log.info("Set max size to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive integers allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) { ex.printStackTrace(); }
            }
        });
        inspector.add(maxSize);
        inspector.add(new JLabel("Starting Energy"));
        JTextField initialEnergy = new JTextField("" + ddf.format(Settings.ENERGY));
        initialEnergy.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Double.parseDouble(initialEnergy.getText()) >= 100) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number 100 or less", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = initialEnergy.getText();
                        try {
                            Double value = Double.parseDouble(text);
                            Settings.ENERGY = value;
                            Log.info("Set initial energy to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive doubles allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) {ex.printStackTrace();}
            }
        });
        inspector.add(initialEnergy);
        inspector.add(new JLabel("Energy per Step"));
        JTextField energyStepCost = new JTextField("" + ddf.format(Settings.ENERGY_STEP_COST));
        energyStepCost.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Double.parseDouble(energyStepCost.getText()) >= 0.1) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number 0.1 or less", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = energyStepCost.getText();
                        try {
                            Double value = Double.parseDouble(text);
                            Settings.ENERGY_STEP_COST = value;
                            Log.info("Set energy step cost to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive doubles allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) {ex.printStackTrace();}
            }
        });
        inspector.add(energyStepCost);
        inspector.add(new JLabel("Energy per Sleep"));
        JTextField energyStepCostSleep = new JTextField("" + ddf.format(Settings.ENERGY_STEP_SLEEP_COST));
        energyStepCostSleep.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Double.parseDouble(energyStepCostSleep.getText()) >= 0.1) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number 0.1 or less", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = energyStepCostSleep.getText();
                        try {
                            Double value = Double.parseDouble(text);
                            Settings.ENERGY_STEP_SLEEP_COST = value;
                            Log.info("Set energy step sleep cost to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive doubles allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex){ex.printStackTrace();}
            }
        });
        inspector.add(energyStepCostSleep);
        inspector.add(new JLabel("Maximum Offspring"));
        JTextField maximumOffstring = new JTextField("" + Settings.MAX_OFFSPRING);
        maximumOffstring.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Integer.parseInt(maximumOffstring.getText()) >= 2) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number 2 or more", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = maximumOffstring.getText();
                        try {
                            Integer value = Integer.parseInt(text);
                            Settings.MAX_OFFSPRING = value;
                            Log.info("Set max offspring to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive integers allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch(Exception ex) { ex.printStackTrace();}
            }
        });
        inspector.add(maximumOffstring);
        inspector.add(new JLabel("Neurons in Layer (0)"));
        JTextField neuronsInBaseLayer = new JTextField("" + Settings.NUMBER_OF_INPUTS);
        neuronsInBaseLayer.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                try {
                    if (Integer.parseInt(neuronsInBaseLayer.getText()) >= 8) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Please enter number 8 or more", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String text = neuronsInBaseLayer.getText();
                        try {
                            Integer value = Integer.parseInt(text);
                            Settings.NUMBER_OF_INPUTS = value;
                            Log.info("Set neurons in base layer to:" + value);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error: Only positive integers allowed", "Error Message",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        inspector.add(neuronsInBaseLayer);

        inspector.add(new JLabel("Reset"));
        JButton reset = new JButton("Clear");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    for (int i = 0; i < world.list.size(); i++) {
                        Entity e = (Entity) world.list.get(i);
                        e.die();
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        inspector.add(reset);
        inspector.add(new JLabel("Earnings"));
        JButton payout = new JButton("Payout");
        ActionListener payoutActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {
                    for (int i = 0; i < world.list.size(); i++) {
                        Entity e = (Entity) world.list.get(i);
                        e.die();
                    }

                    MoneyMQ mq = new MoneyMQ();
                    DecimalFormat df = new DecimalFormat("0.00000000");
                    mq.send(Settings.PAYOUT_ADDRESS, "" + df.format(world.phl));
                    world.phl = 0;
                    world.totalControls = 0;
                    world.totalSpawns = 0;

                } catch(Exception ex) {
                    Log.info(ex);
                    ex.printStackTrace();
                }

            }
        };
        payout.addActionListener(payoutActionListener);
        inspector.add(payout);
        inspector.add(new JLabel("Address"));
        String address = Settings.PAYOUT_ADDRESS;
        JTextField payoutAddress = new JTextField();
        Font font = new Font("Courier", Font.BOLD,10);
        payoutAddress.setFont( font );

        payoutAddress.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (payoutAddress.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter valid address", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String text = payoutAddress.getText();
                    try {
                        Settings.PAYOUT_ADDRESS = text;
                        Log.info("Set payout address to:" + text);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Error: Only valid addresses allowed", "Error Message",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        payoutAddress.setText(address);
        inspector.add(payoutAddress);
        inspector.add(new JLabel("Selected Agent ANN"));

        inspectorContainer.setLayout(new BorderLayout());
        inspectorContainer.add(new JPanel(), BorderLayout.NORTH);
        inspectorContainer.add(new JPanel(), BorderLayout.EAST);
        inspectorContainer.add(new JPanel(), BorderLayout.WEST);

        JPanel selectedInspector = new JPanel();
        canvas = new NNCanvas(world);
        canvas.setMinimumSize(new Dimension(inspectorPanelWidth, inspectorPanelWidth));
        canvas.setMaximumSize(new Dimension(inspectorPanelWidth, inspectorPanelWidth));
        canvas.setPreferredSize(new Dimension(inspectorPanelWidth, inspectorPanelWidth));
        selectedInspector.add(canvas);
        inspectorContainer.add(selectedInspector, BorderLayout.SOUTH);

        inspectorContainer.add(inspector, BorderLayout.CENTER);


        frame.add(world, BorderLayout.CENTER);
        frame.add(inspectorContainer, BorderLayout.EAST);

        //4. Size the frame.

        //5. Show it.
        frame.setVisible(true);

        ThinkTask think = new ThinkTask(frame, world, width - inspectorPanelWidth, height, 5);
        SelectionTask selection = new SelectionTask(frame, world, width - inspectorPanelWidth, height);
        ReplicationTask replication = new ReplicationTask(frame, world, width - inspectorPanelWidth, height);
        EntityTask entityTask = new EntityTask(frame, canvas, world, width - inspectorPanelWidth, height);

        Timer timer = new Timer(true);
        TimerTask paint = new TimerTask() {
            int ctr = 0;

            @Override
            public void run() {

                try {
                    long start = System.currentTimeMillis();
                    Globals.semaphore.acquire();
                    ctr++;
                    if (ctr > 100) {
                        ctr = 0;
                    }
                    world.repaint();
                    //canvas.repaint();
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

        long taskTime = 120;
        timer.scheduleAtFixedRate(paint, 0, 1000 / FPS);
        timer.scheduleAtFixedRate(think, 0, taskTime);
        timer.scheduleAtFixedRate(selection, 50, taskTime);
        timer.scheduleAtFixedRate(replication, 100, taskTime);
        timer.scheduleAtFixedRate(entityTask, 100, taskTime);

    }

}

