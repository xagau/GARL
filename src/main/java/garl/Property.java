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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.Properties;

public class Property {
    public synchronized static String getRemoteProperty(String name){
        try {

            try {
                String text = "https://www.placeh.io/config.properties";
                URL url = new URL(text);
                URLConnection conn = url.openConnection();
                // fake request coming from browser
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line = in.readLine();
                Log.info("Loaded from remote:" + line);

                return line;
            } catch (Exception e) {
                Log.info(e);
                e.printStackTrace();
            }

        } catch(Exception ex) {
            Log.info(ex);
            ex.printStackTrace();
        } finally {
            DecimalFormat df = new DecimalFormat("0.00000000");

            return df.format(Globals.increment);
        }
    }

    public synchronized static String getProperty(String name)
    {
        try {
            String configPath = "./config.properties";
            if(Globals.screenSaverMode && Globals.installed){
                //Check if installed by installer
                File f = new File("C:/GARL/config.properties");
                if( f.exists() ) {
                    configPath = "C:/GARL/config.properties";
                    if( name.equals("settings.genomes")){
                        return "C:/GARL/genomes/";
                    }
                }

            }
            FileInputStream fis = new FileInputStream(new File(configPath));
            java.util.Properties property = new java.util.Properties();
            property.load(fis);
            String value = property.getProperty(name);
            fis.close();
            return value;

        } catch(Exception ex) {
            Log.info("getProperty:"+ ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        Log.info(getRemoteProperty("settings.increment"));
    }
}
