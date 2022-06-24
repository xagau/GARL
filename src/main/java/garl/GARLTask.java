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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;


public class GARLTask extends Thread {

    public static UUID run = UUID.randomUUID();
    ArrayList<Seed> list = null;

    public GARLTask(ArrayList<Seed> list) {
        if (list != null) {
            this.list = list;
        }
    }




    public static void main(String[] args)  {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ArrayList<Seed> list = new ArrayList<>();
                if (args.length > 0) {
                    list = SeedLoader.load();
                }

                GARLTask task = new GARLTask(list);
                task.start();

            }
        });


    }

    static GARLFrame frame = new GARLFrame(Globals.title);

    JPanel inspector = new JPanel();
    public static World world = null;
    Selection selection = null;
    public static JPanel inspectorContainer = new JPanel();



    public void run() {

        try {
            CullingStrategy.cleanup();

            try {
                Globals.increment = Double.parseDouble(Property.getRemoteProperty("settings.increment"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        //1. Create the frame.

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (screenSize.width);
        int height = (screenSize.height);

        int inspectorPanelWidth = Settings.INSPECTOR_WIDTH;
        world = new World(width - inspectorPanelWidth, height);
        Globals.world = world;
        selection = new Selection(world);
        world.selection = selection;
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        ArrayList<Entity> population = new ArrayList<>();
        try {
            list = SeedLoader.load();
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

        world.setMaximumSize(new Dimension(width - inspectorPanelWidth, height));
        world.setMinimumSize(new Dimension(width - inspectorPanelWidth, height));
        world.setPreferredSize(new Dimension(width - inspectorPanelWidth, height));

        inspector.setMaximumSize(new Dimension(inspectorPanelWidth, height));
        inspector.setMinimumSize(new Dimension(inspectorPanelWidth, height));
        inspector.setPreferredSize(new Dimension(inspectorPanelWidth, height));

        GridLayout gridLayout = new GridLayout(14, 2, 2, 32);
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
        inspector.add(new JLabel("Minimum GARL Gene Pool"));
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
        inspectorContainer.addFocusListener(new FocusAdapter() {
        });
        inspector.addMouseListener(new MouseListener(){


            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                inspector.requestFocus(true);
                try {
                    Thread.currentThread().setPriority(0);
                } catch(Exception ex){}
           }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                inspector.requestFocus(false);
                try {
                    Thread.currentThread().setPriority(20);
                } catch(Exception ex){}

            }
        });
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
                } catch(Exception ex) {if( Globals.verbose) { ex.printStackTrace(); }}
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
                } catch(Exception ex) { if(Globals.verbose) {ex.printStackTrace();}}
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

                Log.info("Payout called using:" + Settings.PAYOUT_ADDRESS);
                try {
                    try {
                        for (int i = 0; i < world.list.size(); i++) {
                            Entity e = (Entity) world.list.get(i);
                            e.die();
                        }
                    } catch(Exception ex){
                        if(Globals.verbose){
                            Log.info(ex);
                        }
                    }

                    DecimalFormat df = new DecimalFormat("0.00000000");
                    Globals.mq.send(Settings.PAYOUT_ADDRESS, "" + df.format(world.phl));
                    world.phl = 0;
                    world.totalControls = 0;
                    world.totalSpawns = 0;


                } catch(Exception ex) {
                    Log.info(ex);
                    ex.printStackTrace();
                } catch(Error e){
                    Log.info(e);
                    e.printStackTrace();
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

        inspectorContainer.setLayout(new BorderLayout());
        inspectorContainer.add(new JPanel(), BorderLayout.NORTH);
        inspectorContainer.add(new JPanel(), BorderLayout.EAST);
        inspectorContainer.add(new JPanel(), BorderLayout.WEST);

        inspectorContainer.add(inspector, BorderLayout.CENTER);

        frame.add(world, BorderLayout.CENTER);
        frame.add(inspectorContainer, BorderLayout.EAST);

        //4. Size the frame.

        //5. Show it.
        frame.setVisible(true);

        AWTThreadManager tm = new AWTThreadManager(frame, world);
        tm.start();


    }

}

