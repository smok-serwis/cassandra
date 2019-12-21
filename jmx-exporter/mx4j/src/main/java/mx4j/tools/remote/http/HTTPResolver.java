/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import mx4j.remote.ConnectionResolver;

/**
 * @version $Revision: 1.4 $
 */
public abstract class HTTPResolver extends ConnectionResolver
{
   protected static final String DEFAULT_WEB_CONTAINER_CLASS = "mx4j.tools.remote.http.jetty.JettyWebContainer";

   // TODO: maybe worth to use weak references to hold web containers
   private static Map webContainers = new HashMap();
   private static Map deployedURLs = new HashMap();
   private static final WebContainer EXTERNAL_WEB_CONTAINER = new ExternalWebContainer();

   public Object bindClient(Object client, Map environment) throws IOException
   {
      return client;
   }

   protected String getEndpoint(JMXServiceURL address, Map environment)
   {
      String transport = getEndpointProtocol(environment);
      return transport + getEndpointPath(address);
   }

   protected String getEndpointProtocol(Map environment)
   {
      return "http";
   }

   private String getEndpointPath(JMXServiceURL url)
   {
      String address = url.toString();
      String prefix = "service:jmx:" + url.getProtocol();
      return address.substring(prefix.length());
   }

   public Object createServer(JMXServiceURL url, Map environment) throws IOException
   {
      WebContainer result = null;
      boolean useExternalWebContainer = environment == null ? false : Boolean.valueOf(String.valueOf(environment.get(HTTPConnectorServer.USE_EXTERNAL_WEB_CONTAINER))).booleanValue();
      if (!useExternalWebContainer)
      {
         // Create and start an embedded web container
         String webContainerClassName = environment == null ? null : (String)environment.get(HTTPConnectorServer.EMBEDDED_WEB_CONTAINER_CLASS);
         // Not present, by default use Jetty
         if (webContainerClassName == null || webContainerClassName.length() == 0) webContainerClassName = DEFAULT_WEB_CONTAINER_CLASS;

         result = findWebContainer(url, webContainerClassName);
         if (result == null)
         {
            result = createWebContainer(url, webContainerClassName, environment);
            if (result != null) result.start(url, environment);
         }

         // Nothing present, give up
         if (result == null) throw new IOException("Could not start embedded web container");
      }
      return result;
   }

   private WebContainer findWebContainer(JMXServiceURL url, String webContainerClassName)
   {
      String key = createWebContainerKey(url, webContainerClassName);
      return (WebContainer)webContainers.get(key);
   }

   private String createWebContainerKey(JMXServiceURL url, String webContainerClassName)
   {
      return new StringBuffer(webContainerClassName).append("|").append(url.getHost()).append("|").append(url.getPort()).toString();
   }

   public JMXServiceURL bindServer(Object server, JMXServiceURL url, Map environment) throws IOException
   {
      WebContainer webContainer = (WebContainer)server;
      if (!isDeployed(webContainer, url))
      {
         if (webContainer != null) webContainer.deploy(getServletClassName(), url, environment);
         if (!hasDeployed(webContainer))
         {
            // The jmxconnector web service has never been deployed, deploy it now
            deploy(url, environment);
         }
         addDeployed(webContainer, url);
      }
      return url;
   }

   protected abstract String getServletClassName();

   protected void deploy(JMXServiceURL address, Map environment) throws IOException
   {
   }

   public void unbindServer(Object server, JMXServiceURL address, Map environment) throws IOException
   {
      WebContainer webContainer = (WebContainer)server;
      if (isDeployed(webContainer, address))
      {
         // First undeploy the jmxconnector web service, then undeploy the webContainer: otherwise the service cannot be undeployed
         removeDeployed(webContainer, address);
         if (!hasDeployed(webContainer))
         {
            undeploy(address, environment);
         }
         if (webContainer != null) webContainer.undeploy(getServletClassName(), address, environment);
      }
   }

   protected void undeploy(JMXServiceURL address, Map environment) throws IOException
   {
   }

   public void destroyServer(Object server, JMXServiceURL url, Map environment) throws IOException
   {
      WebContainer webContainer = (WebContainer)server;
      if (webContainer != null && !hasDeployed(webContainer))
      {
         // No more deployed stuff here, shutdown also the web container
         String key = createWebContainerKey(url, server.getClass().getName());
         WebContainer container = (WebContainer)webContainers.remove(key);
         if (webContainer != container) throw new IOException("Trying to stop the wrong web container: " + server + " should be: " + container);
         webContainer.stop();
      }
   }

   protected WebContainer createWebContainer(JMXServiceURL url, String webContainerClassName, Map environment)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (environment != null)
      {
         Object cl = environment.get(JMXConnectorServerFactory.PROTOCOL_PROVIDER_CLASS_LOADER);
         if (cl instanceof ClassLoader) loader = (ClassLoader)cl;
      }

      try
      {
         WebContainer webContainer = (WebContainer)loader.loadClass(webContainerClassName).newInstance();
         String key = createWebContainerKey(url, webContainerClassName);
         webContainers.put(key, webContainer);
         return webContainer;
      }
      catch (Exception x)
      {
      }
      return null;
   }

   private boolean isDeployed(WebContainer webContainer, JMXServiceURL url)
   {
      if (webContainer == null) webContainer = EXTERNAL_WEB_CONTAINER;
      Set urls = (Set)deployedURLs.get(webContainer);
      if (urls == null) return false;
      return urls.contains(url);
   }

   private boolean hasDeployed(WebContainer webContainer)
   {
      if (webContainer == null) webContainer = EXTERNAL_WEB_CONTAINER;
      Set urls = (Set)deployedURLs.get(webContainer);
      if (urls == null) return false;
      return !urls.isEmpty();
   }

   private void addDeployed(WebContainer webContainer, JMXServiceURL url)
   {
      if (webContainer == null) webContainer = EXTERNAL_WEB_CONTAINER;
      Set urls = (Set)deployedURLs.get(webContainer);
      if (urls == null)
      {
         urls = new HashSet();
         deployedURLs.put(webContainer, urls);
      }
      urls.add(url);
   }

   private void removeDeployed(WebContainer webContainer, JMXServiceURL url)
   {
      if (webContainer == null) webContainer = EXTERNAL_WEB_CONTAINER;
      Set urls = (Set)deployedURLs.get(webContainer);
      if (urls != null)
      {
         urls.remove(url);
         if (urls.isEmpty()) deployedURLs.remove(webContainer);
      }
   }

   private static class ExternalWebContainer implements WebContainer
   {
      public void start(JMXServiceURL url, Map environment) throws IOException
      {
      }

      public void stop() throws IOException
      {
      }

      public void deploy(String servletClassName, JMXServiceURL url, Map environment) throws IOException
      {
      }

      public void undeploy(String servletClassName, JMXServiceURL url, Map environment)
      {
      }

      public String toString()
      {
         return "External WebContainer";
      }
   }
}
