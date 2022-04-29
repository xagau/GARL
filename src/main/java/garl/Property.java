package garl;

import java.io.File;
import java.io.FileInputStream;

public class Property {
    public static String getProperty(String name)
    {
        try {
            FileInputStream fis = new FileInputStream(new File("./config.properties"));
            java.util.Properties property = new java.util.Properties();
            property.load(fis);
            String value = property.getProperty(name);
            return value;

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
