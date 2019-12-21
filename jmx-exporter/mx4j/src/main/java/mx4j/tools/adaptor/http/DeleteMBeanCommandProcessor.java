/*
 *  Copyright (C) The MX4J Contributors.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */
package mx4j.tools.adaptor.http;

import java.io.IOException;
import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * DeleteMBeanCommandProcessor, processes a request for unregistering an MBean
 *
 * @version $Revision: 1.3 $
 */
public class DeleteMBeanCommandProcessor extends HttpCommandProcessorAdaptor
{

   public DeleteMBeanCommandProcessor()
   {
   }


   public Document executeRequest(HttpInputStream in)
           throws IOException, JMException
   {
      Document document = builder.newDocument();

      Element root = document.createElement("MBeanOperation");
      document.appendChild(root);
      Element operationElement = document.createElement("Operation");
      operationElement.setAttribute("operation", "delete");
      root.appendChild(operationElement);

      String objectVariable = in.getVariable("objectname");
      operationElement.setAttribute("objectname", objectVariable);
      if (objectVariable == null || objectVariable.equals(""))
      {
         operationElement.setAttribute("result", "error");
         operationElement.setAttribute("errorMsg", "Incorrect parameters in the request");
         return document;
      }
      ObjectName name = null;
      try
      {
         name = new ObjectName(objectVariable);
      }
      catch (MalformedObjectNameException e)
      {
         operationElement.setAttribute("result", "error");
         operationElement.setAttribute("errorMsg", "Malformed object name");
         return document;
      }

      if (server.isRegistered(name))
      {
         try
         {
            server.unregisterMBean(name);
            operationElement.setAttribute("result", "success");
         }
         catch (Exception e)
         {
            operationElement.setAttribute("result", "error");
            operationElement.setAttribute("errorMsg", e.getMessage());
         }
      }
      else
      {
         if (name != null)
         {
            operationElement.setAttribute("result", "error");
            operationElement.setAttribute("errorMsg", "MBean " + name + " not registered");
         }
      }
      return document;
   }

}

