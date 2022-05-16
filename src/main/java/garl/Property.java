package garl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Property {
    public static String getRemoteProperty(String name){
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
            return "0.0000100";
        }
    }

    public static String getProperty(String name)
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
            return value;

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        System.out.println(getRemoteProperty("settings.increment"));
    }
}
