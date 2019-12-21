/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Creates plain ServerSockets.
 *
 * @version $Revision: 1.3 $
 */
public class PlainAdaptorServerSocketFactory implements AdaptorServerSocketFactory
{
   public ServerSocket createServerSocket(int port, int backlog, String host) throws IOException
   {
      return new ServerSocket(port, backlog, InetAddress.getByName(host));
   }
}
