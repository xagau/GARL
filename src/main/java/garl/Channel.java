package garl;

import java.util.ArrayList;
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
