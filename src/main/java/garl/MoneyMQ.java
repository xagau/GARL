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

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MoneyMQ {
    static Connection connection = null;

    public void establishConnection()
    {
        try {

            String uri = Settings.RABBIT_ADDRESS;
            if (uri == null) {
                Log.payment("Unable to send", Level.ALL);
                return;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);
            factory.setRequestedHeartbeat(30);
            factory.setConnectionTimeout(30000);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(10000);
            factory.setTopologyRecoveryEnabled( true );

            connection = factory.newConnection();
        } catch (URISyntaxException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                Log.payment("URISyntaxException", Level.SEVERE);
                ex.printStackTrace();
            }
        } catch (NoSuchAlgorithmException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                Log.payment("NoSuchAlgorithmException", Level.SEVERE);
                ex.printStackTrace();
            }
        } catch (KeyManagementException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                Log.payment("KeyManagementException", Level.SEVERE);
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                Log.payment("IOException", Level.SEVERE);
                ex.printStackTrace();
            }
        } catch(Exception ex) {
            if( Globals.verbose ) {
                Log.payment("Exception", Level.SEVERE);
                ex.printStackTrace();
            }
            Log.info("Exception:" + ex.getMessage());
        } finally {
            Log.info("establish connection");
        }

    }

    public boolean isConnectionDead()
    {

        if ( connection == null ){
            return true;
        }
        if( !connection.isOpen() ){
            return true;
        }
        return false;
    }

    public void ensureConnection()
    {
        if( isConnectionDead() ){
            establishConnection();
        }
    }

    public static void main(String args[])
    {
        MoneyMQ mq = new MoneyMQ();
        mq.send(Settings.PAYOUT_ADDRESS, "1.00000001");
    }

    public void send(String payoutAddress, String money) {
        Channel channel = null;
        try {

            try {
                DecimalFormat df = new DecimalFormat("0.00000000");
                Double d = Double.parseDouble(money);
                if( d < Globals.minPayout ){
                    Log.info("Cannot payout of " + money + " too small");
                    return;
                }
                if( d > Globals.maxPayout ){
                    money = df.format(Globals.maxPayout);
                }
                if( d == 0 ){
                    Log.info("Cannot payout 0.00000000 to " + payoutAddress);
                    return;
                }
                else {
                    Log.info("Will payout " + money + " to " + payoutAddress);
                }
            } catch(Exception ex) {
                Log.payment(ex.getMessage(), Level.ALL);
                Log.info("NPE:" + ex); return;
            }

            ensureConnection();

            channel = connection.createChannel();
            try {
                payoutAddress = payoutAddress.trim();
            } catch(Exception ex) {
                Log.info("TRIM:" + ex.getMessage());
            }

            try {
                money = money.replaceAll(",", ".");
            } catch(Exception ex) {
                Log.info("Money Parse:" + ex.getMessage());
            }
            Transaction t = new Transaction();
            t.setCurrency("PHL");
            t.setOtp("00" + Property.getProperty("otp"));
            t.setClientId(ComputerIdentifier.generateLicenseKey());
            t.setTerminalId(Property.getProperty("terminalid"));
            t.setAmount(money);
            t.setRecipient(payoutAddress);
            t.setTransactionId("MC" + System.currentTimeMillis());

            String queue = "transactions-mc";     //queue name
            Log.info("Publish to Queue:" + queue);
            boolean durable = true;    //durable - RabbitMQ will never lose the queue if a crash occurs
            boolean exclusive = false;  //exclusive - if queue only will be used by one connection
            boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

            Gson gson = new Gson();

            // 2. Java object to JSON string
            String json = gson.toJson(t);

            try {

                channel.queueDeclare(queue, durable, exclusive, autoDelete, null);
            } catch (Exception ex) {
                Log.payment("NPR:" + ex.toString(), Level.ALL);
                ex.printStackTrace();
            }

            //AesGcmJce agjEncryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
            //byte[] encrypted = agjEncryption.encrypt(json.getBytes("ISO-8859-1"), aad.getBytes("ISO-8859-1"));

            String exchangeName = "";
            String routingKey = "transactions-mc";
            Log.payment("Routing Key:" + routingKey, Level.ALL);
            channel.basicPublish(exchangeName, routingKey, null, json.getBytes());
            Log.payment(" [x] Sent '" + new String(json) + "'", Level.ALL);

            channel.close();
            Log.payment(" [x] Sent Successfully", Level.ALL);
        } catch (Exception ex) {
            Log.info("MoneyMQ:" + ex.getMessage(), Level.ALL);
            Log.payment("ERROR: publish failed:" + ex.getMessage(), Level.ALL);
            ex.printStackTrace();
        } finally {
            try {
                if( channel != null ) {
                    if (channel.isOpen()) {
                        channel.close();
                    }
                }
                Log.info("Finally - channel was closed.");
            } catch (Exception ex) {
                Log.payment("Finally:" + ex.toString(), Level.ALL);
                ex.printStackTrace();
            }
        }
    }
}
