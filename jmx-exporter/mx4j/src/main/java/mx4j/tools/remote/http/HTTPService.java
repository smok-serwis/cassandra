/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.tools.remote.Connection;
import mx4j.tools.remote.ConnectionManager;

/**
 * @version $Revision: 1.4 $
 */
public abstract class HTTPService implements HTTPConnection
{
   private final Map connections = new HashMap();

   protected Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public String connect(Object credentials) throws IOException, SecurityException
   {
      JMXServiceURL address = findJMXServiceURL();

      // Lookup the ConnectionManager
      ConnectionManager connectionManager = HTTPConnectorServer.find(address);
      if (connectionManager == null) throw new IOException("Could not find ConnectionManager. Make sure a HTTPConnectorServer is in classloader scope and bound at this address " + address);

      Connection connection = connectionManager.connect(credentials);
      addConnection(connection);
      return connection.getConnectionId();
   }

   protected JMXServiceURL findJMXServiceURL() throws MalformedURLException
   {
      String url = findRequestURL();
      JMXServiceURL temp = new JMXServiceURL("service:jmx:" + url);
      int port = temp.getPort();
      if ("http".equals(temp.getProtocol()) && port == 0)
      {
         // Default HTTP port, set it to 80
         port = 80;
      }
      else if ("https".equals(temp.getProtocol()) && port == 0)
      {
         // Default HTTPS port, set it to 443
         port = 443;
      }
      return new JMXServiceURL(getProtocol(), temp.getHost(), port, temp.getURLPath());
   }

   protected abstract String findRequestURL();

   protected abstract String getProtocol();

   protected void addConnection(Connection connection) throws IOException
   {
      String connectionId = connection.getConnectionId();
      synchronized (this)
      {
         if (connections.containsKey(connectionId)) throw new IOException("Connection '" + connection + "' already connected");
         connections.put(connectionId, connection);

         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Added connection '" + connectionId + "', known connections are " + connections.keySet());
      }
   }

   protected void removeConnection(Connection connection) throws IOException
   {
      String connectionId = connection.getConnectionId();
      synchronized (this)
      {
         if (!connections.containsKey(connectionId)) throw new IOException("Connection '" + connection + "' unknown");
         connections.remove(connectionId);

         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Removed connection '" + connectionId + "', known connections are " + connections.keySet());
      }
   }

   protected Connection findConnection() throws IOException
   {
      String connectionId = findConnectionId();
      synchronized (this)
      {
         Connection connection = (Connection)connections.get(connectionId);
         if (connection != null) return connection;

         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Unknown connection '" + connectionId + "', known connections are " + connections.keySet());
         throw new IOException("Connection ID '" + connectionId + "' unknown");
      }
   }

   protected abstract String findConnectionId();

   public void close() throws IOException
   {
      Connection connection = findConnection();
      removeConnection(connection);
      connection.close();
   }

   public String getConnectionId() throws IOException
   {
      Connection connection = findConnection();
      return connection.getConnectionId();
   }

   public ObjectInstance createMBean(String className, ObjectName name, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.createMBean(className, name, params, signature, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.createMBean(className, name, loaderName, params, signature, delegate);
   }

   public void unregisterMBean(ObjectName name, Subject delegate) throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      connection.unregisterMBean(name, delegate);
   }

   public ObjectInstance getObjectInstance(ObjectName name, Subject delegate) throws InstanceNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getObjectInstance(name, delegate);
   }

   public Set queryMBeans(ObjectName name, Object query, Subject delegate) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.queryMBeans(name, query, delegate);
   }

   public Set queryNames(ObjectName name, Object query, Subject delegate) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.queryNames(name, query, delegate);
   }

   public boolean isRegistered(ObjectName name, Subject delegate) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.isRegistered(name, delegate);
   }

   public Integer getMBeanCount(Subject delegate) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getMBeanCount(delegate);
   }

   public Object getAttribute(ObjectName name, String attribute, Subject delegate) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getAttribute(name, attribute, delegate);
   }

   public AttributeList getAttributes(ObjectName name, String[] attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getAttributes(name, attributes, delegate);
   }

   public void setAttribute(ObjectName name, Object attribute, Subject delegate) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      connection.setAttribute(name, attribute, delegate);
   }

   public AttributeList setAttributes(ObjectName name, Object attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.setAttributes(name, attributes, delegate);
   }

   public Object invoke(ObjectName name, String operationName, Object params, String[] signature, Subject delegate) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.invoke(name, operationName, params, signature, delegate);
   }

   public String getDefaultDomain(Subject delegate) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getDefaultDomain(delegate);
   }

   public String[] getDomains(Subject delegate) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getDomains(delegate);
   }

   public MBeanInfo getMBeanInfo(ObjectName name, Subject delegate) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.getMBeanInfo(name, delegate);
   }

   public boolean isInstanceOf(ObjectName name, String className, Subject delegate) throws InstanceNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.isInstanceOf(name, className, delegate);
   }

   public void addNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      connection.addNotificationListener(name, listener, filter, handback, delegate);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      connection.removeNotificationListener(name, listener, delegate);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      connection.removeNotificationListener(name, listener, filter, handback, delegate);
   }

   public Integer addNotificationListener(ObjectName name, Object filter, Subject delegate) throws InstanceNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.addNotificationListener(name, filter, delegate);
   }

   public void removeNotificationListeners(ObjectName name, Integer[] listenerIDs, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      connection.removeNotificationListeners(name, listenerIDs, delegate);
   }

   public NotificationResult fetchNotifications(long clientSequenceNumber, int maxNotifications, long timeout) throws IOException
   {
      HTTPConnection connection = (HTTPConnection)findConnection();
      return connection.fetchNotifications(clientSequenceNumber, maxNotifications, timeout);
   }
}
