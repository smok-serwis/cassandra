/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.relation.RoleUnresolved;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class RoleUnresolvedDeser extends RoleDeser
{
   private int problemType;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      super.onSetChildValue(value, hint);
      if (RoleUnresolvedSer.PROBLEM_TYPE.equals(hint)) problemType = ((Integer)value).intValue();
   }

   protected Object createObject() throws SAXException
   {
      return new RoleUnresolved(roleName, roleValue, problemType);
   }

}
