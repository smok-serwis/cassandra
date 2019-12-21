/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanConstructorInfoDeser extends AxisDeserializer
{
   private String name;
   private String description;
   private MBeanParameterInfo[] signature;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (MBeanConstructorInfoSer.NAME.equals(hint))
         name = (String)value;
      else if (MBeanConstructorInfoSer.DESCRIPTION.equals(hint))
         description = (String)value;
      else if (MBeanConstructorInfoSer.SIGNATURE.equals(hint)) signature = (MBeanParameterInfo[])value;
   }

   protected Object createObject() throws SAXException
   {
      return new MBeanConstructorInfo(name, description, signature);
   }
}
