/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package mx4j.tools.adaptor.http;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ServerCommandProcessor, processes a request for getting all the
 * MBeans of the current server
 *
 * @version $Revision: 1.3 $
 */
public class ServerCommandProcessor extends HttpCommandProcessorAdaptor
{
   public ServerCommandProcessor()
   {
   }

   public Document executeRequest(HttpInputStream in) throws IOException, JMException
   {
      Document document = builder.newDocument();

      Element root = document.createElement("Server");
      document.appendChild(root);

      String classVariable = in.getVariable("instanceof");
      String queryNames = in.getVariable("querynames");
      Set mbeans = null;
      ObjectName query = null;
      if (queryNames != null)
      {
         try
         {
            query = new ObjectName(queryNames);
            mbeans = new TreeSet(CommandProcessorUtil.createObjectInstanceComparator());
            mbeans.addAll(server.queryMBeans(query, null));
         }
         catch (MalformedObjectNameException e)
         {
            Element exceptionElement = document.createElement("Exception");
            exceptionElement.setAttribute("errorMsg", e.getMessage());
            root.appendChild(exceptionElement);
            return document;
         }
      }
      else
      {
         mbeans = new TreeSet(CommandProcessorUtil.createObjectInstanceComparator());
         mbeans.addAll(server.queryMBeans(null, null));
      }
      Iterator i = mbeans.iterator();
      while (i.hasNext())
      {
         ObjectInstance instance = (ObjectInstance)i.next();
         if (classVariable != null && !classVariable.equals(instance.getClassName()))
         {
            continue;
         }
         Element mBeanElement = document.createElement("MBean");
         mBeanElement.setAttribute("objectname", instance.getObjectName().toString());
         mBeanElement.setAttribute("classname", instance.getClassName());
         MBeanInfo info = server.getMBeanInfo(instance.getObjectName());
         mBeanElement.setAttribute("description", info.getDescription());
         root.appendChild(mBeanElement);
      }
      return document;
   }

}
