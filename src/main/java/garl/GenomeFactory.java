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

