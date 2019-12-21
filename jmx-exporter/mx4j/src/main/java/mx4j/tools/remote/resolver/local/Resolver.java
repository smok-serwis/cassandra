/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.resolver.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

import mx4j.remote.ConnectionResolver;
import mx4j.tools.remote.local.LocalConnectorServer;

/**
 * @version $Revision: 1.1 $
 */
public class Resolver extends ConnectionResolver
{
   private static final String ID_CONTEXT = "/id/";
   private static int connectorID;

   private final Map mbeanServerIds = new HashMap();

   public Object createServer(JMXServiceURL url, Map environment) throws IOException
   {
      String connectorID = findConnectorID(url);
      if (connectorID == null) return null;

      String mbeanServerId = findMBeanServerId(connectorID);
      if (mbeanServerId == null) return null;

      List servers = MBeanServerFactory.findMBeanServer(mbeanServerId);
      if (servers.size() == 1) return servers.get(0);
      return null;
   }

   private String findConnectorID(JMXServiceURL url)
   {
      String path = url.getURLPath();
      if (path == null || !path.startsWith(ID_CONTEXT)) return null;
      return path.substring(ID_CONTEXT.length());
   }

   private String findMBeanServerId(String connectorID)
   {
      synchronized (mbeanServerIds)
      {
         return (String)mbeanServerIds.get(connectorID);
      }
   }

   public JMXServiceURL bindServer(Object server, JMXServiceURL url, Map environment) throws IOException
   {
      String connectorID = findConnectorID(url);
      if (connectorID == null) connectorID = generateConnectorID();

      MBeanServer mbeanServer = (MBeanServer)server;
      try
      {
         String mbeanServerId = (String)mbeanServer.getAttribute(new ObjectName("JMImplementation:type=MBeanServerDelegate"), "MBeanServerId");
         synchronized (mbeanServerIds)
         {
            String existing = findMBeanServerId(connectorID);
            if (existing != null && !existing.equals(mbeanServerId)) throw new IOException("LocalConnectorServer with ID " + connectorID + " is already attached to MBeanServer with ID " + existing);
            mbeanServerIds.put(connectorID, mbeanServerId);
         }
      }
      catch (JMException x)
      {
         throw new IOException("Cannot retrieve MBeanServer ID " + x.toString());
      }

      return new JMXServiceURL(url.getProtocol(), url.getHost(), url.getPort(), ID_CONTEXT + connectorID);
   }

   private String generateConnectorID()
   {
      synchronized (Resolver.class)
      {
         return String.valueOf(++connectorID);
      }
   }

   public void unbindServer(Object server, JMXServiceURL address, Map environment) throws IOException
   {
      String connectorID = findConnectorID(address);
      if (connectorID == null) throw new IOException("Unknown LocalConnectorServer ID: " + address);
      synchronized (mbeanServerIds)
      {
         mbeanServerIds.remove(connectorID);
      }
   }

   public void destroyServer(Object server, JMXServiceURL url, Map environment) throws IOException
   {
   }

   public Object lookupClient(JMXServiceURL url, Map environment) throws IOException
   {
      return LocalConnectorServer.find(url);
   }

   public Object bindClient(Object client, Map environment) throws IOException
   {
      return client;
   }
}
