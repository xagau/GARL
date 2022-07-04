package garl;

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
                //System.out.println("Sound initialized successfully.");
            } catch (LineUnavailableException lue) {
                Log.info("Unavailable data line");
            } catch(IllegalStateException ise){
                Log.info(ise.getMessage());
            }

            for (int i = 0; i < Globals.repeat; i++) {
// fill with noise
                //for (int ii = 0; ii < buffer.length; ii++) {
                //    buffer[ii] = (byte) ((int) (Math.random() * 256) & 0xff);
                //}
                line.write(buffer, 0, buffer.length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                line.drain();
                line.stop();
                line.close();

            } catch(Exception ex) {
                ex.printStackTrace();
            } catch(Error error){

            }
        }

// dunno why this is necessary, but the javasound seems to not let the
// program end...


    }
}

public class Bleep {
    public static void play(ArrayList<Double> play) {
        SoundPlayer bug = new SoundPlayer();
        bug.run(play);
    }

    public static void main(String[] args) {
        // Create the AudioData object from the byte array
        //java.awt.Toolkit.getDefaultToolkit().beep();


        SoundPlayer bug = new SoundPlayer();
        ArrayList<Double> sounds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            sounds.add((Double) (Math.random() * 256));
        }
        bug.run(sounds);

        //byte[] byteArray = new byte[]{1,2,3,4,5,0,0,1,0,0,1};

        //ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        //Bleep bleep = new Bleep();
        //bleep.play(bis);

    }

}
