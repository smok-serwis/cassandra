/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXServiceURL;

import mx4j.remote.ConnectionResolver;
import mx4j.tools.remote.AbstractJMXConnectorServer;

/**
 * @version $Revision: 1.8 $
 */
public class LocalConnectorServer extends AbstractJMXConnectorServer
{
   private static Map instances = new HashMap();

   public static LocalConnectionManager find(JMXServiceURL url)
   {
      synchronized (LocalConnectorServer.class)
      {
         return (LocalConnectionManager)instances.get(url);
      }
   }

   private MBeanServer mbeanServer;
   private LocalConnectionManager connectionManager;

   public LocalConnectorServer(JMXServiceURL url, Map environment, MBeanServer server)
   {
      super(url, environment, server);
   }

   public MBeanServer getMBeanServer()
   {
      return mbeanServer;
   }

   protected void doStart() throws IOException
   {
      JMXServiceURL address = getAddress();
      String protocol = address.getProtocol();
      Map environment = getEnvironment();
      ConnectionResolver resolver = ConnectionResolver.newConnectionResolver(protocol, environment);
      if (resolver == null) throw new MalformedURLException("Unsupported protocol: " + protocol);

      MBeanServer realServer = null;
      MBeanServer server = super.getMBeanServer();

      MBeanServer resolvedServer = (MBeanServer)resolver.createServer(address, environment);
      if (resolvedServer == null)
      {
         if (server == null) throw new IllegalStateException("This LocalConnectorServer is not attached to an MBeanServer");
         realServer = server;
      }
      else
      {
         if (server == null)
         {
            realServer = resolvedServer;
         }
         else
         {
            if (server != resolvedServer) throw new IllegalStateException("This LocalConnectorServer cannot be attached to 2 MBeanServers");
            realServer = server;
         }
      }
      this.mbeanServer = realServer;

      connectionManager = new LocalConnectionManager(this, environment);

      setAddress(resolver.bindServer(realServer, address, environment));

      // Here is where we give to clients the possibility to access us
      register(getAddress(), connectionManager);
   }

   private void register(JMXServiceURL url, LocalConnectionManager manager) throws IOException
   {
      synchronized (LocalConnectorServer.class)
      {
         if (instances.get(url) != null) throw new IOException("A LocalConnectorServer is already serving at address " + url);
         instances.put(url, manager);
      }
   }

   protected void doStop() throws IOException
   {
      connectionManager.close();

      JMXServiceURL address = getAddress();
      String protocol = address.getProtocol();
      Map environment = getEnvironment();
      ConnectionResolver resolver = ConnectionResolver.newConnectionResolver(protocol, environment);
      if (resolver == null) throw new MalformedURLException("Unsupported protocol: " + protocol);
      MBeanServer server = getMBeanServer();
      resolver.unbindServer(server, address, environment);
      resolver.destroyServer(server, address, environment);

      unregister(address);
   }

   private void unregister(JMXServiceURL url) throws IOException
   {
      synchronized (LocalConnectorServer.class)
      {
         Object removed = instances.remove(url);
         if (removed == null) throw new IOException("No LocalConnectorServer is present for address " + url);
      }
   }
}
