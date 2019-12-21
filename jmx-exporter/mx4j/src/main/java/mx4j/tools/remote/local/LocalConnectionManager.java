/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.local;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServer;
import javax.security.auth.Subject;

import mx4j.tools.remote.AbstractConnectionManager;
import mx4j.tools.remote.Connection;

/**
 * @version $Revision: 1.7 $
 */
class LocalConnectionManager extends AbstractConnectionManager
{
   private final MBeanServer mbeanServer;

   LocalConnectionManager(LocalConnectorServer server, Map environment)
   {
      super(server, environment);
      this.mbeanServer = server.getMBeanServer();
   }

   public String getProtocol()
   {
      return "local";
   }

   public Connection doConnect(String connectionId, Subject subject) throws IOException
   {
      LocalConnection serverInvoker = new LocalServerInvoker(mbeanServer);
      LocalConnection subjectInvoker = LocalSubjectInvoker.newInstance(serverInvoker, subject, getSecurityContext(), getEnvironment());
      return new LocalConnectionHandler(connectionId, this, subjectInvoker);
   }

   protected void doClose() throws IOException
   {
      // Yes, do nothing
   }

   protected void doCloseConnection(Connection connection) throws IOException
   {
      // Yes, do nothing
   }
}
