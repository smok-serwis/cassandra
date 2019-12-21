/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.Notification;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.4 $
 */
public class NotificationSer extends AxisSerializer
{
   static final String TYPE = "Notification";
   static final String CLASS_NAME = "type";
   static final String SOURCE = "source";
   static final String SEQUENCE_NUMBER = "sequenceNumber";
   static final String TIMESTAMP = "timeStamp";
   static final String MESSAGE = "message";
   static final String USER_DATA = "userData";
   private static final QName CLASS_NAME_QNAME = new QName("", CLASS_NAME);
   private static final QName SOURCE_QNAME = new QName("", SOURCE);
   private static final QName SEQUENCE_NUMBER_QNAME = new QName("", SEQUENCE_NUMBER);
   private static final QName TIMESTAMP_QNAME = new QName("", TIMESTAMP);
   private static final QName MESSAGE_QNAME = new QName("", MESSAGE);
   private static final QName USER_DATA_QNAME = new QName("", USER_DATA);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      Notification notification = (Notification)value;
      context.startElement(name, attributes);
      onSerialize(context, notification);
      context.endElement();
   }

   protected void onSerialize(SerializationContext context, Notification notification) throws IOException
   {
      context.serialize(CLASS_NAME_QNAME, null, notification.getType());
      context.serialize(SOURCE_QNAME, null, notification.getSource());
      context.serialize(SEQUENCE_NUMBER_QNAME, null, new Long(notification.getSequenceNumber()));
      context.serialize(TIMESTAMP_QNAME, null, new Long(notification.getTimeStamp()));
      context.serialize(MESSAGE_QNAME, null, notification.getMessage());
      context.serialize(USER_DATA_QNAME, null, notification.getUserData());
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element typeElement = types.createElement(SCHEMA_ELEMENT);
      typeElement.setAttribute("name", CLASS_NAME);
      typeElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(typeElement);

      Element sourceElement = types.createElement(SCHEMA_ELEMENT);
      sourceElement.setAttribute("name", SOURCE);
      sourceElement.setAttribute("type", XMLType.XSD_ANYTYPE.getLocalPart());
      allElement.appendChild(sourceElement);

      Element sequenceNumberElement = types.createElement(SCHEMA_ELEMENT);
      sequenceNumberElement.setAttribute("name", SEQUENCE_NUMBER);
      sequenceNumberElement.setAttribute("type", XMLType.XSD_LONG.getLocalPart());
      allElement.appendChild(sequenceNumberElement);

      Element timeStampElement = types.createElement(SCHEMA_ELEMENT);
      timeStampElement.setAttribute("name", TIMESTAMP);
      timeStampElement.setAttribute("type", XMLType.XSD_LONG.getLocalPart());
      allElement.appendChild(timeStampElement);

      Element messageElement = types.createElement(SCHEMA_ELEMENT);
      messageElement.setAttribute("name", MESSAGE);
      messageElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(messageElement);

      Element userDataElement = types.createElement(SCHEMA_ELEMENT);
      userDataElement.setAttribute("name", USER_DATA);
      userDataElement.setAttribute("type", XMLType.XSD_ANYTYPE.getLocalPart());
      allElement.appendChild(userDataElement);

      return complexType;
   }
}
