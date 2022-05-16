package garl;

import java.util.Random;

public class Genome {
    static String DEAD = GenomeFactory.create(Settings.GENOME_LENGTH * Settings.GENOME_LENGTH, '-');
    Entity owner = null;
    String code = null;
    int numAppends = 0;
    int numDeletions = 0;
    int numRecodes = 0;

    public Genome(Entity owner) {
        code = GenomeFactory.create(Settings.GENOME_LENGTH);
        this.owner = owner;
    }

    public Genome(String code) {
        this.code = code;
    }

    public void setOwner(Entity e) {
        this.owner = e;
    }

    char last = 0;

    public synchronized char last() {
        return last;
    }

    public synchronized char read(int loc) {
        if (loc < 0) {
            loc = Settings.GENOME_LENGTH + (int) read(Settings.GENOME_LENGTH + Gene.DECISION);
        }
        if (loc < code.length()) {
            last = code.charAt(loc);
            return last;
        } else if (loc >= code.length()) {
            try {
                int more = code.length() - loc;
                return read(more);
            } catch (Exception ex) {
            }
        }
        char c = code.charAt(Settings.GENOME_LENGTH + (int) read(Settings.GENOME_LENGTH + Gene.DECISION));
        last = c;
        return c;
    }

    public synchronized char read() {
        int loc = index;
        if (loc < 0) {
            loc = Settings.GENOME_LENGTH + (int) read(Settings.GENOME_LENGTH + Gene.DECISION);
        }
        if (loc < code.length()) {
            last = code.charAt(loc);
            return last;
        } else if (loc >= code.length()) {
            try {
                int more = code.length() - loc;
                return read(more);
            } catch (Exception ex) {
            }
        }
        char c = code.charAt(Settings.GENOME_LENGTH + (int) read(Settings.GENOME_LENGTH + Gene.DECISION));
        last = c;
        advance();
        return c;
    }


    public void recode(int loc, char c) {

        if( c == '-'){
            return;
        }
        if (loc + Settings.GENOME_LENGTH > code.length()) {
            return;
        }
        char[] g = code.toCharArray();

        g[Settings.GENOME_LENGTH + loc] = c; //garl.Utility.flatten(c, 26);
        code = String.valueOf(g);
        numRecodes++;
    }

    public void jump(int loc) {
        if (index + loc < code.length()) {
            index += loc;
        }
    }

    int index = 0;

    public void advance() {
        index++;
        if (index >= code.length()) {
            index = Settings.GENOME_LENGTH + 1;
        }
        if (index < Settings.GENOME_LENGTH) {
            index = Settings.GENOME_LENGTH + 1;
        }
    }

    public void reverse() {
        index--;
        if (index <= Settings.GENOME_LENGTH) {
            index = Settings.GENOME_LENGTH + 1;
        }
    }


    public int index() {
        advance();
        return index;
    }

    public synchronized void mutate() {
        Random rand = new Random();
        char[] c = code.toCharArray();
        int index = c.length - 1;
        int mutations = (int) Math.min((c[Gene.GENE_MUTATION_PROBABILITY] * 0.005 * c[Gene.GENE_MUTATION_MULTIPLIER]), Settings.GENOME_LENGTH / 2);
        for (int j = 0; j <= mutations; j++) {
            index = (int) (Math.random()) * index;
            if (index < 0) {
                index = 0;
            } else if (index >= c.length) {
                index = c.length - 1;
            }
            try {
                char t = c[index];
                c[index] = c[index - 1];
                c[index - 1] = t;
            } catch (Exception ex) {
            }
        }
        String time = "" + Long.toHexString(System.currentTimeMillis());
        time = reverse(time);

        c[Gene.KIN] = KinFactory.create(time.charAt(0));
        code = String.valueOf(c);
    }

    public static String reverse(String in) {
        char[] c = in.toCharArray();
        String o = "";
        for (int i = in.length() - 1; i > 0; i--) {
            o += c[i];
        }
        return o;
    }
}
