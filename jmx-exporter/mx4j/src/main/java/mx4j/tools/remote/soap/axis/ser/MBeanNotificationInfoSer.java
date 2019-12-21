/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.MBeanNotificationInfo;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class MBeanNotificationInfoSer extends AxisSerializer
{
   static final String TYPE = "MBeanNotificationInfo";
   static final String NAME = "name";
   static final String DESCRIPTION = "description";
   static final String NOTIFICATION_TYPES = "notificationTypes";
   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName DESCRIPTION_QNAME = new QName("", DESCRIPTION);
   private static final QName NOTIFICATION_TYPES_QNAME = new QName("", NOTIFICATION_TYPES);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      MBeanNotificationInfo info = (MBeanNotificationInfo)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, info.getName());
      context.serialize(DESCRIPTION_QNAME, null, info.getDescription());
      context.serialize(NOTIFICATION_TYPES_QNAME, null, info.getNotifTypes());
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element nameElement = types.createElement(SCHEMA_ELEMENT);
      nameElement.setAttribute("name", NAME);
      nameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(nameElement);

      Element descrElement = types.createElement(SCHEMA_ELEMENT);
      descrElement.setAttribute("name", DESCRIPTION);
      descrElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(descrElement);

      Element typesElement = types.createElement(SCHEMA_ELEMENT);
      typesElement.setAttribute("name", NOTIFICATION_TYPES);
      typesElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(typesElement);

      return complexType;
   }
}
