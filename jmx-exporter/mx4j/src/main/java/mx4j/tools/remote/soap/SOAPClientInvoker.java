/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap;

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
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import mx4j.tools.remote.http.HTTPConnection;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.SOAPHeaderElement;

/**
 * @version $Revision: 1.3 $
 */
public class SOAPClientInvoker implements HTTPConnection
{
   private static final QName qObjectName = new QName(SOAPConstants.NAMESPACE_URI, "ObjectName");
   private static final QName qObjectInstance = new QName(SOAPConstants.NAMESPACE_URI, "ObjectInstance");
   private static final QName qSubject = new QName(SOAPConstants.NAMESPACE_URI, "Subject");

   private final String endpoint;
   private final Service service;
   private String connectionId;

   public SOAPClientInvoker(String endpoint, Service service)
   {
      this.endpoint = endpoint;
      this.service = service;
   }

   public String connect(Object credentials) throws IOException, SecurityException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "connect"));
      call.addParameter("credentials", XMLType.XSD_ANY, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_STRING);
//      call.addFault(new QName(SOAPConstants.NAMESPACE_URI, "SecurityException"), SecurityException.class, XMLType.XSD_ANY, true);

      connectionId = (String)call.invoke(new Object[]{credentials});
      return connectionId;
   }

   public void close() throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "close"));
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[0]);
   }

   public String getConnectionId() throws IOException
   {
      return connectionId;
   }

   public Integer addNotificationListener(ObjectName name, Object filter, Subject delegate) throws InstanceNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "addNotificationListener"));
      call.addParameter("observed", qObjectName, ParameterMode.IN);
      call.addParameter("filter", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_INT);

      return (Integer)call.invoke(new Object[]{name, filter, delegate});
   }

   public void removeNotificationListeners(ObjectName observed, Integer[] ids, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "removeNotificationListeners"));
      call.addParameter("observed", qObjectName, ParameterMode.IN);
      call.addParameter("ids", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[]{observed, ids, delegate});
   }

   public NotificationResult fetchNotifications(long clientSequenceNumber, int maxNotifications, long timeout) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "fetchNotifications"));
      call.addParameter("sequence", XMLType.XSD_LONG, ParameterMode.IN);
      call.addParameter("maxNumber", XMLType.XSD_INT, ParameterMode.IN);
      call.addParameter("timeout", XMLType.XSD_LONG, ParameterMode.IN);
      call.setReturnType(new QName(SOAPConstants.NAMESPACE_URI, "NotificationResult"));

      NotificationResult result = (NotificationResult)call.invoke(new Object[]{new Long(clientSequenceNumber), new Integer(maxNotifications), new Long(timeout)});
      return result;
   }

   public void addNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate)
           throws InstanceNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "addNotificationListener"));
      call.addParameter("observed", qObjectName, ParameterMode.IN);
      call.addParameter("listener", qObjectName, ParameterMode.IN);
      call.addParameter("filter", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("handback", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[]{name, listener, filter, handback, delegate});
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Subject delegate)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "removeNotificationListener"));
      call.addParameter("observed", qObjectName, ParameterMode.IN);
      call.addParameter("listener", qObjectName, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[]{name, listener, delegate});
   }

   public void removeNotificationListener(ObjectName name, ObjectName listener, Object filter, Object handback, Subject delegate)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "removeNotificationListener"));
      call.addParameter("observed", qObjectName, ParameterMode.IN);
      call.addParameter("listener", qObjectName, ParameterMode.IN);
      call.addParameter("filter", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("handback", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[]{name, listener, filter, handback, delegate});
   }

   public MBeanInfo getMBeanInfo(ObjectName objectName, Subject delegate) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getMBeanInfo"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(new QName(SOAPConstants.NAMESPACE_URI, "MBeanInfo"));

      MBeanInfo info = (MBeanInfo)call.invoke(new Object[]{objectName, delegate});
      return info;
   }

   public boolean isInstanceOf(ObjectName objectName, String className, Subject delegate) throws InstanceNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "isInstanceOf"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("className", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_BOOLEAN);

      Boolean isinstanceof = (Boolean)call.invoke(new Object[]{objectName, className, delegate});
      return isinstanceof.booleanValue();
   }

   public String[] getDomains(Subject delegate) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getDomains"));
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.SOAP_ARRAY);

      String[] domains = (String[])call.invoke(new Object[]{delegate});
      return domains;
   }

   public String getDefaultDomain(Subject delegate) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getDefaultDomain"));
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_STRING);

      String domain = (String)call.invoke(new Object[]{delegate});
      return domain;
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, Object args, String[] parameters, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "createMBean"));

      call.addParameter("className", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("arguments", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("signature", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(qObjectInstance);

      ObjectInstance instance = (ObjectInstance)call.invoke(new Object[]{className, objectName, args, parameters, delegate});
      return instance;
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName, Object args, String[] parameters, Subject delegate) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "createMBean"));

      call.addParameter("className", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("loaderName", qObjectName, ParameterMode.IN);
      call.addParameter("arguments", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("signature", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(qObjectInstance);

      ObjectInstance instance = (ObjectInstance)call.invoke(new Object[]{className, objectName, loaderName, args, parameters, delegate});
      return instance;
   }

   public void unregisterMBean(ObjectName objectName, Subject delegate) throws InstanceNotFoundException, MBeanRegistrationException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "unregisterMBean"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[]{objectName, delegate});
   }

   public Object getAttribute(ObjectName objectName, String attribute, Subject delegate) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getAttribute"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("attributeName", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_ANY);

      Object result = call.invoke(new Object[]{objectName, attribute, delegate});
      return result;
   }

   public void setAttribute(ObjectName objectName, Object attribute, Subject delegate) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "setAttribute"));

      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("attribute", new QName(SOAPConstants.NAMESPACE_URI, "Attribute"), ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.AXIS_VOID);

      call.invoke(new Object[]{objectName, attribute, delegate});
   }

   public AttributeList getAttributes(ObjectName objectName, String[] attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getAttributes"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("attributeNames", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(new QName(SOAPConstants.NAMESPACE_URI, "AttributeList"));

      AttributeList list = (AttributeList)call.invoke(new Object[]{objectName, attributes, delegate});
      return list;
   }

   public AttributeList setAttributes(ObjectName objectName, Object attributes, Subject delegate) throws InstanceNotFoundException, ReflectionException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "setAttributes"));
      QName qAttributeList = new QName(SOAPConstants.NAMESPACE_URI, "AttributeList");
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("attributeList", qAttributeList, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(qAttributeList);

      AttributeList list = (AttributeList)call.invoke(new Object[]{objectName, attributes, delegate});
      return list;
   }

   public Object invoke(ObjectName objectName, String methodName, Object args, String[] parameters, Subject delegate) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "invoke"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("operationName", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("arguments", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("signature", XMLType.SOAP_ARRAY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_ANY);

      Object object = call.invoke(new Object[]{objectName, methodName, args, parameters, delegate});
      return object;
   }

   public Integer getMBeanCount(Subject delegate) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getMBeanCount"));
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_INT);

      Integer count = (Integer)call.invoke(new Object[]{delegate});
      return count;
   }

   public boolean isRegistered(ObjectName objectName, Subject delegate) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "isRegistered"));

      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(XMLType.XSD_BOOLEAN);

      Boolean registered = (Boolean)call.invoke(new Object[]{objectName, delegate});
      return registered.booleanValue();
   }

   public ObjectInstance getObjectInstance(ObjectName objectName, Subject delegate) throws InstanceNotFoundException, IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "getObjectInstance"));
      call.addParameter("objectName", qObjectName, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(qObjectInstance);

      ObjectInstance instance = (ObjectInstance)call.invoke(new Object[]{objectName, delegate});
      return instance;
   }

   public Set queryMBeans(ObjectName patternName, Object filter, Subject delegate) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "queryMBeans"));
      call.addParameter("pattern", qObjectName, ParameterMode.IN);
      call.addParameter("query", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(new QName(SOAPConstants.NAMESPACE_URI, "Set"));

      Set set = (Set)call.invoke(new Object[]{patternName, filter, delegate});
      return set;
   }

   public Set queryNames(ObjectName patternName, Object filter, Subject delegate) throws IOException
   {
      Call call = createCall();

      call.setOperationName(new QName(SOAPConstants.NAMESPACE_URI, "queryNames"));
      call.addParameter("pattern", qObjectName, ParameterMode.IN);
      call.addParameter("query", XMLType.XSD_ANY, ParameterMode.IN);
      call.addParameter("delegate", qSubject, ParameterMode.IN);
      call.setReturnType(new QName(SOAPConstants.NAMESPACE_URI, "Set"));

      Set set = (Set)call.invoke(new Object[]{patternName, filter, delegate});
      return set;
   }

   private Call createCall() throws IOException
   {
      try
      {
         Call call = (Call)service.createCall();

         call.setTargetEndpointAddress(endpoint);

         SOAPHeaderElement connectionIDHeader = new SOAPHeaderElement(SOAPConstants.NAMESPACE_URI, SOAPConstants.CONNECTION_ID_HEADER_NAME, connectionId);
         connectionIDHeader.setMustUnderstand(true);
         call.addHeader(connectionIDHeader);

         return call;
      }
      catch (ServiceException x)
      {
         throw new IOException(x.toString());
      }
   }
}
