/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * Description of the ProcessorMBean interface
 *
 * @version $Revision: 1.3 $
 */
public class ProcessorMBeanDescription extends MBeanDescriptionAdapter
{
   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Name"))
      {
         return "Name of the ProcessorMBean";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      if (operation.getName().equals("writeResponse"))
      {
         return "The method process a xml result document into a suitable response";
      }
      if (operation.getName().equals("writeError"))
      {
         return "The method process a xml error into a suitable response";
      }
      if (operation.getName().equals("preProcess"))
      {
         return "Processes paths allowing for the replacement of a certain path with another";
      }
      if (operation.getName().equals("notFoundElement"))
      {
         return "Method invoked when a path is not found";
      }
      return super.getOperationDescription(operation);
   }
}
