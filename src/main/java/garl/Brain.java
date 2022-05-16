package garl;

import garl.ann.NeuralLayer;
import garl.ann.NeuralNet;
import garl.ann.Neuron;

import java.util.ArrayList;

public class Brain {
    Entity entity = null;
    Genome genome = null;
    public NeuralNet ann = null;


    public Brain(Genome genome) {
        ann = new NeuralNet(genome);
        this.genome = genome;
    }

    public void setOwner(Entity o) {
        entity = o;
    }

    public Brain(Entity owner, Genome genome) {
        ann = new NeuralNet(genome);
        this.genome = genome;
        this.entity = owner;
    }


    public static void main(String args[]) {
        World world = new World(1848, 1016);
        Entity e = new Entity(world);
        e.age = 100;
        Action a = e.brain.evaluate(world);
        e.brain.ann.input.calc();
        double d = e.brain.getOutput();

    }

    public synchronized void input(Entity e, World world) {
        ArrayList<Double> list = new ArrayList<>();
        try {
            if (e == null) {
                Log.info("garl.Genome is null");
            }
            list.add((double) world.getWidth());
            list.add((double) world.getHeight());
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
            list.add((double) e.location.x);
            list.add((double) e.location.y);
            list.add(e.location.vy);
            list.add(e.location.vx);
            list.add(e.distanceX);
            list.add(e.distanceY);
            list.add((double) e.age);
            list.add((double) e.getEnergy());
            list.add((double) Settings.DEATH_MULTIPLIER * e.genome.read(Gene.AGE));

            list.add((double) (e.fertile ? 1d : 0d));
            list.add((double) (e.isTrajectoryGoal() ? 1d : 0d));
            list.add((double) (e.isTrajectoryDeath() ? 1d : 0d));
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

            for (int i = 0; i < m.size(); i++) {
                try {
                    list.add((double) m.get(i).location.x);
                    list.add((double) m.get(i).location.y);
                    list.add((double) m.get(i).location.vx);
                    list.add((double) m.get(i).location.vy);
                    list.add((double) m.get(i).age);
                    list.add((double) Settings.DEATH_MULTIPLIER * m.get(i).genome.read(Gene.AGE));
                    list.add((double) (m.get(i).fertile ? 1d : 0d));
                    list.add((double) m.get(i).size);

                    list.add((double) (m.get(i).alive ? 1d : 0d));
                    list.add((double) m.get(i).getEnergy());
                    list.add((double) (KinFactory.create(m.get(i).genome.read(Gene.KIN))));
                    list.add((double) (m.get(i).walls));
                    list.add((double) (m.get(i).reachedGoal ? 1d : 0d));
                } catch (Exception ex) {
                }
            }


            for (int i = 0; i < world.list.size(); i++) {
                try {
                    list.add((double) world.list.get(i).location.x);
                    list.add((double) world.list.get(i).location.y);
                    list.add((double) world.list.get(i).location.vx);
                    list.add((double) world.list.get(i).location.vy);
                    list.add((double) world.list.get(i).age);
                    list.add((double) Settings.DEATH_MULTIPLIER * world.list.get(i).genome.read(Gene.AGE));
                    list.add((double) (world.list.get(i).fertile ? 1d : 0d));
                    list.add((double) world.list.get(i).size);

                    list.add((double) (world.list.get(i).alive ? 1d : 0d));
                    list.add((double) world.list.get(i).getEnergy());
                    list.add((double) (KinFactory.create(world.list.get(i).genome.read(Gene.KIN))));
                    list.add((double) (world.list.get(i).walls));
                    list.add((double) (world.list.get(i).reachedGoal ? 1d : 0d));
                } catch (Exception ex) {
                }

            }
        } catch (Exception ex) {

        } finally {
            try {
                e.brain.ann.input.input = list;
            } catch (Exception ex) {
            }
        }
    }

    public double getOutput() {
        return getOutput(entity);
    }


    public double getOutput(Entity e) {
        if( e != null && e.brain != null && e.brain.ann != null && e.brain.ann.output != null ){
            NeuralLayer d = e.brain.ann.output;
            ArrayList<Neuron> n = d.neuron;
            Neuron nn = d.neuron.get(0);
            double r = 0;
            if (nn == null) {
                Log.info("nn is null");
            } else {
                r = nn.getOutput();
            }
            return r;
        } else {
            return Math.random() * Action.values().length ;
        }
    }

    Action last = null;



    public synchronized Action evaluate(World world) {

        try {

            if (entity != null) {
                if ( entity.target &&  entity.isTrajectoryGoal() && entity.walls <= 1) {
                    entity.location.vx = entity.targetvx;
                    entity.location.vy = entity.targetvy;
                    return Action.FASTER;
                } else if( entity.target && !entity.isTrajectoryGoal()) {
                    entity.process(Action.SLOW, world, 0);
                    entity.process(Action.SCAN, world, 0);
                } else {
                    input(entity, world);
                    try {
                        long s = System.currentTimeMillis();
                        entity.brain.ann.input.calc();
                        long e = System.currentTimeMillis();
                    } catch (Exception ex) {
                        return Action.CONTINUE;
                    }
                    long s = System.currentTimeMillis();
                    double d = entity.brain.getOutput();
                    double loc = entity.genome.index + (int) d;
                    double floc = 0;
                    char cfloc = 0;

                    if (Utility.isOdd((int) loc)) {
                        cfloc = entity.genome.read((int) loc);
                    } else {
                        entity.genome.advance();
                        cfloc = entity.genome.read((int) entity.genome.index());
                    }
                    if (cfloc == entity.genome.last()) {
                        entity.process(Action.RECODE, world, 1);
                    }
                    entity.input = floc;
                    Action a = ActionFactory.create((double) cfloc);
                    entity.genome.advance();
                    long e = System.currentTimeMillis();

                    if (entity.last != a) {
                        entity.last = a;
                        return a;
                    } else {
                        if( !entity.isTrajectoryGoal() ) {
                            return Action.SLOW;
                        }
                        long ss = System.currentTimeMillis();
                        input(entity, world);
                        entity.brain.ann.input.calc();
                        double dd = entity.brain.getOutput();
                        dd = Utility.flatten(dd, (double) Action.values().length);
                        a = ActionFactory.create(entity.genome.read(entity.genome.index + (int) dd));
                        entity.genome.advance();
                        entity.last = a;
                        long ee = System.currentTimeMillis();

                        return a;
                    }
                }
            }
        } catch (Exception ex) {
            return Action.SCAN;
        }
        return Action.SCAN;
    }

    public Action evaluate(Entity entity, World world) {

        try {

            if (entity != null) {
                input(entity, world);
                entity.brain.ann.input.calc();
                double d = entity.brain.getOutput();
                Action a = ActionFactory.create(entity.genome.read(entity.genome.index + (int) d));
                entity.genome.advance();
                if (entity.last != a) {
                    entity.last = a;
                    return a;
                } else {
                    d = Utility.flatten(d, (double) Action.values().length);
                    a = ActionFactory.create(entity.genome.read(entity.genome.index + (int) d));
                    entity.genome.advance();
                    entity.last = a;
                    return a;
                }
            } else {
                entity.genome.advance();
                entity.last = Action.IF;
                return Action.IF;
            }
        } catch (Exception ex) {
            return Action.IF;
        }

    }
}

