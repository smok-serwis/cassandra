/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.security.auth.Subject;

import mx4j.remote.DefaultRemoteNotificationServerHandler;
import mx4j.remote.RemoteNotificationServerHandler;
import mx4j.tools.remote.AbstractConnectionManager;
import mx4j.tools.remote.AbstractJMXConnectorServer;
import mx4j.tools.remote.Connection;

/**
 * @version $Revision: 1.4 $
 */
public class HTTPConnectionManager extends AbstractConnectionManager
{
   private final MBeanServerConnection mbeanServerConnection;
   private final String protocol;

   public HTTPConnectionManager(AbstractJMXConnectorServer server, String protocol, Map environment)
   {
      super(server, environment);
      this.mbeanServerConnection = server.getMBeanServer();
      this.protocol = protocol;
   }

   public String getProtocol()
   {
      return protocol;
   }

   protected Connection doConnect(String connectionId, Subject subject) throws IOException
   {
      RemoteNotificationServerHandler notificationHandler = new DefaultRemoteNotificationServerHandler(getEnvironment());
      HTTPConnection invoker = new HTTPServerInvoker(mbeanServerConnection, notificationHandler);
      HTTPConnection subjectInvoker = HTTPSubjectInvoker.newInstance(invoker, subject, getSecurityContext(), getEnvironment());
      Connection handler = new HTTPConnectionHandler(subjectInvoker, this, connectionId);
      return handler;
   }

   /**
    * HTTPConnectionManager does not really manages connections,
    * so this method does nothing by default
    */
   protected void doClose() throws IOException
   {
   }

   /**
    * HTTPConnectionManager does not really manages connections,
    * so this method does nothing by default
    */
   protected void doCloseConnection(Connection connection) throws IOException
   {
   }
}
