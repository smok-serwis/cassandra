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
import java.util.Set;
import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class SetSer extends AxisSerializer
{
   static final String TYPE = "Set";

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      Set set = (Set)value;
      context.startElement(name, attributes);
      for (Iterator i = set.iterator(); i.hasNext();)
      {
         Object item = i.next();
         context.serialize(Constants.QNAME_LITERAL_ITEM, null, item);
      }
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      types.writeSchemaElement(Constants.SOAP_VECTOR, complexType);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);
      Element element = types.createElement(SCHEMA_ELEMENT);
      element.setAttribute("name", Constants.QNAME_LITERAL_ITEM.getLocalPart());
      element.setAttribute("minOccurs", "0");
      element.setAttribute("maxOccurs", "unbounded");
      element.setAttribute("type", Constants.XSD_ANYTYPE.getLocalPart());
      allElement.appendChild(element);
      return complexType;
   }
}
