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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ServerByDomainCommandProcessor, processes a request for getting all the
 * MBeans of the current server grouped by domains
 *
 * @version $Revision: 1.3 $
 */
public class ServerByDomainCommandProcessor extends HttpCommandProcessorAdaptor
{
   public ServerByDomainCommandProcessor()
   {
   }

   public Document executeRequest(HttpInputStream in) throws IOException, JMException
   {
      Document document = builder.newDocument();

      Element root = document.createElement("Server");
      document.appendChild(root);

      String targetClass = in.getVariable("instanceof");
      String queryNames = in.getVariable("querynames");
      ObjectName query = null;
      if (queryNames != null)
      {
         try
         {
            query = new ObjectName(queryNames);
         }
         catch (MalformedObjectNameException e)
         {
            Element exceptionElement = document.createElement("Exception");
            exceptionElement.setAttribute("errorMsg", e.getMessage());
            root.appendChild(exceptionElement);
            return document;
         }
      }
      Set mbeans = server.queryMBeans(query, null);
      Iterator i = mbeans.iterator();
      // this will order the domains
      Map domains = new TreeMap();
      while (i.hasNext())
      {
         ObjectInstance instance = (ObjectInstance)i.next();
         ObjectName name = instance.getObjectName();
         String domain = name.getDomain();
         if (domains.containsKey(domain))
         {
            ((Set)domains.get(domain)).add(name);
         }
         else
         {
            Set objects = new TreeSet(CommandProcessorUtil.createObjectNameComparator());
            objects.add(name);
            domains.put(domain, objects);
         }
      }
      i = domains.keySet().iterator();
      while (i.hasNext())
      {
         String domain = (String)i.next();
         Element domainElement = document.createElement("Domain");
         root.appendChild(domainElement);
         domainElement.setAttribute("name", domain);
         Set names = (Set)domains.get(domain);
         Iterator j = names.iterator();
         while (j.hasNext())
         {
            ObjectName targetName = (ObjectName)j.next();
            if (targetClass != null && !server.isInstanceOf(targetName, targetClass))
            {
               continue;
            }
            Element mBeanElement = document.createElement("MBean");
            mBeanElement.setAttribute("objectname", targetName.toString());
            MBeanInfo info = server.getMBeanInfo(targetName);
            mBeanElement.setAttribute("description", info.getDescription());
            mBeanElement.setAttribute("classname", info.getClassName());
            domainElement.appendChild(mBeanElement);
         }
      }
      return document;
   }

}
