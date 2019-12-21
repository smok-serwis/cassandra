/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.provider.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;

import mx4j.tools.remote.local.LocalConnectorServer;

/**
 * @version $Revision: 1.4 $
 */
public class ServerProvider implements JMXConnectorServerProvider
{
   public JMXConnectorServer newJMXConnectorServer(JMXServiceURL url, Map environment, MBeanServer server) throws IOException
   {
      String protocol = url.getProtocol();
      if (!"local".equals(protocol)) throw new MalformedURLException("Wrong protocol " + protocol + " for provider " + this);
      return new LocalConnectorServer(url, environment, server);
   }
}
