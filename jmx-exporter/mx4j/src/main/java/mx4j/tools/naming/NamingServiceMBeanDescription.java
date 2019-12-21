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
 * Management interface descriptions for the NamingService MBean.
 *
 * @version $Revision: 1.3 $
 */
public class NamingServiceMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "MBean that wraps rmiregistry";
   }

   public String getConstructorDescription(Constructor ctor)
   {
      if (ctor.toString().equals("public mx4j.tools.naming.NamingService()"))
      {
         return "Creates a new instance of NamingService with the default rmiregistry port (1099)";
      }
      if (ctor.toString().equals("public mx4j.tools.naming.NamingService(int)"))
      {
         return "Creates a new instance of NamingService with the specified port";
      }
      return super.getConstructorDescription(ctor);
   }

   public String getConstructorParameterName(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.tools.naming.NamingService(int)"))
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
      if (ctor.toString().equals("public mx4j.tools.naming.NamingService(int)"))
      {
         switch (index)
         {
            case 0:
               return "The port on which rmiregistry will listen for incoming connections";
         }
      }
      return super.getConstructorParameterDescription(ctor, index);
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Port"))
      {
         return "The port on which rmiregistry listens for incoming connections";
      }
      if (attribute.equals("Running"))
      {
         return "The running status of this MBean";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("start"))
      {
         return "Starts rmiregistry";
      }
      if (name.equals("stop"))
      {
         return "Stops rmiregistry";
      }
      return super.getOperationDescription(operation);
   }
}
