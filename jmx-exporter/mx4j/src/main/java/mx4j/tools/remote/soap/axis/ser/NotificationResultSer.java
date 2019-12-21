/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.remote.NotificationResult;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.4 $
 */
public class NotificationResultSer extends AxisSerializer
{
   static final String TYPE = "NotificationResult";
   static final String EARLIEST_NUMBER = "earliestSequenceNumber";
   static final String NEXT_NUMBER = "nextSequenceNumber";
   static final String NOTIFICATIONS = "targetedNotifications";
   private static final QName EARLIEST_NUMBER_QNAME = new QName("", EARLIEST_NUMBER);
   private static final QName NEXT_NUMBER_QNAME = new QName("", NEXT_NUMBER);
   private static final QName NOTIFICATIONS_QNAME = new QName("", NOTIFICATIONS);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      NotificationResult notificationResult = (NotificationResult)value;
      context.startElement(name, attributes);
      context.serialize(EARLIEST_NUMBER_QNAME, null, new Long(notificationResult.getEarliestSequenceNumber()));
      context.serialize(NEXT_NUMBER_QNAME, null, new Long(notificationResult.getNextSequenceNumber()));
      context.serialize(NOTIFICATIONS_QNAME, null, notificationResult.getTargetedNotifications());
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element typeElement = types.createElement(SCHEMA_ELEMENT);
      typeElement.setAttribute("name", EARLIEST_NUMBER);
      typeElement.setAttribute("type", XMLType.XSD_LONG.getLocalPart());
      allElement.appendChild(typeElement);

      Element sourceElement = types.createElement(SCHEMA_ELEMENT);
      sourceElement.setAttribute("name", NEXT_NUMBER);
      sourceElement.setAttribute("type", XMLType.XSD_LONG.getLocalPart());
      allElement.appendChild(sourceElement);

      Element sequenceNumberElement = types.createElement(SCHEMA_ELEMENT);
      sequenceNumberElement.setAttribute("name", NOTIFICATIONS);
      sequenceNumberElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(sequenceNumberElement);

      return complexType;
   }
}
