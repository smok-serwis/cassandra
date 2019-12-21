/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.MBeanOperationInfo;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.5 $
 */
public class MBeanOperationInfoSer extends AxisSerializer
{
   static final String TYPE = "MBeanOperationInfo";
   static final String NAME = "name";
   static final String CLASS_NAME = "type";
   static final String DESCRIPTION = "description";
   static final String SIGNATURE = "signature";
   static final String IMPACT = "impact";
   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName CLASS_NAME_QNAME = new QName("", CLASS_NAME);
   private static final QName DESCRIPTION_QNAME = new QName("", DESCRIPTION);
   private static final QName SIGNATURE_QNAME = new QName("", SIGNATURE);
   private static final QName IMPACT_QNAME = new QName("", IMPACT);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      MBeanOperationInfo info = (MBeanOperationInfo)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, info.getName());
      context.serialize(DESCRIPTION_QNAME, null, info.getDescription());
      context.serialize(SIGNATURE_QNAME, null, info.getSignature());
      context.serialize(CLASS_NAME_QNAME, null, info.getReturnType());
      context.serialize(IMPACT_QNAME, null, new Integer(info.getImpact()));
      context.endElement();
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element nameElement = types.createElement(SCHEMA_ELEMENT);
      nameElement.setAttribute("name", NAME);
      nameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(nameElement);

      Element descrElement = types.createElement(SCHEMA_ELEMENT);
      descrElement.setAttribute("name", DESCRIPTION);
      descrElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(descrElement);

      Element signatureElement = types.createElement(SCHEMA_ELEMENT);
      signatureElement.setAttribute("name", SIGNATURE);
      signatureElement.setAttribute("type", XMLType.SOAP_ARRAY.getLocalPart());
      allElement.appendChild(signatureElement);

      Element typeElement = types.createElement(SCHEMA_ELEMENT);
      typeElement.setAttribute("name", CLASS_NAME);
      typeElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      allElement.appendChild(typeElement);

      Element impactElement = types.createElement(SCHEMA_ELEMENT);
      impactElement.setAttribute("name", IMPACT);
      impactElement.setAttribute("type", XMLType.XSD_INT.getLocalPart());
      allElement.appendChild(impactElement);

      return complexType;
   }
}
