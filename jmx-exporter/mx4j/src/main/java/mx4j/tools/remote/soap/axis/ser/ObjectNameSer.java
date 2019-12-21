/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.ObjectName;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class ObjectNameSer extends AxisSerializer
{
   static final String TYPE = "ObjectName";
   static final String NAME = "canonicalName";
   private static final QName NAME_QNAME = new QName("", NAME);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      ObjectName objectName = (ObjectName)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, objectName.getCanonicalName());
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);
      Element element = types.createElement(SCHEMA_ELEMENT);
      element.setAttribute("name", NAME);
      element.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(element);
      return complexType;
   }
}
