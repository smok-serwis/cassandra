/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * @version $Revision: 1.3 $
 */
public class SSLRMIClientSocketFactory implements RMIClientSocketFactory, Serializable
{
   public Socket createSocket(String host, int port) throws IOException
   {
      return SSLSocketFactory.getDefault().createSocket(host, port);
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (this == obj) return true;
      return getClass() == obj.getClass();
   }

   public int hashCode()
   {
      return 13;
   }
}
