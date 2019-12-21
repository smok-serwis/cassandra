/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.lang.reflect.Method;

/**
 * Management interface descriptions for the XSLTProcessor MBean.
 *
 * @version $Revision: 1.3 $
 */
public class XSLTProcessorMBeanDescription extends ProcessorMBeanDescription
{
   public String getMBeanDescription()
   {
      return "XSLTPostProcessor which passes the XML message from the HttpAdaptor through a XSL transformation";
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("File"))
      {
         return "The jar/zip file or the directory where to find the XSL files";
      }
      if (attribute.equals("PathInJar"))
      {
         return "The path of the XSL templates inside a jar file";
      }
      if (attribute.equals("DefaultPage"))
      {
         return "The default start page";
      }
      if (attribute.equals("UseJar"))
      {
         return "Indicates whether XSL files are contained in an external jar/zip file";
      }
      if (attribute.equals("UsePath"))
      {
         return "Indicates whether XSL files are contained in an external path";
      }
      if (attribute.equals("UseCache"))
      {
         return "Indicates whether the XSL Templates are cached";
      }
      if (attribute.equals("Locale"))
      {
         return "The locale used to internationalize the output";
      }
      if (attribute.equals("LocaleString"))
      {
         return "Sets the locale used to internationalize the output, as a string";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("addMimeType"))
      {
         return "Adds a MIME type to the default list";
      }
      return super.getOperationDescription(operation);
   }

   public String getOperationParameterName(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("addMimeType"))
      {
         switch (index)
         {
            case 0:
               return "extension";
            case 1:
               return "mimeType";
         }
      }
      return super.getOperationParameterName(method, index);
   }

   public String getOperationParameterDescription(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("addMimeType"))
      {
         switch (index)
         {
            case 0:
               return "The extension of the file";
            case 1:
               return "The MIME type for the extension";
         }
      }
      return super.getOperationParameterDescription(method, index);
   }
}
