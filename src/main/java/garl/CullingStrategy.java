package garl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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

public class CullingStrategy {

    public static int MAX_ENTITIES = 500;
    public static void main(String[] args)
    {
        cleanup();
    }
    public static void cleanup() {

        try {
            Log.info("Culling Strategy called");
            String genomePath = Property.getProperty("settings.genomes");
            if (genomePath == null) {
                genomePath = "./genomes/";
            }
            File dir = new File(genomePath);
            boolean f = dir.isDirectory();
            File[] list = dir.listFiles();

            int lowestReward = SeedLoader.lowestReward();
            ArrayList<Seed> seeds = SeedLoader.load();
            int limit = CullingStrategy.MAX_ENTITIES;
            int count = 0;
            Log.info(f + " genome source: " + genomePath + " " + list.length);
            for (int i = 0; i < seeds.size(); i++) {
                if( seeds.size() >= MAX_ENTITIES) {
                    Seed seed = (Seed) seeds.get(i);
                    if (seed.reward <= lowestReward) {
                        Log.info("Removing:" + seeds.get(i).file.getName() + " because reward = " + seeds.get(i).reward);
                        seeds.get(i).file.delete();
                        count++;
                    } else {
                        Log.info("Retain:" + seeds.get(i).file.getName() + " because reward = " + seeds.get(i).reward);
                    }
                }
            }

    } catch( IOException ex) {
            ex.printStackTrace();
            Log.info(ex);

    } catch(Exception ex) {
            ex.printStackTrace();
            Log.info(ex);
        } catch(Error e){
            e.printStackTrace();
            Log.info(e);
        }

    }
}
