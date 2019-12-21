/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.security.auth.Subject;

import mx4j.remote.MX4JRemoteUtils;

/**
 * Implementation of the ConnectionManager interface that implements emission of connection notifications,
 * authentication, and proper closing of connections.
 *
 * @version $Revision: 1.7 $
 */
public abstract class AbstractConnectionManager implements ConnectionManager
{
   private final AbstractJMXConnectorServer server;
   private final Map environment;
   private final AccessControlContext context;
   private final Map connections = new HashMap();
   private volatile boolean closed;

   /**
    * Called by subclasses.
    *
    * @param server      The JMXConnectorServer that will emit connection notifications
    * @param environment The environment passed when the JMXConnectorServer is created.
    */
   protected AbstractConnectionManager(AbstractJMXConnectorServer server, Map environment)
   {
      this.server = server;
      this.environment = environment;
      this.context = AccessController.getContext();
   }

   /**
    * Implemented using the template method pattern, it handles authentication, creation of the connection ID,
    * emission of connection notification of type "opened".
    *
    * @see #doConnect
    * @see #authenticate
    * @see #createConnectionID
    */
   public synchronized Connection connect(Object credentials) throws IOException, SecurityException
   {
      if (isClosed()) throw new IOException("This connection manager is already closed " + this);

      Subject subject = authenticate(credentials);
      String connectionId = createConnectionID(subject);

      Connection client = doConnect(connectionId, subject);
      WeakReference weak = new WeakReference(client);

      synchronized (connections)
      {
         connections.put(connectionId, weak);
      }

      server.connectionOpened(connectionId, "Connection opened " + client, null);

      return client;
   }

   /**
    * Returns a connection ID as specified by JSR 160.
    *
    * @param subject The authenticated Subject
    */
   protected String createConnectionID(Subject subject)
   {
      return MX4JRemoteUtils.createConnectionID(getProtocol(), null, -1, subject);
   }

   /**
    * Template method to be implemented by subclasses; must return the server-side part of
    * a connection.
    * When an remote invocation arrives, it will lookup the corrispondent server-side part
    * of the connection and delegate the call to it. The server-side part of the connection
    * must then (eventually) call the MBeanServer to satisfy the request.
    *
    * @param connectionId The connection ID for connection that is returned
    * @param subject      The authenticated Subject
    * @return The server-side part of a connection (with the given connection ID)
    * @throws IOException If the connection cannot be created
    */
   protected abstract Connection doConnect(String connectionId, Subject subject) throws IOException;

   /**
    * Implemented using the template method pattern
    *
    * @see #doClose
    * @see #closeConnection
    */
   public synchronized void close() throws IOException
   {
      if (isClosed()) return;
      closed = true;
      doClose();
      closeConnections();
   }

   /**
    * Closes this ConnectionManager but not the connections it manages
    *
    * @throws IOException If this ConnectionManager cannot be closed
    */
   protected abstract void doClose() throws IOException;

   private void closeConnections() throws IOException
   {
      IOException clientException = null;
      synchronized (connections)
      {
         while (!connections.isEmpty())
         {
            // Create the iterator every time, since closeConnection() may modify the Map
            Iterator entries = connections.entrySet().iterator();
            Map.Entry entry = (Map.Entry)entries.next();
            WeakReference weak = (WeakReference)entry.getValue();
            Connection connection = (Connection)weak.get();
            if (connection == null)
            {
               // Already GC'ed
               entries.remove();
               continue;
            }
            else
            {
               try
               {
                  connection.close();
               }
               catch (IOException x)
               {
                  if (clientException == null) clientException = x;
               }
            }
         }
      }
      if (clientException != null) throw clientException;
   }

   /**
    * Implemented using the template method pattern, handles the emission of the connection notification
    * of type "closed".
    * This method is called both when closing the connector server and when closing a connector.
    *
    * @see #doCloseConnection
    */
   public void closeConnection(Connection connection) throws IOException
   {
      String connectionID = connection.getConnectionId();
      WeakReference weak = null;
      synchronized (connections)
      {
         weak = (WeakReference)connections.remove(connectionID);
      }
      // Someone may have called stop() and closed all connections in the meanwhile
      if (weak == null) return;

      Connection client = (Connection)weak.get();
      if (connection != client) throw new IOException("Could not find active connection " + connection + ", expecting " + client);

      doCloseConnection(connection);

      server.connectionClosed(connectionID, "Closed connection " + connection, null);
   }

   /**
    * Closes the given Connection.
    */
   protected abstract void doCloseConnection(Connection connection) throws IOException;

   /**
    * Returns whether the {@link #close} method has been called.
    */
   protected boolean isClosed()
   {
      return closed;
   }

   /**
    * Returns the environment passed when creating the JMXConnectorServer
    */
   protected Map getEnvironment()
   {
      return environment;
   }

   /**
    * Returns a security context at the moment of creation of this ConnectionManager.
    * This security context is the restricting context that should be used when a call
    * from a remote client is invoked in a doPrivileged() block.
    */
   protected AccessControlContext getSecurityContext()
   {
      return context;
   }

   /**
    * Authenticates a Subject with the given credentials, by looking up a JMXAuthenticator
    * in the environment returned by {@link #getEnvironment}.
    */
   protected Subject authenticate(Object credentials) throws IOException, SecurityException
   {
      Map environment = getEnvironment();
      if (environment != null)
      {
         JMXAuthenticator authenticator = (JMXAuthenticator)environment.get(JMXConnectorServer.AUTHENTICATOR);
         if (authenticator != null)
         {
            return authenticator.authenticate(credentials);
         }
      }
      return null;
   }
}
