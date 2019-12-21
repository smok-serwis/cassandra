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
 * Abstract implementation of an adapter that converts MBeanServerConnection calls
 * to JMXConnection calls.
 * It does not handle nor marshalling nor remote notifications, which are left to
 * subclasses.
 * This is the client side counterpart of {@link AbstractServerInvoker}
 *
 * @version $Revision: 1.3 $
 */
public abstract class JMXConnectionMBeanServerConnection implements MBeanServerConnection
{
   private final JMXConnection connection;
   private final Subject delegate;

   protected JMXConnectionMBeanServerConnection(JMXConnection connection, Subject delegate)
   {
      this.connection = connection;
      this.delegate = delegate;
   }

   protected JMXConnection getConnection()
   {
      return connection;
   }

   protected Subject getDelegateSubject()
   {
      return delegate;
   }

   public MBeanInfo getMBeanInfo(ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      return getConnection().getMBeanInfo(objectName, getDelegateSubject());
   }

   public boolean isInstanceOf(ObjectName objectName, String className) throws InstanceNotFoundException, IOException
   {
      return getConnection().isInstanceOf(objectName, className, getDelegateSubject());
   }

   public String[] getDomains() throws IOException
   {
      return getConnection().getDomains(getDelegateSubject());
   }

   public String getDefaultDomain() throws IOException
   {
      return getConnection().getDefaultDomain(getDelegateSubject());
   }

   public ObjectInstance createMBean(String className, ObjectName objectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return createMBean(className, objectName, null, null);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, Object[] args, String[] parameters) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      return getConnection().createMBean(className, objectName, args, parameters, getDelegateSubject());
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return createMBean(className, objectName, loaderName, null, null);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName, Object[] args, String[] parameters) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      return getConnection().createMBean(className, objectName, loaderName, args, parameters, getDelegateSubject());
   }

   public void unregisterMBean(ObjectName objectName) throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      getConnection().unregisterMBean(objectName, getDelegateSubject());
   }

   public Object getAttribute(ObjectName objectName, String attribute) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      return getConnection().getAttribute(objectName, attribute, getDelegateSubject());
   }

   public void setAttribute(ObjectName objectName, Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      getConnection().setAttribute(objectName, attribute, getDelegateSubject());
   }

   public AttributeList getAttributes(ObjectName objectName, String[] attributes) throws InstanceNotFoundException, ReflectionException, IOException
   {
      return getConnection().getAttributes(objectName, attributes, getDelegateSubject());
   }

   public AttributeList setAttributes(ObjectName objectName, AttributeList attributes) throws InstanceNotFoundException, ReflectionException, IOException
   {
      return getConnection().setAttributes(objectName, attributes, getDelegateSubject());
   }

   public Object invoke(ObjectName objectName, String methodName, Object[] args, String[] parameters) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      return getConnection().invoke(objectName, methodName, args, parameters, getDelegateSubject());
   }

   public Integer getMBeanCount() throws IOException
   {
      return getConnection().getMBeanCount(getDelegateSubject());
   }

   public boolean isRegistered(ObjectName objectName) throws IOException
   {
      return getConnection().isRegistered(objectName, getDelegateSubject());
   }

   public ObjectInstance getObjectInstance(ObjectName objectName) throws InstanceNotFoundException, IOException
   {
      return getConnection().getObjectInstance(objectName, getDelegateSubject());
   }

   public Set queryMBeans(ObjectName patternName, QueryExp filter) throws IOException
   {
      return getConnection().queryMBeans(patternName, filter, getDelegateSubject());
   }

   public Set queryNames(ObjectName patternName, QueryExp filter) throws IOException
   {
      return getConnection().queryNames(patternName, filter, getDelegateSubject());
   }

   public void addNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, IOException
   {
      getConnection().addNotificationListener(observed, listener, filter, handback, getDelegateSubject());
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getConnection().removeNotificationListener(observed, listener, getDelegateSubject());
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      getConnection().removeNotificationListener(observed, listener, filter, handback, getDelegateSubject());
   }
}
