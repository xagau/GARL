package garl;

import java.util.Random;

public class GenomeFactory {
    public static String create(int numSequence) {

        if (numSequence <= Settings.GENOME_LENGTH) {
            numSequence = Settings.GENOME_LENGTH;
        }
        String code = "";
        String str = "";
        for (int i = 0; i <= Settings.GENOME_LENGTH; i++) {
            Random r = new Random();
            char c = (char) (r.nextInt(Settings.CHAR_SET) + 'a');
            str += c;
        }
        for (int i = 0; i < numSequence; i++) {
            code += str;
        }
        code = code.replaceAll("-", "");
        return code;
    }

    public static String create(int numSequence, char c) {
        char[] chars = new char[numSequence];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = c;
        }
        return String.valueOf(chars);
    }
}

