package garl;

public class KinFactory {
    public static char create(char c) {
        if (c < 'a') {
            return 'f';
        } else if (c < 'e') {
            return 'c';
        } else if (c < 'm') {
            return 'g';
        } else if (c > 'w') {
            return 't';
        }
        return 'z';
    }
}
