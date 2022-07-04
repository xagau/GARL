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
import javax.sound.sampled.*;
import java.util.ArrayList;

class SoundPlayer {

    private static final int BUFFER_SIZE = 1026;

    private AudioFormat format;
    private SourceDataLine line;

    private byte[] buffer;

    public void run(ArrayList<Double> sounds) {

        try {
            ArrayList<Byte> byteArrayList = new ArrayList<>();
            for (int i = 0; i < sounds.size(); i++) {
                Double d = sounds.get(i);
                byte[] arr = Utility.doubleToByteArray(d);
                for (int j = 0; j < arr.length; j++) {
                    byteArrayList.add(arr[j]);
                }
            }

            buffer = new byte[BUFFER_SIZE];
            for (int i = 0; i < buffer.length; i++) {
                if (i < byteArrayList.size()) {
                    buffer[i] = byteArrayList.get(i);
                }
            }

            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                while( !line.isOpen()) {
                    line.open();
                    line.start();
                }

            } catch (LineUnavailableException lue) {
                if(Globals.verbose) {
                    Log.info("Unavailable data line");
                }
            } catch(IllegalStateException ise){
                if( Globals.verbose) {
                    Log.info(ise.getMessage());
                }
            }

            for (int i = 0; i < Globals.repeat; i++) {

                line.write(buffer, 0, buffer.length);
            }

        } catch (Exception e) {
            if(Globals.verbose) {
                e.printStackTrace();
            }
        } finally {
            try {
                line.drain();
                line.stop();
                line.close();

            } catch(Exception ex) {
                if(Globals.verbose) {
                    ex.printStackTrace();
                }
            } catch(Error error){

            }
        }
    }
}

public class Bleep {
    public static void play(ArrayList<Double> play) {
        SoundPlayer bug = new SoundPlayer();
        bug.run(play);
    }

    public static void main(String[] args) {
        SoundPlayer bug = new SoundPlayer();
        ArrayList<Double> sounds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            sounds.add((Double) (Math.random() * 256));
        }
        bug.run(sounds);
    }

}
