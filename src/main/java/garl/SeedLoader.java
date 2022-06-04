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
import java.util.Set;

public class SeedLoader {

    public synchronized static ArrayList<Seed> load() throws IOException {
        ArrayList<Seed> list = new ArrayList<Seed>();
        String seed = "./genomes/";
        Gson gson = new Gson(); //null;
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
                Log.info("Using GARL Entity: [" + ctr + "] " + f.getName());

                    Reader reader = Files.newBufferedReader(Paths.get(seed + fName));
                    try {
                        Seed lseed = (Seed) gson.fromJson(reader, Seed.class);
                        lseed.seedName = f.getName();
                        list.add(lseed);
                        ctr++;
                    } catch (Exception ex) {
                        if( Globals.verbose ) {
                            Log.info(ex);
                        }
                    }
            }
            if (ctr >= CullingStrategy.MAX_ENTITIES) {
                break;
            }
        }

        Log.info("list size:" + list.size());



        // CODE BELOW SELECTS MOST FIT INDIVIDUALS BASED ON METRICS - REQUIRES REFACTORING


        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Seed s1 = (Seed)o1;
                Seed s2 = (Seed)o2;
                if( s1.generation>s2.generation){
                    return -1;
                } else if( s1.generation == s2.generation ){
                    return 0;
                }
                return 1;
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
            if( i > Settings.STARTING_POPULATION){
                break;
            }
            Log.info(arr[i].generation + " " + arr[i].genome);
        }
        list = new ArrayList<>();
        for(int i =0; i < arr.length; i++ ){
            list.add(arr[i]);
        }

        return list;
    }

    public static ArrayList<Entity> load(ArrayList<Seed> seeds, World world) throws IOException {
        ArrayList<Seed> list = seeds;
        ArrayList<Entity> ents = new ArrayList<>();

        for (int i = 0; i < Settings.STARTING_POPULATION; i++) {
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

        if( list.size() < Settings.STARTING_POPULATION){
            ents = Population.create(world, Settings.STARTING_POPULATION);
        }

        return ents;
    }

}
