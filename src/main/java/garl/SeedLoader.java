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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SeedLoader {
    public synchronized static int lowestReward() throws IOException {

        int lowestReward = Integer.MAX_VALUE;
        Log.info("lowest reward");
        ArrayList<Seed> list = load(count());
        for(int i = 0; i < list.size(); i++ ){
            Seed s = (Seed)list.get(i);
            if( s.reward < lowestReward ) {
                lowestReward = s.reward;
            }
        }

        return lowestReward;
    }

    public synchronized static int lowestPenalty() throws IOException {

        int lowestPenalty = Integer.MAX_VALUE;
        Log.info("lowest penalty");
        ArrayList<Seed> list = load(count());
        for(int i = 0; i < list.size(); i++ ){
            Seed s = (Seed)list.get(i);
            if( s.penalty < lowestPenalty ) {
                lowestPenalty = s.penalty;
            }
        }

        return lowestPenalty;
    }

    public synchronized static int highestPenalty() throws IOException {

        int highestPenalty = Integer.MIN_VALUE;
        Log.info("highest penalty");
        ArrayList<Seed> list = load(count());
        for(int i = 0; i < list.size(); i++ ){
            Seed s = (Seed)list.get(i);
            if( s.penalty > highestPenalty ) {
                highestPenalty = s.penalty;
            }
        }

        return highestPenalty;
    }

    public synchronized static int count()
    {
        String seed = "./genomes/";
        Gson gson = new Gson(); //null;
        // create a reader
        File dir = new File(seed);

        File[] listFiles = dir.listFiles();
        if(listFiles != null) {
            return listFiles.length;
        } else {
            return 0;
        }
    }
    public synchronized static int highestReward() throws IOException {


        int highestReward = Integer.MIN_VALUE;
        Log.info("highest reward");
        ArrayList<Seed> list = load(count());
        for(int i = 0; i < list.size(); i++ ){
            Seed s = (Seed)list.get(i);
            if( s.reward > highestReward ) {
                highestReward = s.reward;
            }
        }

        return highestReward;
    }

    public synchronized static ArrayList<Seed> load() {
        Log.info("load()");
        return load(count());
    }

    public synchronized static ArrayList<Seed> load(int max) {
        Log.info("load(int max)");
        ArrayList<Seed> list = new ArrayList<Seed>();
        String seed = "./genomes/";
        Gson gson = new Gson(); //null;
        // create a reader
        File dir = new File(seed);

        File[] files = dir.listFiles();
        Log.info("Found:" + files.length + " files in the genome folder");
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        int ctr = 0;
        for (int i = files.length - 1; i >= 0; i--) {
            File f = files[i];
            if (f.getName().contains("genome")) {
                try {
                    String fName = f.getName();

                    Reader reader = Files.newBufferedReader(Paths.get(seed + fName));
                    try {
                        Seed lseed = (Seed) gson.fromJson(reader, Seed.class);
                        lseed.seedName = f.getName();
                        lseed.file = Paths.get(seed + fName).toFile();
                        list.add(lseed);
                        ctr++;
                    } catch (Exception ex) {
                        if (Globals.verbose) {
                            Log.info(ex);
                        }
                    }
                    reader.close();
                } catch(Exception ex){}
            }
        }

        Log.info("list size:" + list.size());



        // CODE BELOW SELECTS MOST FIT INDIVIDUALS BASED ON METRICS - REQUIRES REFACTORING


        Comparator comparator = new Comparator<Seed>(){

            public int compare(Seed e1, Seed e2) {
                if (e1.reward - e1.penalty == e2.reward - e2.penalty) {
                    return 0;
                } else if (e1.reward - e1.penalty < e2.reward - e2.penalty) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };



        Seed[] arr = new Seed[list.size()];
        for(int i=0; i < list.size(); i++ ){
            try {
                arr[i] = list.get(i);
            } catch(Exception ex){
                Log.info(ex);
                ex.printStackTrace();
            }
        }
        Arrays.sort(arr, comparator);
        for(int i =0; i < arr.length; i++ ){
            if (i > max) {
                break;
            }
            Log.info("generation:" + arr[i].generation + " genome length:" + arr[i].genome.length() + " reward:" + arr[i].reward + " penalty:" + arr[i].penalty);
        }
        list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(arr[i]);
        }

        Log.info("list size:" + list.size());

        return list;
    }

    public static ArrayList<Entity> load(ArrayList<Seed> seeds, World world)  {
        ArrayList<Seed> list = seeds;
        ArrayList<Entity> ents = new ArrayList<>();

        Log.info("SeedLoader.load(ArrayList<Seed> seeds, World world)");
        for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
            if (list.get(i).genome.contains("-")) {
                continue;
            }
            try {
                String genome = list.get(i).genome;
                Log.info("Adding:" + i + ":" + genome);
                Genome g = new Genome(genome);
                Entity e = new Entity(world);
                Brain brain = new Brain(e, g);
                brain.setOwner(e);
                e.location.x = (int) (Math.random() * world.width);
                e.location.y = (int) (Math.random() * world.height);
                g.setOwner(e);
                e.brain = brain;
                try {
                    e.reward = list.get(i).reward;
                } catch (Exception ex) {
                }
                try {
                    e.penalty = list.get(i).penalty;
                } catch (Exception ex) {
                }

                e.genome = g;
                ents.add(e);
                Log.info("Added:" + i + " at " + e.location.x + " " + e.location.y);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list.size() < Settings.STARTING_POPULATION) {
            Log.info("Not enough genomes, using random starting population");
            ents = Population.create(world, Settings.STARTING_POPULATION);
        }

        return ents;
    }

    public static void main(String[] args) {
        try {
            SeedLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
