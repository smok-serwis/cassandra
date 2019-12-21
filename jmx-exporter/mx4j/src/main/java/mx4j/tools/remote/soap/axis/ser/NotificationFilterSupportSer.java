/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.management.NotificationFilterSupport;
import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.3 $
 */
public class NotificationFilterSupportSer extends AxisSerializer
{
   static final String TYPE = "NotificationFilterSupport";
   static final String NOTIFICATION_TYPE = "notificationType";
   private static final QName NOTIFICATION_TYPE_QNAME = new QName("", NOTIFICATION_TYPE);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      NotificationFilterSupport filter = (NotificationFilterSupport)value;
      context.startElement(name, attributes);
      onSerialize(context, filter);
      context.endElement();
   }

   protected void onSerialize(SerializationContext context, NotificationFilterSupport filter) throws IOException
   {
      Vector types = filter.getEnabledTypes();
      for (Iterator i = types.iterator(); i.hasNext();)
      {
         String type = (String)i.next();
         context.serialize(NOTIFICATION_TYPE_QNAME, null, type);
      }
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      types.writeSchemaElement(Constants.SOAP_VECTOR, complexType);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);
      Element element = types.createElement(SCHEMA_ELEMENT);
      element.setAttribute("name", NOTIFICATION_TYPE);
      element.setAttribute("minOccurs", "0");
      element.setAttribute("maxOccurs", "unbounded");
      element.setAttribute("type", Constants.XSD_STRING.getLocalPart());
      allElement.appendChild(element);
      return complexType;
   }
}
