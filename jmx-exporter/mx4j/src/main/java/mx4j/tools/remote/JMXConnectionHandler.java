/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote;

import java.io.IOException;
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
import javax.security.auth.Subject;

/**
 * This handler manages the operations related to the connection itself, such as
 * {@link #close}, {@link #getConnectionId}.
 * <p/>
 * It is important that this object is the outermost wrapper of the Connection objects
 * returned by {@link mx4j.tools.remote.ConnectionManager#connect}
 *
 * @version $Revision: 1.3 $
 */
public abstract class JMXConnectionHandler extends AbstractConnection implements JMXConnection
{
   private final JMXConnection connection;
   private volatile boolean closed;

   public JMXConnectionHandler(JMXConnection connection, ConnectionManager manager, String connectionId)
   {
      super(connectionId, manager);
      this.connection = connection;
   }

   /**
    * Overridden to allow nested connections to close and release their resources and, afterwards,
    * to close this connection with the JSR 160 semantic provided by the superclass.
    */
   public void close() throws IOException
   {
      if (isClosed()) return;
      closed = true;
      getConnection().close();
      super.close();
   }

   protected boolean isClosed()
   {
      return closed;
   }

   protected JMXConnection getConnection()
   {
      return connection;
   }

   public ObjectInstance createMBean(String className, ObjectName name, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().createMBean(className, name, params, signature, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().createMBean(className, name, loaderName, params, signature, delegate);
   }

   public void unregisterMBean(ObjectName name, Subject delegate) throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      getConnection().unregisterMBean(name, delegate);
   }

   public ObjectInstance getObjectInstance(ObjectName name, Subject delegate) throws InstanceNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getObjectInstance(name, delegate);
   }

   public Set queryMBeans(ObjectName name, Object query, Subject delegate) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().queryMBeans(name, query, delegate);
   }

   public Set queryNames(ObjectName name, Object query, Subject delegate) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().queryNames(name, query, delegate);
   }

   public boolean isRegistered(ObjectName name, Subject delegate) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().isRegistered(name, delegate);
   }

   public Integer getMBeanCount(Subject delegate) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getMBeanCount(delegate);
   }

   public Object getAttribute(ObjectName name, String attribute, Subject delegate) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getAttribute(name, attribute, delegate);
   }

   public AttributeList getAttributes(ObjectName name, String[] attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getAttributes(name, attributes, delegate);
   }

   public void setAttribute(ObjectName name, Object attribute, Subject delegate) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      getConnection().setAttribute(name, attribute, delegate);
   }

   public AttributeList setAttributes(ObjectName name, Object attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().setAttributes(name, attributes, delegate);
   }

   public Object invoke(ObjectName name, String operationName, Object params, String[] signature, Subject delegate) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().invoke(name, operationName, params, signature, delegate);
   }

   public String getDefaultDomain(Subject delegate) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getDefaultDomain(delegate);
   }

   public String[] getDomains(Subject delegate) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getDomains(delegate);
   }

   public MBeanInfo getMBeanInfo(ObjectName name, Subject delegate) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().getMBeanInfo(name, delegate);
   }

   public boolean isInstanceOf(ObjectName name, String className, Subject delegate) throws InstanceNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return getConnection().isInstanceOf(name, className, delegate);
   }

   public void addNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      getConnection().addNotificationListener(name, listener, filter, handback, delegate);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      getConnection().removeNotificationListener(name, listener, delegate);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      getConnection().removeNotificationListener(name, listener, filter, handback, delegate);
   }
}
