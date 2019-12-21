/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http.jetty;

import java.io.IOException;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.tools.remote.http.HTTPConnectorServer;
import mx4j.tools.remote.http.WebContainer;
import org.mortbay.http.HttpListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;

/**
 * Jetty's implementation of WebContainer interface
 *
 * @version $Revision: 1.5 $
 */
public class JettyWebContainer implements WebContainer
{
   private final Server server;

   public JettyWebContainer()
   {
      server = new Server();
   }

   protected Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   protected Server getServer()
   {
      return server;
   }

   public void start(JMXServiceURL url, Map environment) throws IOException
   {
      try
      {
         configure(url, environment);
         getServer().start();
      }
      catch (IOException x)
      {
         throw x;
      }
      catch (Exception x)
      {
         throw new IOException(x.toString());
      }
   }

   private void configure(JMXServiceURL url, Map environment) throws IOException
   {
      Logger logger = getLogger();

      if (environment != null)
      {
         Object config = environment.get(HTTPConnectorServer.WEB_CONTAINER_CONFIGURATION);
         if (config instanceof String)
         {
            if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Configuring Jetty with configuration " + config);
            getServer().configure((String)config);

            // Be sure there is at least one listener on the port specified by the JMXServiceURL
            HttpListener[] listeners = getServer().getListeners();
            if (listeners != null)
            {
               boolean found = false;
               for (int i = 0; i < listeners.length; ++i)
               {
                  HttpListener listener = listeners[i];
                  if (listener.getPort() == url.getPort())
                  {
                     found = true;
                     break;
                  }
               }
               if (!found) throw new IOException("No listener configured with configuration " + config + " matches JMXServiceURL " + url);
               // Configured successfully, return
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Configured Jetty successfully with configuration " + config);
               return;
            }
            else
            {
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Jetty configuration " + config + " does not have any listener");
            }
         }
         else
         {
            if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Skipping Jetty configuration " + config + " (must be a String)");
         }
      }
      configureListener(url, environment);
   }

   protected void configureListener(JMXServiceURL url, Map environment) throws IOException
   {
      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Configuring Jetty with a default listener on port " + url.getPort());
      getServer().addListener(":" + url.getPort());
   }

   public void stop() throws IOException
   {
      try
      {
         getServer().stop();
      }
      catch (InterruptedException x)
      {
         Thread.currentThread().interrupt();
      }
   }

   public void deploy(String servletClassName, JMXServiceURL url, Map environment) throws IOException
   {
      try
      {
         String urlPattern = resolveServletMapping(url);
         ServletHttpContext context = (ServletHttpContext)getServer().getContext("/");
         context.addServlet(urlPattern, servletClassName);
         // TODO: be sure an undeployed url is not restarted !
         if (!context.isStarted()) context.start();
      }
      catch (Exception x)
      {
         throw new IOException(x.toString());
      }
   }

   public void undeploy(String servletName, JMXServiceURL url, Map environment)
   {
      String urlPattern = resolveServletMapping(url);
      ServletHttpContext context = (ServletHttpContext)getServer().getContext("/");
      ServletHandler handler = context.getServletHandler();
      handler.getServletMap().remove(urlPattern);
   }

   private String resolveServletMapping(JMXServiceURL url)
   {
      String path = url.getURLPath();
      String urlPattern = null;
      if (path.endsWith("/"))
         urlPattern = path + "*";
      else
         urlPattern = path + "/*";
      if (!urlPattern.startsWith("/")) urlPattern = "/" + urlPattern;
      return urlPattern;
   }
}
