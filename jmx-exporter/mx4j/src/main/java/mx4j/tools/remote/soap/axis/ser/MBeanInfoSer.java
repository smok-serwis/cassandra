/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.MBeanInfo;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class MBeanInfoSer extends AxisSerializer
{
   static final String TYPE = "MBeanInfo";
   static final String CLASS_NAME = "className";
   static final String DESCRIPTION = "description";
   static final String ATTRIBUTES = "attributes";
   static final String CONSTRUCTORS = "constructors";
   static final String OPERATIONS = "operations";
   static final String NOTIFICATIONS = "notifications";
   private static final QName CLASS_NAME_QNAME = new QName("", CLASS_NAME);
   private static final QName DESCRIPTION_QNAME = new QName("", DESCRIPTION);
   private static final QName ATTRIBUTES_QNAME = new QName("", ATTRIBUTES);
   private static final QName CONSTRUCTORS_QNAME = new QName("", CONSTRUCTORS);
   private static final QName OPERATIONS_QNAME = new QName("", OPERATIONS);
   private static final QName NOTIFICATIONS_QNAME = new QName("", NOTIFICATIONS);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      MBeanInfo info = (MBeanInfo)value;
      context.startElement(name, attributes);
      context.serialize(CLASS_NAME_QNAME, null, info.getClassName());
      context.serialize(DESCRIPTION_QNAME, null, info.getDescription());
      context.serialize(ATTRIBUTES_QNAME, null, info.getAttributes());
      context.serialize(CONSTRUCTORS_QNAME, null, info.getConstructors());
      context.serialize(OPERATIONS_QNAME, null, info.getOperations());
      context.serialize(NOTIFICATIONS_QNAME, null, info.getNotifications());
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element typeElement = types.createElement(SCHEMA_ELEMENT);
      typeElement.setAttribute("name", CLASS_NAME);
      typeElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(typeElement);

      Element descrElement = types.createElement(SCHEMA_ELEMENT);
      descrElement.setAttribute("name", DESCRIPTION);
      descrElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(descrElement);

      Element attributesElement = types.createElement(SCHEMA_ELEMENT);
      attributesElement.setAttribute("name", ATTRIBUTES);
      attributesElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(attributesElement);

      Element constructorsElement = types.createElement(SCHEMA_ELEMENT);
      constructorsElement.setAttribute("name", CONSTRUCTORS);
      constructorsElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(constructorsElement);

      Element operationsElement = types.createElement(SCHEMA_ELEMENT);
      operationsElement.setAttribute("name", OPERATIONS);
      operationsElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(operationsElement);

      Element notificationsElement = types.createElement(SCHEMA_ELEMENT);
      notificationsElement.setAttribute("name", NOTIFICATIONS);
      notificationsElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(notificationsElement);

      return complexType;
   }
}
