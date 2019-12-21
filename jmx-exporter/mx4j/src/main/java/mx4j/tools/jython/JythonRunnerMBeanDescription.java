/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.jython;

import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * Management interface descriptions for the JythonRunner MBean.
 *
 * @version $Revision: 1.7 $
 */
public class JythonRunnerMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "Runs a jython script for management purposes";
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("NotificationType"))
      {
         return "The Notification type that triggers the script execution";
      }
      if (attribute.equals("ObservedObject"))
      {
         return "The ObjectName being observed";
      }
      if (attribute.equals("UseText"))
      {
         return "Indicates wether a text based or file based script is used";
      }
      if (attribute.equals("Script"))
      {
         return "The script text";
      }
      if (attribute.equals("ScriptURL"))
      {
         return "The script's URL";
      }
      if (attribute.equals("CacheScript"))
      {
         return "Indicates whether the script is read every time or only once";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("runScript"))
      {
         return "Runs the jython script";
      }
      return super.getOperationDescription(operation);
   }
}
