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
import javax.management.relation.RelationTypeSupport;
import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.3 $
 */
public class RelationTypeSupportSer extends AxisSerializer
{
   static final String TYPE = "RelationTypeSupport";
   static final String NAME = "name";
   static final String ROLE_INFOS = "roleInfos";

   private static final QName NAME_QNAME = new QName("", NAME);
   private static final QName ROLE_INFOS_QNAME = new QName("", ROLE_INFOS);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      RelationTypeSupport relTypeSup = (RelationTypeSupport)value;
      context.startElement(name, attributes);
      context.serialize(NAME_QNAME, null, relTypeSup.getRelationTypeName());
      for (Iterator i = relTypeSup.getRoleInfos().iterator(); i.hasNext();)
      {
         context.serialize(Constants.QNAME_LITERAL_ITEM, null, i.next());
      }
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

      types.writeSchemaElement(Constants.SOAP_VECTOR, complexType);
      Element sequence = types.createElement(SCHEMA_SEQUENCE);
      complexType.appendChild(sequence);
      Element element = types.createElement(SCHEMA_ELEMENT);
      element.setAttribute("name", Constants.QNAME_LITERAL_ITEM.getLocalPart());
      element.setAttribute("minOccurs", "0");
      element.setAttribute("maxOccurs", "unbounded");
      element.setAttribute("type", RoleInfoSer.TYPE);
      sequence.appendChild(element);

      return complexType;
   }

}
