package garl;

import java.util.ArrayList;

public class Message {
    private ArrayList<Double> message = new ArrayList<Double>();
    private Entity sender = null;
    public long time = 0;
    private int number = -1;


    public ArrayList<Double> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<Double> message) {
        this.message = message;
        this.time = System.nanoTime();
    }

    public void add(double d){
        message.add(d);

    }

    public Entity getSender() {
        return sender;
    }

    public void setSender(Entity sender) {
        this.sender = sender;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
