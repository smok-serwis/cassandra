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
 * Implementation of the Connection interface, to be used on the server-side end of the connection.
 *
 * @version $Revision: 1.4 $
 */
public abstract class AbstractConnection implements Connection
{
   private final String connectionId;
   private final ConnectionManager manager;

   protected AbstractConnection(String connectionId, ConnectionManager manager)
   {
      this.connectionId = connectionId;
      this.manager = manager;
   }

   public void close() throws IOException
   {
      manager.closeConnection(this);
   }

   public String getConnectionId() throws IOException
   {
      return connectionId;
   }
}
