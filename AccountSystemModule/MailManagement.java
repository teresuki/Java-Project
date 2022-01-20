/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chautruongcoop.testsendmail;

/**
 *
 * @author chautruong
 */
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class MailManagement {
    private static Properties properties;
    private static String username;
    private static String pwd;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();;

    //private static String host;
    MailManagement(String username, String pwd) throws NoSuchProviderException, MessagingException{
        this.username = username;
        this.pwd=pwd;
        //this.host=host;
        this.properties = new Properties();
        //pop3 for checking mail
        properties.put("mail.pop3.host", "pop.gmail.com");
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        
        //smtp for sending mail
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host","smtp.gmail.com");

    }
    private static Session getSession() throws NoSuchProviderException, MessagingException{
        Session emailSession = Session.getInstance(properties,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(
                  username, pwd);
            }
         });
        return emailSession;
    }
    
   public static void checkMail() 
   {
      try {
        Session emailSession = getSession();
        System.out.println("You start checkMail method!");
        //create the POP3 store object and connect with the pop server
        Store store = emailSession.getStore("pop3s");

        store.connect("pop.gmail.com", username, pwd);

        //create the folder object and open it
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        // retrieve the messages from the folder in an array and print it
        Message[] messages = emailFolder.getMessages();
        System.out.println("messages.length---" + messages.length);

        for (int i = 0, n = messages.length; i < n; i++) {
            Message message = messages[i];
            System.out.println("---------------------------------");
            System.out.println("Email Number " + (i + 1));
            System.out.println("Subject: " + message.getSubject());
            System.out.println("From: " + message.getFrom()[0]);
            System.out.println("Text: " + message.getContent().toString());

        }

        //close the store and folder objects
        emailFolder.close(false);
        store.close();

      } catch (NoSuchProviderException e) {
            e.printStackTrace();
      } catch (MessagingException e) {
            e.printStackTrace();
      } catch (Exception e) {
            e.printStackTrace();
      }
   }
   
   public static void sendMail(String recipientAddress, String mailSubject, String mailContent){
        try {
            Session emailSession = getSession();
            // Create a default MimeMessage object
            Message message = new MimeMessage(emailSession);
            
            //Set From: header field of the header
            message.setFrom();
            
            //Set To: header field of the header
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(recipientAddress));
            
            //Set subject: header field
            message.setSubject(mailSubject);
	    // Now set the actual message
	    message.setText(mailContent);

	    // Send message
	    Transport.send(message);

	    System.out.println("Sent message successfully....");
            
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
       
   }
   public static void sendScheduledMail(int time, String recipientAddress, String mailSubject, String content){     
        final Runnable beeper = new Runnable() {
            public void run() { 
                sendMail(recipientAddress, mailSubject, content); }
        }; // Creating a new runnable task which will be passed as an argument to scheduler 
        scheduler.schedule(beeper, time, SECONDS); // Creates and executes a one-shot action that becomes enabled after the given delay.
        scheduler.shutdown();
        System.out.println("Send scheduled message after "+time+ " seconds successfully");
}

   public static void main(String[] args) throws MessagingException {

        //String host = "pop.gmail.com";// change accordingly
        //String mailStoreType = "pop3";
        String username = "chautruongvinhhoang04@gmail.com";// change accordingly
        String password = "Uyen_3012";// change accordingly

        MailManagement user1 = new MailManagement(username, password);
        user1.sendMail("chautruongvinhhoang04@gmail.com","This field is for mail subject","This is a content");
        user1.sendScheduledMail(10,"chautruongvinhoang04@gmail.com","Send mail after 10s","Hello");
   }

}
