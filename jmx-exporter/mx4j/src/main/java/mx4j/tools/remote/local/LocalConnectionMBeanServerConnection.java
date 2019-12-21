/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.local;

import java.io.IOException;
import java.util.Set;
import javax.management.Attribute;
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
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.security.auth.Subject;

/**
 * @version $Revision: 1.3 $
 */
class LocalConnectionMBeanServerConnection implements MBeanServerConnection
{
   private final LocalConnection connection;
   private final Subject delegate;

   LocalConnectionMBeanServerConnection(LocalConnection connection, Subject delegate)
   {
      this.connection = connection;
      this.delegate = delegate;
   }

   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, IOException
   {
      connection.addNotificationListener(observed, listener, filter, handback, delegate);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      connection.removeNotificationListener(observed, listener, delegate);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      connection.removeNotificationListener(observed, listener, filter, handback, delegate);
   }

   public void addNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, IOException
   {
      connection.addNotificationListener(observed, listener, filter, handback, delegate);
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      connection.removeNotificationListener(observed, listener, delegate);
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      connection.removeNotificationListener(observed, listener, filter, handback, delegate);
   }

   public MBeanInfo getMBeanInfo(ObjectName objectName)
           throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      return connection.getMBeanInfo(objectName, delegate);
   }

   public boolean isInstanceOf(ObjectName objectName, String className)
           throws InstanceNotFoundException, IOException
   {
      return connection.isInstanceOf(objectName, className, delegate);
   }

   public String[] getDomains()
           throws IOException
   {
      return connection.getDomains(delegate);
   }

   public String getDefaultDomain()
           throws IOException
   {
      return connection.getDefaultDomain(delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return connection.createMBean(className, objectName, null, null, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, Object[] args, String[] parameters)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return connection.createMBean(className, objectName, args, parameters, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return connection.createMBean(className, objectName, loaderName, null, null, delegate);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName, Object[] args, String[] parameters)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return connection.createMBean(className, objectName, loaderName, args, parameters, delegate);
   }

   public void unregisterMBean(ObjectName objectName)
           throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      connection.unregisterMBean(objectName, delegate);
   }

   public Object getAttribute(ObjectName objectName, String attribute)
           throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      return connection.getAttribute(objectName, attribute, delegate);
   }

   public void setAttribute(ObjectName objectName, Attribute attribute)
           throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      connection.setAttribute(objectName, attribute, delegate);
   }

   public AttributeList getAttributes(ObjectName objectName, String[] attributes)
           throws InstanceNotFoundException, ReflectionException, IOException
   {
      return connection.getAttributes(objectName, attributes, delegate);
   }

   public AttributeList setAttributes(ObjectName objectName, AttributeList attributes)
           throws InstanceNotFoundException, ReflectionException, IOException
   {
      return connection.setAttributes(objectName, attributes, delegate);
   }

   public Object invoke(ObjectName objectName, String methodName, Object[] args, String[] parameters)
           throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      return connection.invoke(objectName, methodName, args, parameters, delegate);
   }

   public Integer getMBeanCount()
           throws IOException
   {
      return connection.getMBeanCount(delegate);
   }

   public boolean isRegistered(ObjectName objectName)
           throws IOException
   {
      return connection.isRegistered(objectName, delegate);
   }

   public ObjectInstance getObjectInstance(ObjectName objectName)
           throws InstanceNotFoundException, IOException
   {
      return connection.getObjectInstance(objectName, delegate);
   }

   public Set queryMBeans(ObjectName patternName, QueryExp filter)
           throws IOException
   {
      return connection.queryMBeans(patternName, filter, delegate);
   }

   public Set queryNames(ObjectName patternName, QueryExp filter)
           throws IOException
   {
      return connection.queryNames(patternName, filter, delegate);
   }
}
