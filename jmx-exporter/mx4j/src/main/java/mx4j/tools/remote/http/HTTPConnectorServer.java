/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXServiceURL;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.remote.ConnectionResolver;
import mx4j.tools.remote.AbstractJMXConnectorServer;
import mx4j.tools.remote.ConnectionManager;

/**
 * @version $Revision: 1.5 $
 */
public class HTTPConnectorServer extends AbstractJMXConnectorServer
{
   /**
    * MX4J's implementation uses this property to specify a String that points to the configuration
    * resource used to configure the HTTP server for JSR 160 connectors that use HTTP as transport.
    * For Jetty, the default HTTP server, this can be a URL or a relative path (in this latter case
    * the resource must be in classpath).
    */
   public static final String WEB_CONTAINER_CONFIGURATION = "jmx.remote.x.http.server.configuration";
   public static final String USE_EXTERNAL_WEB_CONTAINER = "jmx.remote.x.http.use.external.web.container";
   public static final String EMBEDDED_WEB_CONTAINER_CLASS = "jmx.remote.x.http.embedded.web.container.class";

   private static Map instances = new HashMap();

   private WebContainer webContainer;
   private ConnectionManager connectionManager;

   public HTTPConnectorServer(JMXServiceURL url, Map environment, MBeanServer server)
   {
      super(url, environment, server);
   }

   protected void doStart() throws IOException, IllegalStateException
   {
      MBeanServer server = getMBeanServer();
      if (server == null) throw new IllegalStateException("This JMXConnectorServer is not attached to an MBeanServer");

      JMXServiceURL address = getAddress();
      String protocol = address.getProtocol();
      Map environment = getEnvironment();
      ConnectionResolver resolver = ConnectionResolver.newConnectionResolver(protocol, environment);
      if (resolver == null) throw new MalformedURLException("Unsupported protocol: " + protocol);

      webContainer = (WebContainer)resolver.createServer(address, environment);

      setAddress(resolver.bindServer(webContainer, address, environment));

      connectionManager = createConnectionManager(this, address, environment);

      // Here is where we give to clients the possibility to access us
      register(getAddress(), connectionManager);
   }

   protected ConnectionManager createConnectionManager(AbstractJMXConnectorServer server, JMXServiceURL url, Map environment)
   {
      return new HTTPConnectionManager(server, url.getProtocol(), environment);
   }

   private void register(JMXServiceURL url, ConnectionManager manager) throws IOException
   {
      synchronized (HTTPConnectorServer.class)
      {
         // TODO: must use weak references to connection managers, otherwise they're not GC'ed
         // TODO: in case the connector server is not stopped cleanly
         if (instances.get(url) != null) throw new IOException("A JMXConnectorServer is already serving at address " + url);
         instances.put(url, manager);
      }
   }

   private void unregister(JMXServiceURL url) throws IOException
   {
      synchronized (HTTPConnectorServer.class)
      {
         Object removed = instances.remove(url);
         if (removed == null) throw new IOException("No JMXConnectorServer is present for address " + url);
      }
   }

   static ConnectionManager find(JMXServiceURL address)
   {
      synchronized (HTTPConnectorServer.class)
      {
         ConnectionManager manager = (ConnectionManager)instances.get(address);
         if (manager != null) return manager;

         Logger logger = Log.getLogger(HTTPConnectorServer.class.getName());
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Known HTTPConnectorServers bound at " + instances.keySet());
         return null;
      }
   }

   protected void doStop() throws IOException
   {
      JMXServiceURL url = getAddress();
      unregister(url);

      if (connectionManager != null)
      {
         connectionManager.close();
         connectionManager = null;
      }

      String protocol = url.getProtocol();
      Map environment = getEnvironment();
      ConnectionResolver resolver = ConnectionResolver.newConnectionResolver(protocol, environment);
      if (resolver == null) throw new MalformedURLException("Unsupported protocol: " + protocol);

      resolver.unbindServer(webContainer, url, environment);

      resolver.destroyServer(webContainer, url, environment);
   }
}
