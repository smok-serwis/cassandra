/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanOperationInfoDeser extends AxisDeserializer
{
   private String name;
   private String description;
   private MBeanParameterInfo[] signature;
   private String className;
   private int impact;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (MBeanOperationInfoSer.NAME.equals(hint))
         name = (String)value;
      else if (MBeanOperationInfoSer.DESCRIPTION.equals(hint))
         description = (String)value;
      else if (MBeanOperationInfoSer.SIGNATURE.equals(hint))
         signature = (MBeanParameterInfo[])value;
      else if (MBeanOperationInfoSer.CLASS_NAME.equals(hint))
         className = (String)value;
      else if (MBeanOperationInfoSer.IMPACT.equals(hint)) impact = ((Integer)value).intValue();
   }

   protected Object createObject() throws SAXException
   {
      return new MBeanOperationInfo(name, description, signature, className, impact);
   }
}
