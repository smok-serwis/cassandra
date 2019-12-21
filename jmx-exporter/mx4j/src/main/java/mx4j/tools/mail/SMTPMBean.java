/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.mail;

import javax.management.ObjectName;

/**
 * Management Interface of a SMTP MBean.
 * <p/>
 * This MBean is meant to send a mail given certain situation. It may be used to listen to a monitor
 * or timer and send a mail.
 * <p/>
 * To use it you need to add to your classpath the mail.jar from the JavaMail API and the activation.jar
 * from the Java Activation Framework.
 * <p/>
 * Besides you need to configure all the required fields, at least the serverHost and To fields and if your server
 * requires login also the serverUsername and serverPassword fields
 * <p/>
 * The subject and content fields are subject to keyword expansions, i.e. some keyworks put between $ signs will
 * be exapnded this can be used to give a more informative message. The current available expansions are
 * <p/>
 * $date$ -> Current date formatted with locale format
 * $time$ -> Current tim formatted with locale format
 * $datetime$ -> Current date and time formatted with locale format
 * $notification$ -> Notification type
 * $observed$ -> ObjectName of the observed object
 * $objectname$ -> This MBean's objectname
 *
 * @version $Revision: 1.4 $
 */
public interface SMTPMBean
{
   /**
    * Gets the MBean's objectname which is being listened
    */
   public ObjectName getObservedObject();

   /**
    * Sets the observed object. It is expected that the observed MBean is a NotificationBroadcster
    * On the contrary the MBean will not be listening to events
    */
   public void setObservedObject(ObjectName targetMBeanName);

   /**
    * Returns the notification which will trigger the mail sending
    */
   public String getNotificationName();

   /**
    * Sets the notification name which will trigger the mail sending. If it is null any notification
    * will trigger a mail
    */
   public void setNotificationName(String notificationName);

   /**
    * Gets the server's host as name or IP
    */
   public String getServerHost();

   /**
    * Sets the server's host, it can be set as name or IP
    */
   public void setServerHost(String host);

   /**
    * Sets the server's port.
    */
   public void setServerPort(int port);

   /**
    * Gets the server's port, by default is 25
    */
   public int getServerPort();

   /**
    * Sets server's username, use with setLoginToServer(true)
    */
   public void setServerUsername(String username);

   /**
    * Gets the username to log to the server
    */
   public String getServerUsername();

   /**
    * Sets server's passowrd, use with setLoginToServer(true) and setServerUsername
    */
   public void setServerPassword(String password);

   /**
    * Sets whether to login to the SMTP server
    */
   public void setLoginToServer(boolean login);

   /**
    * Indicates whether login to the SMTP server will be attpemted
    */
   public boolean isLoginToServer();

   /**
    * Sets the send timeout, by default it is 10 secs
    */
   public void setTimeout(int timeout);

   /**
    * Returns the timeout used when sending mails
    */
   public int getTimeout();

   /**
    * Gets the from address attached to mails
    */
   public String getFromAddress();

   /**
    * Sets the form address set to mail
    */
   public void setFromAddress(String fromAddress);

   /**
    * Gets the from name presented on the mail
    */
   public String getFromName();

   /**
    * Sets the from name presented on the mail
    */
   public void setFromName(String fromName);

   /**
    * Gets the MIME type set to the mail
    */
   public String getMimeType();

   /**
    * Sets the MIME type, by default it is text/plain
    */
   public void setMimeType(String mimeType);

   /**
    * Gets a comma separated list of addresses set in the TO field
    */
   public String getTo();

   /**
    * Sets a comma separated list of address which will go in the TO mail field
    */
   public void setTo(String toAddresses);

   /**
    * Gets a comma separated list of addresses set in the BCC field
    */
   public String getBCC();

   /**
    * Sets a comma separated list of address which will go in the BCC mail field
    */
   public void setBCC(String bccAddresses);

   /**
    * Gets a comma separated list of addresses set in the CC field
    */
   public String getCC();

   /**
    * Sets a comma separated list of address which will go in the CC mail field
    */
   public void setCC(String ccAddresses);

   /**
    * Gets the mail subject
    */
   public String getSubject();

   /**
    * Sets the mail's subject, by default is Empty subject. The subject field can contain keyword expansion
    */
   public void setSubject(String subject);

   /**
    * Returns the content of the mail
    */
   public String getContent();

   /**
    * Sets the content of the mail
    */
   public void setContent(String content);

   /**
    * This will directly execute the send mail. It can be used to manually testing the MBean or direct execution
    */
   public void sendMail();

}
