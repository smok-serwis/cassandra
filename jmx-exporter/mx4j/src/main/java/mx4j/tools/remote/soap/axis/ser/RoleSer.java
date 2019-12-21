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
import javax.management.ObjectName;
import javax.management.relation.Role;
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
public class RoleSer extends AxisSerializer
{
   static final String TYPE = "Role";
   static final String ROLE_NAME = "roleName";
   static final String ROLE_VALUE = "roleValue";
   protected static final QName ROLE_NAME_QNAME = new QName("", ROLE_NAME);
   protected static final QName ROLE_VALUE_QNAME = new QName("", ROLE_VALUE);


   public void serialize(QName name, Attributes attributes, Object value,
                         SerializationContext context)
           throws IOException
   {
      Role role = (Role)value;
      context.startElement(name, attributes);
      context.serialize(ROLE_NAME_QNAME, null, role.getRoleName());
      for (Iterator i = role.getRoleValue().iterator(); i.hasNext();)
      {
         ObjectName on = (ObjectName)i.next();
         context.serialize(Constants.QNAME_LITERAL_ITEM, null, on);
      }
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);

      Element nameElement = types.createElement(SCHEMA_ELEMENT);
      nameElement.setAttribute("name", ROLE_NAME);
      nameElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      complexType.appendChild(nameElement);

      types.writeSchemaElement(Constants.SOAP_VECTOR, complexType);
      Element sequence = types.createElement(SCHEMA_SEQUENCE);
      complexType.appendChild(sequence);
      Element element = types.createElement(SCHEMA_ELEMENT);
      element.setAttribute("name", Constants.QNAME_LITERAL_ITEM.getLocalPart());
      element.setAttribute("minOccurs", "0");
      element.setAttribute("maxOccurs", "unbounded");
      element.setAttribute("type", AttributeSer.TYPE);
      sequence.appendChild(element);
      return complexType;
   }

}
