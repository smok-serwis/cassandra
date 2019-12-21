/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.naming;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * Management interface descriptions for the CosNamingService MBean.
 *
 * @version $Revision: 1.3 $
 */
public class CosNamingServiceMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "MBean that wraps tnameserv";
   }

   public String getConstructorDescription(Constructor ctor)
   {
      if (ctor.toString().equals("public mx4j.tools.naming.CosNamingService()"))
      {
         return "Creates a new instance of CosNamingService with the default port (900)";
      }
      if (ctor.toString().equals("public mx4j.tools.naming.CosNamingService(int)"))
      {
         return "Creates a new instance of CosNamingService with the specified port";
      }
      return super.getConstructorDescription(ctor);
   }

   public String getConstructorParameterName(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.tools.naming.CosNamingService(int)"))
      {
         switch (index)
         {
            case 0:
               return "port";
         }
      }
      return super.getConstructorParameterName(ctor, index);
   }

   public String getConstructorParameterDescription(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.tools.naming.CosNamingService(int)"))
      {
         switch (index)
         {
            case 0:
               return "The port on which tnameserv will listen for incoming connections";
         }
      }
      return super.getConstructorParameterDescription(ctor, index);
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Port"))
      {
         return "The port on which tnameserv listens for incoming connections";
      }
      if (attribute.equals("Running"))
      {
         return "The running status of this MBean";
      }
      if (attribute.equals("Delay"))
      {
         return "The delay (ms) for the start() and stop() methods";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("start"))
      {
         return "Starts tnameserv";
      }
      if (name.equals("stop"))
      {
         return "Stops tnameserv";
      }
      return super.getOperationDescription(operation);
   }
}
