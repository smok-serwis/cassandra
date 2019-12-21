/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.mail;

import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import mx4j.log.Log;
import mx4j.log.Logger;

/**
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
 * @version $Revision: 1.7 $
 */
public class SMTP implements SMTPMBean, NotificationListener, MBeanRegistration
{
   private MBeanServer server = null;
   private ObjectName targetMBeanName, objectName;
   private String notificationName;
   private Properties sessionProperties = new Properties();
   private Session session;
   private String content = "Empty default mail";
   private String mimeType = "text/plain";
   private String subject = "Empty Subject";
   private String fromAddress = "noreply";
   private String fromName = "MX4J default";
   private String toAddresses = null;
   private String ccAddresses = null;
   private String bccAddresses = null;
   private String serverHost;
   private String serverPassword;
   private String serverUserName;
   private int serverPort = 25;
   private int timeout = 10000;
   private boolean doLogin;
   private Object sessionLock = new Object();

   public void handleNotification(Notification notification, Object handback)
   {
      if (notificationName != null && !notification.getType().equals(notificationName)) return;

      Logger log = getLogger();
      log.debug("Notification " + notification + " hit, sending message");
      sendMail();
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public void sendMail()
   {
      // send the message in an independet thread not to stop the MBeanServer execution flow
      new Thread(new Runnable()
      {
         public void run()
         {
            if (validState())
            {
               Logger logger = getLogger();

               synchronized (sessionLock)
               {
                  createSession();
                  try
                  {
                     MimeMessage message = new MimeMessage(session);
                     message.setContent(doKeywordExpansion(content), mimeType);
                     message.setSubject(doKeywordExpansion(subject));

                     Address from = new InternetAddress(fromAddress, fromName);
                     message.setFrom(from);
                     message.setReplyTo(new Address[]{from});

                     if (toAddresses != null)
                     {
                        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddresses));
                     }

                     if (ccAddresses != null)
                     {
                        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddresses));
                     }

                     if (bccAddresses != null)
                     {
                        message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccAddresses));
                     }
                     Transport transport = session.getTransport("smtp");
                     if (doLogin)
                     {
                        transport.connect(serverHost, serverPort, serverUserName, serverPassword);
                     }
                     else
                     {
                        transport.connect();
                     }
                     message.saveChanges();

                     if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Sending message");
                     transport.sendMessage(message, message.getAllRecipients());
                     transport.close();
                     if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Message sent");
                  }
                  catch (Exception e)
                  {
                     logger.error("Exception during message sending ", e);
                  }
               }
            }
         }
      }).start();
   }

   private String doKeywordExpansion(String source)
   {
      StringBuffer sourceCopy = new StringBuffer();
      int index = -1;
      int lastIndex = 0;
      int length = source.length();
      while ((index = source.indexOf("$", lastIndex)) > 0)
      {
         sourceCopy.append(source.substring(lastIndex, index));
         if (index >= (length - 1))
         {
            break;
         }
         lastIndex = ++index;
         if (source.charAt(index) == '$')
         {
            sourceCopy.append('$');
            lastIndex++;
         }
         if (source.startsWith("date$", index))
         {
            sourceCopy.append(DateFormat.getDateInstance().format(new Date()));
            lastIndex += 5;
         }
         if (source.startsWith("time$", index))
         {
            sourceCopy.append(DateFormat.getTimeInstance().format(new Date()));
            lastIndex += 5;
         }
         if (source.startsWith("datetime$", index))
         {
            sourceCopy.append(DateFormat.getDateTimeInstance().format(new Date()));
            lastIndex += 9;
         }
         if (source.startsWith("observed$", index))
         {
            if (targetMBeanName != null)
            {
               sourceCopy.append(targetMBeanName);
               lastIndex += 9;
            }
         }
         if (source.startsWith("objectname$", index))
         {
            if (objectName != null)
            {
               sourceCopy.append(objectName);
               lastIndex += 11;
            }
         }
         if (source.startsWith("notification$", index))
         {
            if (notificationName != null)
            {
               sourceCopy.append(notificationName);
               lastIndex += 13;
            }
         }
      }
      sourceCopy.append(source.substring(lastIndex, length));
      return sourceCopy.toString();
   }

   /**
    * Sanity check
    */
   private boolean validState()
   {
      return serverHost != null && toAddresses != null && (!doLogin || (serverUserName != null || serverPassword != null));
   }

   private void createSession()
   {
      synchronized (sessionLock)
      {
         if (session == null)
         {
            sessionProperties.setProperty("mail.smtp.host", serverHost);
            sessionProperties.setProperty("mail.smtp.port", Integer.toString(serverPort));
            sessionProperties.setProperty("mail.smtp.timeout", Integer.toString(timeout));
            sessionProperties.setProperty("mail.smtp.connectiontimeout", Integer.toString(timeout));
            sessionProperties.setProperty("mail.smtp.sendpartial", "true");
            session = Session.getInstance(sessionProperties, null);
         }
      }
   }

   public String getBCC()
   {
      return bccAddresses;
   }

   public void setBCC(String bccAddresses)
   {
      this.bccAddresses = bccAddresses;
   }

   public void setCC(String ccAddresses)
   {
      this.ccAddresses = ccAddresses;
   }

   public String getCC()
   {
      return ccAddresses;
   }

   public String getFromAddress()
   {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress)
   {
      this.fromAddress = fromAddress;
   }

   public void setServerHost(String host)
   {
      synchronized (sessionLock)
      {
         this.serverHost = host;
         session = null;
      }
   }

   public String getServerHost()
   {
      return this.serverHost;
   }

   public void setServerPort(int port)
   {
      synchronized (sessionLock)
      {
         this.serverPort = port;
         session = null;
      }
   }

   public int getServerPort()
   {
      return this.serverPort;
   }

   public void setServerUsername(String username)
   {
      this.serverUserName = username;
   }

   public String getServerUsername()
   {
      return this.serverUserName;
   }

   public void setServerPassword(String password)
   {
      this.serverPassword = password;
   }

   public void setLoginToServer(boolean login)
   {
      this.doLogin = login;
   }

   public boolean isLoginToServer()
   {
      return this.doLogin;
   }

   public String getFromName()
   {
      return fromName;
   }

   public void setFromName(String fromName)
   {
      this.fromName = fromName;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }

   public String getNotificationName()
   {
      return notificationName;
   }

   public void setNotificationName(String notificationName)
   {
      this.notificationName = notificationName;
   }

   public String getSubject()
   {
      return subject;
   }

   public void setSubject(String subject)
   {
      this.subject = subject;
   }

   public String getContent()
   {
      return content;
   }

   public void setContent(String content)
   {
      this.content = content;
   }

   public void setTimeout(int timeout)
   {
      synchronized (sessionLock)
      {
         this.timeout = timeout;
         session = null;
      }
   }

   public int getTimeout()
   {
      return timeout;
   }

   public void setObservedObject(ObjectName targetMBeanName)
   {
      this.targetMBeanName = targetMBeanName;
      registerListener();
   }


   public ObjectName getObservedObject()
   {
      return targetMBeanName;
   }

   public String getTo()
   {
      return toAddresses;
   }

   public void setTo(String toAddresses)
   {
      this.toAddresses = toAddresses;
   }

   /**
    * Gathers some basic data
    */
   public ObjectName preRegister(MBeanServer server, ObjectName name)
           throws java.lang.Exception
   {
      this.server = server;
      this.objectName = name;
      return name;
   }


   public void postRegister(Boolean registrationDone)
   {
   }


   public void preDeregister() throws java.lang.Exception
   {
      unregisterListener();
   }


   public void postDeregister()
   {
   }

   protected void registerListener()
   {
      try
      {
         if (targetMBeanName != null && server.isInstanceOf(targetMBeanName, "javax.management.NotificationBroadcaster"))
         {
            server.addNotificationListener(targetMBeanName, this, new MessageFilter(), null);
         }
      }
      catch (InstanceNotFoundException e)
      {
         Logger log = getLogger();
         log.error("Exception during notification registration", e);
      }
   }

   protected void unregisterListener()
   {
      try
      {
         if (targetMBeanName != null && server.isInstanceOf(targetMBeanName, "javax.management.NotificationBroadcaster"))
         {
            server.removeNotificationListener(targetMBeanName, this);
         }
      }
      catch (InstanceNotFoundException e)
      {
         Logger log = getLogger();
         log.error("Exception during notification unregistration", e);
      }
      catch (ListenerNotFoundException e)
      {
      }
   }

   private class MessageFilter implements NotificationFilter
   {
      public boolean isNotificationEnabled(Notification notification)
      {
         return notificationName == null || (notification.getType() != null && notification.getType().equals(notificationName));
      }
   }
}
