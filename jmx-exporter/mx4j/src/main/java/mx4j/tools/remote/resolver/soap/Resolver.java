/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.resolver.soap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import mx4j.log.Logger;
import mx4j.tools.remote.http.HTTPResolver;
import mx4j.tools.remote.soap.SOAPClientInvoker;
import org.apache.axis.client.AdminClient;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.transport.http.AxisServlet;
import org.apache.axis.utils.Options;

/**
 * @version $Revision: 1.1 $
 */
public class Resolver extends HTTPResolver
{
   private static final String SERVER_DEPLOY_WSDD = "server-deploy.wsdd";
   private static final String SERVER_UNDEPLOY_WSDD = "server-undeploy.wsdd";
   private static final String CLIENT_WSDD = "client.wsdd";
   private static final String AXIS_DEPLOY_SERVICE = "AdminService";

   public Object lookupClient(JMXServiceURL address, Map environment) throws IOException
   {
      String endpoint = getEndpoint(address, environment);

      InputStream wsdd = getClass().getResourceAsStream(CLIENT_WSDD);
      if (wsdd == null) throw new IOException("Could not find AXIS deployment descriptor");
      Service service = new Service(new FileProvider(wsdd));
      service.setMaintainSession(true);

      return new SOAPClientInvoker(endpoint, service);
   }

   protected String getServletClassName()
   {
      return AxisServlet.class.getName();
   }

   protected void deploy(JMXServiceURL address, Map environment) throws IOException
   {
      String path = address.getURLPath();
      if (!path.endsWith("/")) path += "/";
      String deployPath = path + AXIS_DEPLOY_SERVICE;

      JMXServiceURL temp = new JMXServiceURL(address.getProtocol(), address.getHost(), address.getPort(), deployPath);
      String deployEndpoint = getEndpoint(temp, environment);

      try
      {
         AdminClient deployer = new AdminClient();
         Options options = new Options(null);
         options.setDefaultURL(deployEndpoint);
         InputStream wsdd = getClass().getResourceAsStream(SERVER_DEPLOY_WSDD);
         if (wsdd == null) throw new IOException("Could not find AXIS deployment descriptor");
         deployer.process(options, wsdd);
      }
      catch (RuntimeException x)
      {
         throw x;
      }
      catch (Exception x)
      {
         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.INFO)) logger.info("Exception while deploying AXIS service", x);
         throw new IOException("Could not deploy connector server to AXIS " + x.toString());
      }
   }

   protected void undeploy(JMXServiceURL address, Map environment) throws IOException
   {
      String path = address.getURLPath();
      if (!path.endsWith("/")) path += "/";
      String undeployPath = path + AXIS_DEPLOY_SERVICE;

      JMXServiceURL temp = new JMXServiceURL(address.getProtocol(), address.getHost(), address.getPort(), undeployPath);
      String undeployEndpoint = getEndpoint(temp, environment);

      try
      {
         AdminClient deployer = new AdminClient();
         Options options = new Options(null);
         options.setDefaultURL(undeployEndpoint);
         InputStream wsdd = getClass().getResourceAsStream(SERVER_UNDEPLOY_WSDD);
         if (wsdd == null) throw new IOException("Could not find AXIS deployment descriptor " + SERVER_UNDEPLOY_WSDD);
         deployer.process(options, wsdd);
      }
      catch (Exception x)
      {
         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.INFO)) logger.info("Exception while undeploying AXIS service", x);
         throw new IOException("Could not undeploy connector server " + x.toString());
      }
   }
}
