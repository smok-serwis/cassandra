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
 * @version $Revision: 1.3 $
 */
public interface JMXConnection
{
   public void close()
           throws IOException;

   public ObjectInstance createMBean(String className, ObjectName name, Object params, String[] signature, Subject delegate)
           throws ReflectionException,
                  InstanceAlreadyExistsException,
                  MBeanRegistrationException,
                  MBeanException,
                  NotCompliantMBeanException,
                  IOException;

   public ObjectInstance createMBean(String className, ObjectName name, ObjectName loaderName, Object params, String[] signature, Subject delegate)
           throws ReflectionException,
                  InstanceAlreadyExistsException,
                  MBeanRegistrationException,
                  MBeanException,
                  NotCompliantMBeanException,
                  InstanceNotFoundException,
                  IOException;

   public void unregisterMBean(ObjectName name, Subject delegate)
           throws InstanceNotFoundException,
                  MBeanRegistrationException,
                  IOException;

   public ObjectInstance getObjectInstance(ObjectName name, Subject delegate)
           throws InstanceNotFoundException,
                  IOException;

   public Set queryMBeans(ObjectName name, Object query, Subject delegate)
           throws IOException;

   public Set queryNames(ObjectName name, Object query, Subject delegate)
           throws IOException;

   public boolean isRegistered(ObjectName name, Subject delegate)
           throws IOException;

   public Integer getMBeanCount(Subject delegate)
           throws IOException;

   public Object getAttribute(ObjectName name, String attribute, Subject delegate)
           throws MBeanException,
                  AttributeNotFoundException,
                  InstanceNotFoundException,
                  ReflectionException,
                  IOException;

   public AttributeList getAttributes(ObjectName name, String[] attributes, Subject delegate)
           throws InstanceNotFoundException,
                  ReflectionException,
                  IOException;

   public void setAttribute(ObjectName name, Object attribute, Subject delegate)
           throws InstanceNotFoundException,
                  AttributeNotFoundException,
                  InvalidAttributeValueException,
                  MBeanException,
                  ReflectionException,
                  IOException;

   public AttributeList setAttributes(ObjectName name, Object attributes, Subject delegate)
           throws InstanceNotFoundException,
                  ReflectionException,
                  IOException;

   public Object invoke(ObjectName name, String operationName, Object params, String[] signature, Subject delegate)
           throws InstanceNotFoundException,
                  MBeanException,
                  ReflectionException,
                  IOException;

   public String getDefaultDomain(Subject delegate)
           throws IOException;

   public String[] getDomains(Subject delegate)
           throws IOException;

   public MBeanInfo getMBeanInfo(ObjectName name, Subject delegate)
           throws InstanceNotFoundException,
                  IntrospectionException,
                  ReflectionException,
                  IOException;

   public boolean isInstanceOf(ObjectName name, String className, Subject delegate)
           throws InstanceNotFoundException,
                  IOException;

   public void addNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate)
           throws InstanceNotFoundException,
                  IOException;

   public void removeNotificationListener(ObjectName name, ObjectName listener, Subject delegate)
           throws InstanceNotFoundException,
                  ListenerNotFoundException,
                  IOException;

   public void removeNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate)
           throws InstanceNotFoundException,
                  ListenerNotFoundException,
                  IOException;
}
