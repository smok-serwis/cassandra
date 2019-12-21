/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.5 $
 */
public class ObjectNameDeser extends AxisDeserializer
{
   private String canonicalName;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (ObjectNameSer.NAME.equals(hint)) canonicalName = (String)value;
   }

   protected Object createObject() throws SAXException
   {
      try
      {
         return new ObjectName(canonicalName);
      }
      catch (MalformedObjectNameException x)
      {
         throw new SAXException(x);
      }
   }
}
