/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kwetter_go;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author RY Jin Simple message provider
 */
public class Main {

    static final Logger logger = Logger.getLogger("SimpleMessageClient");
    private static final String JNDI_CONNECTION_FACTORY = "jms/Queue";
    private static final String JNDI_TOPIC = "jms/myTopic"; // not used... you can use topic for publish-subscribe
    private static final String JNDI_MYQUEUE = "jms/myQueue"; //myQueue | jms/Queue

    private static ConnectionFactory connectionFactory;
    private static Queue queue;
    private static Context jndiContext;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String name = null;
        String content = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("name:");
        try {
            name = br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read your name!");
            System.exit(1);
        }

        System.out.print("content:");
        try {
            content = br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read your name!");
            System.exit(1);
        }
        
        System.out.println("----- INPUT -----");
        System.out.println(name + ": " + content);
        
        try {
            System.out.println("sending message...");
            sendMessage(name, content);
        } catch (NamingException | JMSException ex) {
            ex.printStackTrace();
        }
    }

    // send message method
    // creates context and looks up connectionfactory and queue (that you must make first in the admin-panel of the glassfish-server)
    private static void sendMessage(String username, String contents) throws NamingException, JMSException {
        jndiContext = new InitialContext();
        connectionFactory = (ConnectionFactory) jndiContext.lookup(JNDI_CONNECTION_FACTORY);
        queue = (Queue) jndiContext.lookup(JNDI_MYQUEUE);
        //try with resources
        try (Connection testConnection = connectionFactory.createConnection()) {
            Session session = testConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage();
            message.setStringProperty("username", username);
            message.setStringProperty("content", contents);
            message.setText("new message from KwetterGO!");
            producer.send(message);
            System.out.println("message send.");
        }
    }
}
