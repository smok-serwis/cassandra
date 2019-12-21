/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

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
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;

/**
 * @version $Revision: 1.3 $
 */
public abstract class HTTPClientInvoker implements HTTPConnection
{
   private String connectionId;

   protected abstract HTTPConnection getService();

   public String connect(Object credentials) throws IOException, SecurityException
   {
      connectionId = getService().connect(credentials);
      return connectionId;
   }

   public void close() throws IOException
   {
      getService().close();
   }

   public String getConnectionId() throws IOException
   {
      return connectionId;
   }

   public ObjectInstance createMBean(String className, ObjectName name, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return getService().createMBean(className, name, params, signature, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return getService().createMBean(className, name, loaderName, params, signature, delegate);
   }

   public void unregisterMBean(ObjectName name, Subject delegate) throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      getService().unregisterMBean(name, delegate);
   }

   public ObjectInstance getObjectInstance(ObjectName name, Subject delegate) throws InstanceNotFoundException, IOException
   {
      return getService().getObjectInstance(name, delegate);
   }

   public Set queryMBeans(ObjectName name, Object query, Subject delegate) throws IOException
   {
      return getService().queryMBeans(name, query, delegate);
   }

   public Set queryNames(ObjectName name, Object query, Subject delegate) throws IOException
   {
      return getService().queryNames(name, query, delegate);
   }

   public boolean isRegistered(ObjectName name, Subject delegate) throws IOException
   {
      return getService().isRegistered(name, delegate);
   }

   public Integer getMBeanCount(Subject delegate) throws IOException
   {
      return getService().getMBeanCount(delegate);
   }

   public Object getAttribute(ObjectName name, String attribute, Subject delegate) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      return getService().getAttribute(name, attribute, delegate);
   }

   public AttributeList getAttributes(ObjectName name, String[] attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      return getService().getAttributes(name, attributes, delegate);
   }

   public void setAttribute(ObjectName name, Object attribute, Subject delegate) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      getService().setAttribute(name, attribute, delegate);
   }

   public AttributeList setAttributes(ObjectName name, Object attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      return getService().setAttributes(name, attributes, delegate);
   }

   public Object invoke(ObjectName name, String operationName, Object params, String[] signature, Subject delegate) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      return getService().invoke(name, operationName, params, signature, delegate);
   }

   public String getDefaultDomain(Subject delegate) throws IOException
   {
      return getService().getDefaultDomain(delegate);
   }

   public String[] getDomains(Subject delegate) throws IOException
   {
      return getService().getDomains(delegate);
   }

   public MBeanInfo getMBeanInfo(ObjectName name, Subject delegate) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      return getService().getMBeanInfo(name, delegate);
   }

   public boolean isInstanceOf(ObjectName name, String className, Subject delegate) throws InstanceNotFoundException, IOException
   {
      return getService().isInstanceOf(name, className, delegate);
   }

   public void addNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, IOException
   {
      getService().addNotificationListener(name, listener, filter, handback, delegate);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getService().removeNotificationListener(name, listener, delegate);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getService().removeNotificationListener(name, listener, filter, handback, delegate);
   }

   public Integer addNotificationListener(ObjectName name, Object filter, Subject delegate) throws InstanceNotFoundException, IOException
   {
      return getService().addNotificationListener(name, filter, delegate);
   }

   public void removeNotificationListeners(ObjectName name, Integer[] listenerIDs, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getService().removeNotificationListeners(name, listenerIDs, delegate);
   }

   public NotificationResult fetchNotifications(long clientSequenceNumber, int maxNotifications, long timeout) throws IOException
   {
      return getService().fetchNotifications(clientSequenceNumber, maxNotifications, timeout);
   }
}
