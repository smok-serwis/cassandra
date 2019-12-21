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
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import mx4j.remote.ConnectionResolver;
import mx4j.tools.remote.AbstractJMXConnector;
import mx4j.tools.remote.Connection;
import mx4j.tools.remote.ConnectionManager;

/**
 * @version $Revision: 1.9 $
 */
public class LocalConnector extends AbstractJMXConnector
{
   private transient Connection connection;

   public LocalConnector(JMXServiceURL url, Map environment) throws IOException
   {
      super(url);
   }

   protected void doConnect(Map environment) throws IOException, SecurityException
   {
      JMXServiceURL address = getAddress();
      String protocol = address.getProtocol();
      ConnectionResolver resolver = ConnectionResolver.newConnectionResolver(protocol, environment);
      if (resolver == null) throw new MalformedURLException("Unsupported protocol: " + protocol);

      ConnectionManager server = (ConnectionManager)resolver.lookupClient(address, environment);
      server = (ConnectionManager)resolver.bindClient(server, environment);

      Object credentials = environment == null ? null : environment.get(CREDENTIALS);
      connection = server.connect(credentials);
   }

   protected void doClose() throws IOException
   {
      connection.close();
   }

   protected MBeanServerConnection doGetMBeanServerConnection(Subject delegate) throws IOException
   {
      return new LocalConnectionMBeanServerConnection((LocalConnection)connection, delegate);
   }

   public String getConnectionId() throws IOException
   {
      return connection.getConnectionId();
   }
}
