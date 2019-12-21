/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.lang.reflect.Constructor;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.5 $
 */
public class PrincipalDeser extends AxisDeserializer
{
   private String className;
   private String name;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (PrincipalSer.CLASS_NAME.equals(hint))
         className = (String)value;
      else if (PrincipalSer.NAME.equals(hint)) name = (String)value;
   }

   protected Object createObject() throws SAXException
   {
      try
      {
         Class principalClass = Thread.currentThread().getContextClassLoader().loadClass(className);
         Constructor ctor = principalClass.getConstructor(new Class[]{String.class});
         return ctor.newInstance(new Object[]{name});
      }
      catch (Exception x)
      {
         throw new SAXException(x);
      }
   }
}
