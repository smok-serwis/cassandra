/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.Attribute;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.8 $
 */
public class AttributeSer extends AxisSerializer
{
   static final String TYPE = "Attribute";
   static final String NAME = "name";
   static final String VALUE = "value";
   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName VALUE_QNAME = new QName("", VALUE);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      Attribute attribute = (Attribute)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, attribute.getName());
      context.serialize(VALUE_QNAME, null, attribute.getValue());
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element nameElement = types.createElement(SCHEMA_ELEMENT);
      nameElement.setAttribute("name", NAME);
      nameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(nameElement);

      Element valueElement = types.createElement(SCHEMA_ELEMENT);
      valueElement.setAttribute("name", VALUE);
      valueElement.setAttribute("type", XMLType.XSD_ANYTYPE.getLocalPart());
      allElement.appendChild(valueElement);

      return complexType;
   }
}
