/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap;

/**
 * This 'pivot' is the one to be used when the invocation is uses the
 * https protocol
 *
 * @version $Revision: 1.1 $
 */
public class SSLSOAPService extends SOAPService
{
   protected String getProtocol()
   {
      return "soap+ssl";
   }
}
