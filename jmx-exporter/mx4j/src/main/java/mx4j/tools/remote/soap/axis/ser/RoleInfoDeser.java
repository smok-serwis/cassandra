/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.relation.RoleInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class RoleInfoDeser extends AxisDeserializer
{
   private String name;
   private String description;
   private String refMBeanClassName;
   private boolean readable;
   private boolean writeable;
   private int minDegree;
   private int maxDegree;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (RoleInfoSer.NAME.equals(hint)) name = (String)value;
      if (RoleInfoSer.DESCRIPTION.equals(hint)) description = (String)value;
      if (RoleInfoSer.REF_MBEAN_CLASS_NAME.equals(hint)) refMBeanClassName = (String)value;
      if (RoleInfoSer.READABLE.equals(hint)) readable = ((Boolean)value).booleanValue();
      if (RoleInfoSer.WRITEABLE.equals(hint)) writeable = ((Boolean)value).booleanValue();
      if (RoleInfoSer.MIN_DEGREE.equals(hint)) minDegree = ((Integer)value).intValue();
      if (RoleInfoSer.MAX_DEGREE.equals(hint)) maxDegree = ((Integer)value).intValue();
   }

   protected Object createObject() throws SAXException
   {
      try
      {
         RoleInfo roleInfo =
                 new RoleInfo(name, refMBeanClassName, readable, writeable, minDegree, maxDegree, description);
         return roleInfo;
      }
      catch (Exception e)
      {
         throw new SAXException(e);
      }
   }

}
