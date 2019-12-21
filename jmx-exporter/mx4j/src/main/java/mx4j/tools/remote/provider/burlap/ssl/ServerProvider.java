/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.provider.burlap.ssl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;

import mx4j.tools.remote.http.HTTPConnectorServer;

/**
 * @version $Revision: 1.2 $
 */
public class ServerProvider implements JMXConnectorServerProvider
{
   public JMXConnectorServer newJMXConnectorServer(JMXServiceURL url, Map environment, MBeanServer server) throws IOException
   {
      String protocol = url.getProtocol();
      if (!"burlap+ssl".equals(protocol)) throw new MalformedURLException("Wrong protocol " + protocol + " for provider " + this);
      return new HTTPConnectorServer(url, environment, server);
   }
}
