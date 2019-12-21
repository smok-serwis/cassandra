/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.remote.TargetedNotification;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.4 $
 */
public class TargetedNotificationSer extends AxisSerializer
{
   static final String NOTIFICATION = "notification";
   static final String LISTENER_ID = "listenerID";
   private static final QName NOTIFICATION_QNAME = new QName("", NOTIFICATION);
   private static final QName LISTENER_ID_QNAME = new QName("", LISTENER_ID);
   static final String TYPE = "TargetedNotification";

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      TargetedNotification targetedNotification = (TargetedNotification)value;
      context.startElement(name, attributes);
      context.serialize(NOTIFICATION_QNAME, null, targetedNotification.getNotification());
      context.serialize(LISTENER_ID_QNAME, null, targetedNotification.getListenerID());
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element typeElement = types.createElement(SCHEMA_ELEMENT);
      typeElement.setAttribute("name", NOTIFICATION);
      typeElement.setAttribute("type", NotificationSer.TYPE);
      allElement.appendChild(typeElement);

      Element sourceElement = types.createElement(SCHEMA_ELEMENT);
      sourceElement.setAttribute("name", LISTENER_ID);
      sourceElement.setAttribute("type", XMLType.XSD_INT.getLocalPart());
      allElement.appendChild(sourceElement);

      return complexType;
   }
}
