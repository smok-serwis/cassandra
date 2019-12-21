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
 * Represents a connection between a client and a server.
 * A connection has normally a client end and a server end. Both ends will be implemented in
 * a protocol specific way by JSR 160 protocol provider implementations.
 *
 * @version $Revision: 1.4 $
 */
public interface Connection
{
   /**
    * Closes this connection
    */
   public void close() throws IOException;

   /**
    * Returns the connection ID as specified by JSR 160 specification
    */
   public String getConnectionId() throws IOException;
}
