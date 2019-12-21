/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanParameterInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanParameterInfoDeser extends AxisDeserializer
{
   private String name;
   private String className;
   private String description;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (MBeanParameterInfoSer.NAME.equals(hint))
         name = (String)value;
      else if (MBeanParameterInfoSer.CLASS_NAME.equals(hint))
         className = (String)value;
      else if (MBeanParameterInfoSer.DESCRIPTION.equals(hint)) description = (String)value;
   }

   protected Object createObject() throws SAXException
   {
      return new MBeanParameterInfo(name, className, description);
   }
}
