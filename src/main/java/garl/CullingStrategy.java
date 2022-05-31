package garl;

import java.io.File;
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
    public static void cleanup() {

        try {
            Log.info("Culling Strategy called");
            String genomePath = Property.getProperty("settings.genomes");
            if( genomePath == null ){
                genomePath = "./genomes/";
            }
            File dir = new File(genomePath);
            boolean f = dir.isDirectory();
            File[] list = dir.listFiles();

            ArrayList<Seed> slist = SeedLoader.load();
            if(slist.isEmpty() && list == null && list.length == 0){
                return;
            }
            Log.info("Seed List size to select fit individuals:" + slist.size());
            int limit = CullingStrategy.MAX_ENTITIES;
            int count = 0;
            Log.info(f + " genome source: " + genomePath + " " + list.length);
            Log.info(f + " seed source: " + genomePath + " " + slist.size());
            for(int i = 0; i < list.length; i++ ) {
                String name = list[i].getName();
                Date d = new Date(list[i].lastModified());
                if (name.contains("genome")) {
                    if (count++ > limit) {
                        Log.info("Delete:" + list[i].getName() + " because " + d.toString());
                        // TODO Filter by fitness.
                        list[i].delete();
                    } else {
                        Log.info("Retain:" + list[i].getName() + " because " + d.toString());

                    }
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            Log.info(ex);
        } catch(Error e){
            e.printStackTrace();
            Log.info(e);
        }

    }
}
