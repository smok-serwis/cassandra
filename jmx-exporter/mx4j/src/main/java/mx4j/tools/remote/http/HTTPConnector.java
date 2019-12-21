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
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import mx4j.remote.ConnectionNotificationEmitter;
import mx4j.remote.ConnectionResolver;
import mx4j.remote.HeartBeat;
import mx4j.remote.RemoteNotificationClientHandler;
import mx4j.tools.remote.AbstractJMXConnector;

/**
 * @version $
 */
public class HTTPConnector extends AbstractJMXConnector
{
   private transient HTTPConnection connection;
   private transient String connectionId;
   private transient HeartBeat heartbeat;
   private transient RemoteNotificationClientHandler notificationHandler;

   public HTTPConnector(JMXServiceURL address, Map environment) throws IOException
   {
      super(address);
   }

   protected void doConnect(Map environment) throws IOException, SecurityException
   {
      JMXServiceURL address = getAddress();
      String protocol = address.getProtocol();
      ConnectionResolver resolver = ConnectionResolver.newConnectionResolver(protocol, environment);
      if (resolver == null) throw new MalformedURLException("Unsupported protocol: " + protocol);

      HTTPConnection temp = (HTTPConnection)resolver.lookupClient(address, environment);
      connection = (HTTPConnection)resolver.bindClient(temp, environment);

      Object credentials = environment == null ? null : environment.get(CREDENTIALS);
      connectionId = connection.connect(credentials);

      this.heartbeat = createHeartBeat(connection, getConnectionNotificationEmitter(), environment);
      this.notificationHandler = createRemoteNotificationClientHandler(connection, getConnectionNotificationEmitter(), heartbeat, environment);

      this.heartbeat.start();
      this.notificationHandler.start();
   }

   protected HeartBeat createHeartBeat(HTTPConnection connection, ConnectionNotificationEmitter emitter, Map environment)
   {
      return new HTTPHeartBeat(connection, emitter, environment);
   }

   protected RemoteNotificationClientHandler createRemoteNotificationClientHandler(HTTPConnection connection, ConnectionNotificationEmitter emitter, HeartBeat heartbeat, Map environment)
   {
      return new HTTPRemoteNotificationClientHandler(connection, emitter, heartbeat, environment);
   }

   protected MBeanServerConnection doGetMBeanServerConnection(Subject delegate) throws IOException
   {
      return new HTTPConnectionMBeanServerConnection(getHTTPConnection(), delegate, getRemoteNotificationClientHandler());
   }

   protected void doClose() throws IOException
   {
      if (notificationHandler != null) notificationHandler.stop();
      if (heartbeat != null) heartbeat.stop();
      if (connection != null) connection.close();
   }

   public String getConnectionId() throws IOException
   {
      return connectionId;
   }

   protected HTTPConnection getHTTPConnection()
   {
      return connection;
   }

   public RemoteNotificationClientHandler getRemoteNotificationClientHandler()
   {
      return notificationHandler;
   }
}
