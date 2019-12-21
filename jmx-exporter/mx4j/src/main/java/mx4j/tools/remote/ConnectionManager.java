/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote;

import java.io.IOException;

/**
 * A ConnectionManager is a server-side object that acts as the factory for new connections and
 * manages the authentication details.
 * A JMXConnectorServer delegates a ConnectionManager for the creation of connections, and a
 * ConnectionManager interacts with the JMXConnectorServer to emit connection notifications.
 * It is the first object contacted by a remote client to obtain a client-specific connection, that is,
 * a connection with a specific connection ID.
 *
 * @version $Revision: 1.4 $
 */
public interface ConnectionManager
{
   /**
    * Factory method that creates connections that are specific to the client that invoked this method.
    *
    * @param credentials The credentials sent by the client to authenticate a subject.
    * @return A new client-specific connection.
    * @throws IOException       If the connection cannot be created.
    * @throws SecurityException If the authentication fails.
    */
   public Connection connect(Object credentials) throws IOException, SecurityException;

   /**
    * Returns the protocol used by the corrispondent JMXConnectorServer.
    */
   public String getProtocol();

   /**
    * Closes this ConnectionManager and all the opened connections it manages.
    *
    * @see #closeConnection
    */
   public void close() throws IOException;

   /**
    * Closes the given Connection.
    * This method is called by the connection manager when it is closing the connections it manages,
    * or as a consequence of the fact that the client end of the connection has been closed.
    *
    * @see Connection#close
    * @see #close
    */
   public void closeConnection(Connection connection) throws IOException;
}
