package garl;

import java.util.ArrayList;

public class Channel {
    private ArrayList<Message> messages = new ArrayList<>();
    int ctr = 0 ;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void broadcast(Message m){
        if( m != null && !m.getMessage().isEmpty()) {
            m.setNumber(ctr++);
            this.messages.add(m);
        }
        if(Globals.sound) {
            try {
                Thread t = new Thread() {
                    public void run() {
                        Bleep.play(m.getMessage());
                    }
                };
                t.start();
            } catch (Exception ex) {
            }
        }

    }

    public Message listen(){
        if(messages.isEmpty()){
            return null;
        }
        Message m = messages.get(messages.size()-1);
        return m;
    }

    public Message listen(int i){
        if( i < 0 || i >= messages.size()){
            return null;
        }
        return messages.get(i);
    }

}
