/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.MBeanAttributeInfo;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class MBeanAttributeInfoSer extends AxisSerializer
{
   static final String TYPE = "MBeanAttributeInfo";
   static final String NAME = "name";
   static final String CLASS_NAME = "type";
   static final String DESCRIPTION = "description";
   static final String IS_READABLE = "isReadable";
   static final String IS_WRITABLE = "isWritable";
   static final String IS_IS = "isIs";
   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName TYPE_QNAME = new QName("", CLASS_NAME);
   private static final QName DESCRIPTION_QNAME = new QName("", DESCRIPTION);
   private static final QName IS_READABLE_QNAME = new QName("", IS_READABLE);
   private static final QName IS_WRITABLE_QNAME = new QName("", IS_WRITABLE);
   private static final QName IS_IS_QNAME = new QName("", IS_IS);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      MBeanAttributeInfo info = (MBeanAttributeInfo)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, info.getName());
      context.serialize(TYPE_QNAME, null, info.getType());
      context.serialize(DESCRIPTION_QNAME, null, info.getDescription());
      context.serialize(IS_READABLE_QNAME, null, info.isReadable() ? Boolean.TRUE : Boolean.FALSE);
      context.serialize(IS_WRITABLE_QNAME, null, info.isWritable() ? Boolean.TRUE : Boolean.FALSE);
      context.serialize(IS_IS_QNAME, null, info.isIs() ? Boolean.TRUE : Boolean.FALSE);
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

      Element readableElement = types.createElement(SCHEMA_ELEMENT);
      readableElement.setAttribute("name", IS_READABLE);
      readableElement.setAttribute("type", XMLType.XSD_BOOLEAN.getLocalPart());
      allElement.appendChild(readableElement);

      Element writableElement = types.createElement(SCHEMA_ELEMENT);
      writableElement.setAttribute("name", IS_WRITABLE);
      writableElement.setAttribute("type", XMLType.XSD_BOOLEAN.getLocalPart());
      allElement.appendChild(writableElement);

      Element isElement = types.createElement(SCHEMA_ELEMENT);
      isElement.setAttribute("name", IS_IS);
      isElement.setAttribute("type", XMLType.XSD_BOOLEAN.getLocalPart());
      allElement.appendChild(isElement);

      return complexType;
   }
}
