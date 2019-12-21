/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.MBeanParameterInfo;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class MBeanParameterInfoSer extends AxisSerializer
{
   static final String TYPE = "MBeanParameterInfo";
   static final String NAME = "name";
   static final String CLASS_NAME = "type";
   static final String DESCRIPTION = "description";
   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName CLASS_NAME_QNAME = new QName("", CLASS_NAME);
   private static final QName DESCRIPTION_QNAME = new QName("", DESCRIPTION);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      MBeanParameterInfo info = (MBeanParameterInfo)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, info.getName());
      context.serialize(CLASS_NAME_QNAME, null, info.getType());
      context.serialize(DESCRIPTION_QNAME, null, info.getDescription());
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

      Element typeElement = types.createElement(SCHEMA_ELEMENT);
      typeElement.setAttribute("name", CLASS_NAME);
      typeElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(typeElement);

      Element descrElement = types.createElement(SCHEMA_ELEMENT);
      descrElement.setAttribute("name", DESCRIPTION);
      descrElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(descrElement);

      return complexType;
   }
}
