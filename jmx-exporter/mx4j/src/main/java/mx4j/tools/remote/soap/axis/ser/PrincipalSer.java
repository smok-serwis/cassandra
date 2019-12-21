/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import java.security.Principal;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.4 $
 */
public class PrincipalSer extends AxisSerializer
{
   static final String TYPE = "Principal";
   static final String CLASS_NAME = "className";
   static final String NAME = "name";
   private static final QName CLASS_NAME_QNAME = new QName("", CLASS_NAME);
   private static final QName NAME_QNAME = new QName("", NAME);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      Principal principal = (Principal)value;
      context.startElement(name, attributes);
      context.serialize(CLASS_NAME_QNAME, null, principal.getClass().getName());
      context.serialize(NAME_QNAME, null, principal.getName());
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);
      Element classNameElement = types.createElement(SCHEMA_ELEMENT);
      classNameElement.setAttribute("name", CLASS_NAME);
      classNameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(classNameElement);
      Element nameElement = types.createElement(SCHEMA_ELEMENT);
      nameElement.setAttribute("name", NAME);
      nameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(nameElement);
      return complexType;
   }
}
