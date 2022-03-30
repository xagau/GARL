package garl;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Kernel {
    Graphics2D g2 = null;

    public Kernel(Graphics2D g2, int w, int h){
        this.g2 = g2;
        this.w = w;
        this.h = h;
        matrix = new int[w*h];

    }
    public int x, y;
    public int w, h;

    public int[] matrix = new int[w*h];

    public void transform(){
        for(int i = 0; i < matrix.length; i++ ){
            if( Utility.isOdd(matrix[i]) ){
                matrix[i] = Color.BLUE.getRGB();
            }
            //matrix[i] = matrix[i] << 128;
        }
        //shuffleArray(matrix);
    }

    static void shuffleArray(int[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
