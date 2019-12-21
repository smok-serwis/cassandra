/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.ObjectInstance;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class ObjectInstanceSer extends AxisSerializer
{
   static final String TYPE = "ObjectInstance";
   static final String OBJECT_NAME = "objectName";
   static final String CLASS_NAME = "className";
   private static final QName OBJECTNAME_QNAME = new QName("", OBJECT_NAME);
   private static final QName CLASS_NAME_QNAME = new QName("", CLASS_NAME);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      ObjectInstance instance = (ObjectInstance)value;
      context.startElement(name, attributes);
      context.serialize(OBJECTNAME_QNAME, null, instance.getObjectName());
      context.serialize(CLASS_NAME_QNAME, null, instance.getClassName());
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element objectNameElement = types.createElement(SCHEMA_ELEMENT);
      objectNameElement.setAttribute("name", OBJECT_NAME);
      objectNameElement.setAttribute("type", ObjectNameSer.TYPE);
      allElement.appendChild(objectNameElement);

      Element classNameElement = types.createElement(SCHEMA_ELEMENT);
      classNameElement.setAttribute("name", CLASS_NAME);
      classNameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(classNameElement);

      return complexType;
   }
}
