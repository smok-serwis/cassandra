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
import javax.management.relation.RoleUnresolved;
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
public class RoleUnresolvedSer extends RoleSer
{
   static final String TYPE = "RoleUnresolved";
   static final String PROBLEM_TYPE = "problemType";
   private static final QName PROBLEM_TYPE_QNAME = new QName("", PROBLEM_TYPE);

   public void serialize(QName name, Attributes attributes, Object value,
                         SerializationContext context)
           throws IOException
   {
      RoleUnresolved role = (RoleUnresolved)value;
      context.startElement(name, attributes);
      context.serialize(ROLE_NAME_QNAME, null, role.getRoleName());
      for (Iterator i = role.getRoleValue().iterator(); i.hasNext();)
      {
         ObjectName on = (ObjectName)i.next();
         context.serialize(Constants.QNAME_LITERAL_ITEM, null, on);
      }
      context.serialize(PROBLEM_TYPE_QNAME, null, new Integer(role.getProblemType()));
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = super.writeSchema(aClass, types);
      Element problemType = types.createElement(SCHEMA_ELEMENT);
      problemType.setAttribute("name", PROBLEM_TYPE);
      problemType.setAttribute("type", XMLType.XSD_INT.getLocalPart());
      complexType.appendChild(problemType);
      return complexType;
   }

}
