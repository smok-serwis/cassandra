/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.relation.RoleList;
import javax.management.relation.RoleResult;
import javax.management.relation.RoleUnresolvedList;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class RoleResultDeser extends AxisDeserializer
{
   private RoleList roleList;
   private RoleUnresolvedList roleUnresolvedList;

   protected void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (RoleResultSer.ROLE_LIST.equals(hint))
         roleList = (RoleList)value;
      if (RoleResultSer.ROLE_UNRESOLVED_LIST.equals(hint))
         roleUnresolvedList = (RoleUnresolvedList)value;
   }

   protected Object createObject() throws SAXException
   {
      return new RoleResult(roleList, roleUnresolvedList);
   }

}
