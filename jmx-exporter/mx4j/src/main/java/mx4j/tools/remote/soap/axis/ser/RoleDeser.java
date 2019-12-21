/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.util.ArrayList;
import java.util.List;
import javax.management.relation.Role;

import org.apache.axis.Constants;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class RoleDeser extends AxisDeserializer
{
   String roleName;
   List roleValue = new ArrayList();

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (RoleSer.ROLE_NAME.equals(hint)) roleName = (String)value;
      if (Constants.QNAME_LITERAL_ITEM.getLocalPart().equals(hint)) roleValue.add(value);
   }

   protected Object createObject() throws SAXException
   {
      return new Role(roleName, roleValue);
   }
}
