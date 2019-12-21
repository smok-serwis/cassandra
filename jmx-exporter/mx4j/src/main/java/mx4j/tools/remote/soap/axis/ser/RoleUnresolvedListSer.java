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
import javax.management.relation.RoleUnresolved;
import javax.management.relation.RoleUnresolvedList;
import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.3 $
 */
public class RoleUnresolvedListSer extends AxisSerializer
{
   static final String TYPE = "RoleUnresolvedList";

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      RoleUnresolvedList list = (RoleUnresolvedList)value;
      context.startElement(name, attributes);
      for (Iterator i = list.iterator(); i.hasNext();)
      {
         RoleUnresolved item = (RoleUnresolved)i.next();
         context.serialize(Constants.QNAME_LITERAL_ITEM, null, item);
      }
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      types.writeSchemaElement(Constants.SOAP_VECTOR, complexType);
      Element sequence = types.createElement(SCHEMA_SEQUENCE);
      complexType.appendChild(sequence);
      Element element = types.createElement(SCHEMA_ELEMENT);
      element.setAttribute("name", Constants.QNAME_LITERAL_ITEM.getLocalPart());
      element.setAttribute("minOccurs", "0");
      element.setAttribute("maxOccurs", "unbounded");
      element.setAttribute("type", RoleUnresolvedSer.TYPE);
      sequence.appendChild(element);
      return complexType;
   }

}
