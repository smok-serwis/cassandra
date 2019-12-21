/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanAttributeInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanAttributeInfoDeser extends AxisDeserializer
{
   private String name;
   private String className;
   private String description;
   private boolean isReadable;
   private boolean isWritable;
   private boolean isIs;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (MBeanAttributeInfoSer.NAME.equals(hint))
         name = (String)value;
      else if (MBeanAttributeInfoSer.CLASS_NAME.equals(hint))
         className = (String)value;
      else if (MBeanAttributeInfoSer.DESCRIPTION.equals(hint))
         description = (String)value;
      else if (MBeanAttributeInfoSer.IS_READABLE.equals(hint))
         isReadable = ((Boolean)value).booleanValue();
      else if (MBeanAttributeInfoSer.IS_WRITABLE.equals(hint))
         isWritable = ((Boolean)value).booleanValue();
      else if (MBeanAttributeInfoSer.IS_IS.equals(hint)) isIs = ((Boolean)value).booleanValue();
   }

   protected Object createObject() throws SAXException
   {
      return new MBeanAttributeInfo(name, className, description, isReadable, isWritable, isIs);
   }
}
