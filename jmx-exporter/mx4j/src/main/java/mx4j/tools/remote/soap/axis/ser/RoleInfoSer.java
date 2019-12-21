/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.relation.RoleInfo;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.3 $
 */
public class RoleInfoSer extends AxisSerializer
{
   static final String TYPE = "RoleInfo";
   static final String MAX_DEGREE = "maxDegree";
   static final String MIN_DEGREE = "minDegree";
   static final String NAME = "name";
   static final String DESCRIPTION = "description";
   static final String REF_MBEAN_CLASS_NAME = "refMBeanClassName";
   static final String READABLE = "readable";
   static final String WRITEABLE = "writeable";

   private static final QName MAX_DEGREE_QNAME = new QName("", MAX_DEGREE);
   private static final QName MIN_DEGREE_QNAME = new QName("", MIN_DEGREE);
   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName DESCRIPTION_QNAME = new QName("", DESCRIPTION);
   private static final QName REF_MBEAN_CLASS_NAME_QNAME = new QName("", REF_MBEAN_CLASS_NAME);
   private static final QName READABLE_QNAME = new QName("", READABLE);
   private static final QName WRITABLE_QNAME = new QName("", WRITEABLE);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      RoleInfo roleInfo = (RoleInfo)value;
      context.startElement(name, attributes);
      context.serialize(MAX_DEGREE_QNAME, null, new Integer(roleInfo.getMaxDegree()));
      context.serialize(MIN_DEGREE_QNAME, null, new Integer(roleInfo.getMaxDegree()));
      context.serialize(NAME_QNAME, null, roleInfo.getName());
      context.serialize(DESCRIPTION_QNAME, null, roleInfo.getDescription());
      context.serialize(REF_MBEAN_CLASS_NAME_QNAME, null, roleInfo.getRefMBeanClassName());
      context.serialize(READABLE_QNAME, null, roleInfo.isReadable() ? Boolean.TRUE : Boolean.FALSE);
      context.serialize(WRITABLE_QNAME, null, roleInfo.isWritable() ? Boolean.TRUE : Boolean.FALSE);
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element maxDegreeElement = types.createElement(SCHEMA_ELEMENT);
      maxDegreeElement.setAttribute("name", MAX_DEGREE);
      maxDegreeElement.setAttribute("type", XMLType.XSD_INT.getLocalPart());
      allElement.appendChild(maxDegreeElement);

      Element minDegreeElement = types.createElement(SCHEMA_ELEMENT);
      minDegreeElement.setAttribute("name", MIN_DEGREE);
      minDegreeElement.setAttribute("type", XMLType.XSD_INT.getLocalPart());
      allElement.appendChild(minDegreeElement);

      Element nameElement = types.createElement(SCHEMA_ELEMENT);
      nameElement.setAttribute("name", NAME);
      nameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(nameElement);

      Element descriptionElement = types.createElement(SCHEMA_ELEMENT);
      descriptionElement.setAttribute("name", DESCRIPTION);
      descriptionElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(descriptionElement);

      Element refMBeanClassNameElement = types.createElement(SCHEMA_ELEMENT);
      refMBeanClassNameElement.setAttribute("name", REF_MBEAN_CLASS_NAME);
      refMBeanClassNameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(refMBeanClassNameElement);

      Element readableElement = types.createElement(SCHEMA_ELEMENT);
      readableElement.setAttribute("name", READABLE);
      readableElement.setAttribute("type", XMLType.XSD_BOOLEAN.getLocalPart());
      allElement.appendChild(readableElement);

      Element writeableElement = types.createElement(SCHEMA_ELEMENT);
      writeableElement.setAttribute("name", WRITEABLE);
      writeableElement.setAttribute("type", XMLType.XSD_BOOLEAN.getLocalPart());
      allElement.appendChild(writeableElement);

      return complexType;
   }

}
