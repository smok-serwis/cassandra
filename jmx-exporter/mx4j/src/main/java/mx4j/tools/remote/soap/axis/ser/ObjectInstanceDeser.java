/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class ObjectInstanceDeser extends AxisDeserializer
{
   private ObjectName objectName;
   private String className;

   protected void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (ObjectInstanceSer.CLASS_NAME.equals(hint))
         className = (String)value;
      else if (ObjectInstanceSer.OBJECT_NAME.equals(hint)) objectName = (ObjectName)value;
   }

   protected Object createObject() throws SAXException
   {
      return new ObjectInstance(objectName, className);
   }
}
