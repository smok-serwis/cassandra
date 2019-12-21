/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.provider.soap.ssl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;

import mx4j.tools.remote.soap.SOAPConnector;

/**
 * @version $Revision: 1.2 $
 */
public class ClientProvider implements JMXConnectorProvider
{
   public JMXConnector newJMXConnector(JMXServiceURL url, Map environment) throws IOException
   {
      String protocol = url.getProtocol();
      if (!"soap+ssl".equals(protocol)) throw new MalformedURLException("Wrong protocol " + protocol + " for provider " + this);
      return new SOAPConnector(url, environment);
   }
}
