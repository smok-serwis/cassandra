/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.relation.RoleResult;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.3 $
 */
public class RoleResultSer extends AxisSerializer
{
   static final String TYPE = "RoleResult";
   static final String ROLE_LIST = "roleList";
   static final String ROLE_UNRESOLVED_LIST = "roleUnresolvedList";
   protected static final QName ROLE_LIST_QNAME = new QName("", ROLE_LIST);
   protected static final QName ROLE_UNRESOLVED_LIST_QNAME = new QName("", ROLE_UNRESOLVED_LIST);

   public void serialize(QName name, Attributes attributes, Object value,
                         SerializationContext context)
           throws IOException
   {
      RoleResult role = (RoleResult)value;
      context.startElement(name, attributes);
      context.serialize(ROLE_LIST_QNAME, null, role.getRoles());
      context.serialize(ROLE_UNRESOLVED_LIST_QNAME, null, role.getRolesUnresolved());
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);

      Element roleListElement = types.createElement(SCHEMA_ELEMENT);
      roleListElement.setAttribute("name", ROLE_LIST);
      roleListElement.setAttribute("type", RoleListSer.TYPE);
      complexType.appendChild(roleListElement);

      Element roleUnresolvedListElement = types.createElement(SCHEMA_ELEMENT);
      roleUnresolvedListElement.setAttribute("name", ROLE_LIST);
      roleUnresolvedListElement.setAttribute("type", RoleUnresolvedListSer.TYPE);
      complexType.appendChild(roleUnresolvedListElement);

      return complexType;
   }

}
