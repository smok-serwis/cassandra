/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import mx4j.remote.ConnectionNotificationEmitter;

/**
 * Abstract implementation of the JMXConnector interface.
 * It gives support for emitting connection notifications and implements JMXConnector methods
 * using the template method pattern.
 *
 * @version $Revision: 1.8 $
 */
public abstract class AbstractJMXConnector implements JMXConnector, Serializable
{
   /**
    * @serial
    */
   private final JMXServiceURL address;
   private transient boolean connected;
   private transient boolean closed;
   private transient ConnectionNotificationEmitter emitter;

   /**
    * Creates a new JMXConnector that will connect to the given JMXServiceURL
    */
   protected AbstractJMXConnector(JMXServiceURL address) throws IOException
   {
      if (address == null) throw new IOException("JMXServiceURL cannot be null");
      this.address = address;
   }

   /**
    * Returns the JMXServiceURL this JMXConnector will connect to.
    */
   protected JMXServiceURL getAddress()
   {
      return address;
   }

   public void connect() throws IOException, SecurityException
   {
      connect(null);
   }

   public void connect(Map environment) throws IOException, SecurityException
   {
      synchronized (this)
      {
         if (isConnected()) return;
         if (isClosed()) throw new IOException("This connector has already been closed");

         doConnect(environment);

         connected = true;
      }

      sendConnectionNotificationOpened();
   }

   protected abstract void doConnect(Map environment) throws IOException, SecurityException;

   public void close() throws IOException
   {
      synchronized (this)
      {
         if (isClosed()) return;
         closed = true;
         connected = false;

         doClose();
      }

      sendConnectionNotificationClosed();
   }

   /**
    * Template method to be implemented by subclasses to close this JMXConnector
    */
   protected abstract void doClose() throws IOException;

   public MBeanServerConnection getMBeanServerConnection() throws IOException
   {
      return getMBeanServerConnection(null);
   }

   public MBeanServerConnection getMBeanServerConnection(Subject delegate) throws IOException
   {
      if (!isConnected()) throw new IOException("Connection has not been established");
      return doGetMBeanServerConnection(delegate);
   }

   /**
    * Template method to be implemented by subclasses to return an MBeanServerConnection
    * for the given delegate subject.
    * This method should return an MBeanServerConnection that delegates method calls to a
    * {@link JMXConnection} (or an equivalent client side connection object).
    * The JMXConnection object to which calls are delegated can in turn be a chain of
    * objects that decorate the call performing some other operation; the final object in
    * the chain is the one that really communicates with the server side, and it is normally
    * called <protocol>ClientInvoker.
    */
   protected abstract MBeanServerConnection doGetMBeanServerConnection(Subject delegate) throws IOException;

   public void addConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
   {
      getConnectionNotificationEmitter().addNotificationListener(listener, filter, handback);
   }

   public void removeConnectionNotificationListener(NotificationListener listener) throws ListenerNotFoundException
   {
      getConnectionNotificationEmitter().removeNotificationListener(listener);
   }

   public void removeConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException
   {
      getConnectionNotificationEmitter().removeNotificationListener(listener, filter, handback);
   }

   private void sendConnectionNotificationOpened()
   {
      getConnectionNotificationEmitter().sendConnectionNotificationOpened();
   }

   protected void sendConnectionNotificationClosed()
   {
      getConnectionNotificationEmitter().sendConnectionNotificationClosed();
   }

   /**
    * Creates a notification emitter used to emit connection notifications.
    * This method is called once per JMXConnector.
    */
   protected ConnectionNotificationEmitter createConnectionNotificationEmitter()
   {
      return new ConnectionNotificationEmitter(this);
   }

   protected ConnectionNotificationEmitter getConnectionNotificationEmitter()
   {
      synchronized (this)
      {
         if (emitter == null) emitter = createConnectionNotificationEmitter();
      }
      return emitter;
   }

   /**
    * Returns whether the {@link #connect} or {@link #connect(Map)} method has been called on this JMXConnector.
    */
   protected synchronized boolean isConnected()
   {
      return connected;
   }

   /**
    * Returns whether the {@link #close} method has been called.
    */
   protected synchronized boolean isClosed()
   {
      return closed;
   }
}
