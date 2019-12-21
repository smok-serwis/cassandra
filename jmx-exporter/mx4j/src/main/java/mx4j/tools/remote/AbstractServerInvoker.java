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
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.security.auth.Subject;

/**
 * This class implements partially the JMXConnection interface to forward the calls
 * to an MBeanServerConnection object (hence the name 'invoker').
 * It does not handle nor unmarshalling of arguments (and all related classloading
 * problems), nor remote notification mechanisms of any sort, which are left
 * to subclasses, which will implement them in a protocol specific way.
 * This class is the server-side counterpart of {@link JMXConnectionMBeanServerConnection}
 *
 * @version $Revision: 1.3 $
 */
public abstract class AbstractServerInvoker implements JMXConnection
{
   private final MBeanServerConnection server;

   protected AbstractServerInvoker(MBeanServerConnection server)
   {
      this.server = server;
   }

   public MBeanServerConnection getServer()
   {
      return server;
   }

   public ObjectInstance createMBean(String className, ObjectName name, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return getServer().createMBean(className, name, (Object[])params, signature);
   }

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object params, String[] signature, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return getServer().createMBean(className, name, loaderName, (Object[])params, signature);
   }

   public void unregisterMBean(ObjectName name, Subject delegate) throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      getServer().unregisterMBean(name);
   }

   public ObjectInstance getObjectInstance(ObjectName name, Subject delegate) throws InstanceNotFoundException, IOException
   {
      return getServer().getObjectInstance(name);
   }

   public Set queryMBeans(ObjectName name, Object query, Subject delegate) throws IOException
   {
      return getServer().queryMBeans(name, (QueryExp)query);
   }

   public Set queryNames(ObjectName name, Object query, Subject delegate) throws IOException
   {
      return getServer().queryNames(name, (QueryExp)query);
   }

   public boolean isRegistered(ObjectName name, Subject delegate) throws IOException
   {
      return getServer().isRegistered(name);
   }

   public Integer getMBeanCount(Subject delegate) throws IOException
   {
      return getServer().getMBeanCount();
   }

   public Object getAttribute(ObjectName name, String attribute, Subject delegate) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      return getServer().getAttribute(name, attribute);
   }

   public AttributeList getAttributes(ObjectName name, String[] attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      return getServer().getAttributes(name, attributes);
   }

   public void setAttribute(ObjectName name, Object attribute, Subject delegate) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      getServer().setAttribute(name, (Attribute)attribute);
   }

   public AttributeList setAttributes(ObjectName name, Object attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      return getServer().setAttributes(name, (AttributeList)attributes);
   }

   public Object invoke(ObjectName name, String operationName, Object params, String[] signature, Subject delegate) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      return getServer().invoke(name, operationName, (Object[])params, signature);
   }

   public String getDefaultDomain(Subject delegate) throws IOException
   {
      return getServer().getDefaultDomain();
   }

   public String[] getDomains(Subject delegate) throws IOException
   {
      return getServer().getDomains();
   }

   public MBeanInfo getMBeanInfo(ObjectName name, Subject delegate) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      return getServer().getMBeanInfo(name);
   }

   public boolean isInstanceOf(ObjectName name, String className, Subject delegate) throws InstanceNotFoundException, IOException
   {
      return getServer().isInstanceOf(name, className);
   }

   public void addNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, IOException
   {
      getServer().addNotificationListener(name, listener, (NotificationFilter)filter, handback);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getServer().removeNotificationListener(name, listener);
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getServer().removeNotificationListener(name, listener, (NotificationFilter)filter, handback);
   }
}
