/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * The ServerSocket factory interface. <p>
 * It allows to create ServerSocket for JMX adaptors
 *
 * @version $Revision: 1.3 $
 */
public interface AdaptorServerSocketFactory
{
   /**
    * Creates a new ServerSocket on the specified port, with the specified backlog and on the given host. <br>
    * The last parameter is useful for hosts with more than one IP address.
    */
   public ServerSocket createServerSocket(int port, int backlog, String host) throws IOException;
}
