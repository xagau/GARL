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
import garl.ann.NeuralLayer;
import garl.ann.NeuralNet;
import garl.ann.Neuron;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Brain {
    Entity entity = null;
    Genome genome = null;
    public NeuralNet ann = null;



    public void setOwner(Entity o) {
        entity = o;
    }

    public Brain(Entity owner, Genome genome) {
        ann = new NeuralNet(genome);
        this.genome = genome;
        this.genome.owner = owner;
        this.entity = owner;
    }

    public void calculate()
    {
        try {
            ann.input.calc();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String args[]) {
        World world = new World(1848, 1016);
        Entity e = new Entity(world);
        e.age = 100;
        e.process(Action.BROADCAST, world, 0);
        e.process(Action.BROADCAST, world, 0);
        e.process(Action.BROADCAST, world, 0);
        e.process(Action.BROADCAST, world, 0);
        e.process(Action.BROADCAST, world, 0);

        Message m = world.getChannel().listen();
        if (m != null) {
            int sz = m.getMessage().size();

            for (int i = 0; i < sz; i++) {
                System.out.println(m.getMessage().get(i));
            }
            e.brain.ann.input.calc();
            double d = e.brain.getOutput();

            DecimalFormat df = new DecimalFormat("0.0000000");
            Log.info(df.format(d));
        }

    }

    public synchronized void input(World world)
    {
        for(int i = 0; i < world.list.size(); i++ ){
            Entity e = world.list.get(i);
            if( e != null && e.alive ){
                entity.brain.input(e, world);
            }
        }
    }
    public synchronized void input(double input) {
        if (entity != null) {
            if (!entity.alive) {
                return;
            }
        } else {
            return;
        }
        if (entity != null && entity.brain != null && entity.brain.ann != null && entity.brain.ann.input != null && entity.brain.ann.input.input != null) {
            entity.brain.ann.input.input.add(input);
        } else {
            if (entity.brain == null) {
                Log.info("entity.brain is null");
            } else {
                if (entity.brain.ann == null) {
                    Log.info("entity.brain.ann is null");
                } else {
                    if (entity.brain.ann.input == null) {
                        Log.info("entity.brain.ann.input is null");
                    }
                }
            }
            //Log.info("entity.brain.ann.input.input was null");
        }

    }

    public synchronized void input(Entity e, double d) {
        ArrayList<Double> list = new ArrayList<>();
        list.add(d);
        try {
            if (e != null && e.alive) {
                e.brain.ann.input.input = list;
            } else {

                return;
            }
        } catch (Exception ex) {
            Log.info("in input(Entity, double) e.brain.ann.input.input was null");
            Log.info(ex);
        }
    }

    public synchronized void input(Entity e, World world) {
        ArrayList<Double> list = new ArrayList<>();
        try {
            if (e == null || world == null || entity == null || entity.brain == null) {
                if (e == null) {
                    Log.info("e is null");
                } else if (world == null) {
                    Log.info("world is null");
                } else if (entity == null) {
                    Log.info("entity is null");
                } else if (entity.brain == null) {
                    Log.info("entity.brain (top) is null");
                }
                return;
            }
            list.add((double) e.reward);
            list.add((double) e.getEnergy());
            list.add((double) e.age);
            list.add((double) e.location.x);
            list.add((double) e.location.y);
            list.add(e.location.vy);
            list.add(e.location.vx);
            list.add(e.distanceX);
            list.add(e.distanceY);
            list.add((double) world.width);
            list.add((double) world.height);
            list.add((double) Globals.spawn.x);
            list.add((double) Globals.spawn.y);
            list.add((double) Globals.spawn.width);
            list.add((double) Globals.spawn.height);
            list.add((double) Globals.spawn.getColor().getRGB());
            list.add((double) Globals.control.x);
            list.add((double) Globals.control.y);
            list.add((double) Globals.control.width);
            list.add((double) Globals.control.height);
            list.add((double) Globals.control.getColor().getRGB());
            list.add((double) (1d));
            list.add((double) Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE));

            list.add((double) (e.fertile ? 1d : 0d));
            list.add((double) (e.isTrajectoryGoal() ? 1d : 0d));
            list.add((double) (e.walls));
            list.add((double) e.size);
            list.add(e.getEnergy());
            list.add((double) KinFactory.create(e.genome.read(Gene.KIN)));
            list.add((double) e.genome.index);
            list.add((double) e.genome.read(e.genome.index));
            list.add((double) e.location.x - Globals.spawn.x);
            list.add((double) e.location.y - Globals.spawn.y);
            list.add((double) e.location.x - Globals.spawn.width);
            list.add((double) e.location.y - Globals.spawn.width);
            list.add((double) world.getLivingCount());
            long mlcs = Utility.checksum(e.genome.code);
            double mld = Utility.flatten(mlcs, Action.values().length);
            list.add(mld);
            for (int i = 0; i < world.selection.rlist.size(); i++) {
                list.add((double) world.selection.rlist.get(i).x);
                list.add((double) world.selection.rlist.get(i).y);
                list.add((double) world.selection.rlist.get(i).width);
                list.add((double) world.selection.rlist.get(i).height);
                list.add((double) (world.selection.rlist.get(i).kill ? 1d : 0d));
                list.add((double) world.selection.rlist.get(i).getColor().getRGB());
                list.add((double) (0d));
            }

            ArrayList<Entity> m = e.sampleForward();

            if (m != null) {
                for (int i = 0; i < m.size(); i++) {
                    try {
                        Entity ent = m.get(i);
                        if (ent != null) {
                            list.add((double) ent.location.x);
                            list.add((double) ent.location.y);
                            list.add((double) ent.location.vx);
                            list.add((double) ent.location.vy);
                            list.add((double) ent.age);
                            list.add((double) Settings.DEATH_MULTIPLIER * m.get(i).genome.read(Gene.AGE));
                            list.add((double) (ent.fertile ? 1d : 0d));
                            list.add((double) ent.size);

                            list.add((double) (ent.alive ? 1d : 0d));
                            list.add((double) ent.getEnergy());
                            list.add((double) (KinFactory.create(ent.genome.read(Gene.KIN))));
                            list.add((double) (ent.walls));
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }


            for (int i = 0; i < world.list.size(); i++) {
                try {
                    Entity ent = world.list.get(i);
                    if (ent != null) {
                        list.add((double) ent.location.x);
                        list.add((double) ent.location.y);
                        list.add((double) ent.location.vx);
                        list.add((double) ent.location.vy);
                        list.add((double) ent.age);
                        list.add((double) Settings.DEATH_MULTIPLIER * world.list.get(i).genome.read(Gene.AGE));
                        list.add((double) (ent.fertile ? 1d : 0d));
                        list.add((double) ent.size);

                        list.add((double) (ent.alive ? 1d : 0d));
                        list.add((double) ent.getEnergy());
                        list.add((double) (KinFactory.create(ent.genome.read(Gene.KIN))));
                        list.add((double) (ent.walls));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (e != null && e.alive) {
                    if (e.brain != null) {
                        if (e.brain.ann != null) {
                            e.brain.ann.input.input = list;
                        } else {
                            Log.info("brain.input(Entity e, World world) e.brain.ann was null (finally)");
                        }
                    } else {
                        Log.info("brain.input(Entity e, World world) entity.brain was null (finally)");
                    }
                }
            } catch (Exception ex) {
                Log.info("brain.input(Entity e, World world) entity.brain.ann.input.input was null (finally)");
            }
        }
    }

    public synchronized double getOutput(double d) {
        if (entity != null && entity.brain != null && entity.brain.ann != null && entity.brain.ann.input != null && entity.brain.ann.input.input != null) {
            entity.brain.ann.input.input.add(d);
            entity.brain.ann.output.calc();
            double t = entity.brain.ann.output.output.isEmpty() ? 0 : entity.brain.ann.output.output.get(0);
            return t;
        } else {
            if (entity == null) {
                Log.info("getOutput(double) entity was null in brain");
            } else if (entity.brain == null) {
                Log.info("getOutput(double) entity.brain.ann.input.input was null");
            } else if (entity.brain.ann == null) {
                Log.info("getOutput(double) entity.brain.ann was null");
            } else if (entity.brain.ann.input == null) {
                Log.info("getOutput(double) entity.brain.ann.input was null");
            } else if (entity.brain.ann.input.input == null) {
                Log.info("getOutput(double) entity.brain.ann.input.input was null");
            }
            return (double) Action.STOP.ordinal();
        }
    }

    public synchronized double getOutput() {
        if (entity != null) {
            return getOutput(entity);
        } else {
            Log.info(hashCode() + " entity not set");
            return -1;
        }
    }


    public synchronized double getOutput(Entity e) {

        try {
            if (e != null && e.brain != null && e.brain.ann != null && e.brain.ann.output != null) {
                NeuralLayer d = e.brain.ann.output;
                if (d != null) {
                    ArrayList<Neuron> n = d.neuron;
                    if (!d.neuron.isEmpty()) {
                        Neuron cur = d.neuron.get(0);
                        double r = 0;
                        if (cur == null) {
                            Log.info("cur is null");
                        } else {
                            r = cur.getOutput();
                            return r;
                        }
                    }
                } else {
                    return -1;
                }
            }
        } catch(Exception ex) {
            Log.info(ex);
            ex.printStackTrace();
        }
        return -1;
    }

    Action last = null;



    public synchronized Action evaluate(World world) {

        try {

            if (entity != null) {
                if (entity.target && entity.isTrajectoryGoal() && entity.walls <= 1) {
                    entity.location.vx = entity.targetvx;
                    entity.location.vy = entity.targetvy;
                    return Action.FASTER;
                } else if (entity.target && !entity.isTrajectoryGoal()) {
                    entity.process(Action.SLOW, world, 0);
                    entity.process(Action.SCAN, world, 0);
                }

            } else {
                Log.info("Brain with no body!");
            }
        } catch (Exception ex) {
            return Action.SCAN;
        }
        return Action.SCAN;
    }


    //public Action evaluate(World world) {

    //}

    public Action evaluate(Entity e, World world) {

        try {
            if (e != null) {
                input(e, world);
                ann.input.calc();
                double d = getOutput();
                Action a = ActionFactory.create(this.entity.genome.read(this.entity.genome.index + (int) d));
                this.genome.advance();
                if (this.entity.last != a) {
                    this.entity.last = a;
                    return a;
                } else {
                    d = Utility.flatten(d, (double) Action.values().length);
                    a = ActionFactory.create(this.entity.genome.read(this.entity.genome.index + (int) d));
                    this.entity.genome.advance();
                    this.entity.last = a;
                    return a;
                }
            } else {
                return Action.IF;
            }
        } catch (Exception ex) {
            return Action.IF;
        }

    }
}

