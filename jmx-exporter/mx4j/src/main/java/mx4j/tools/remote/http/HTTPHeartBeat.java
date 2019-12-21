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

import mx4j.remote.AbstractHeartBeat;
import mx4j.remote.ConnectionNotificationEmitter;

/**
 * @version $Revision: 1.3 $
 */
public class HTTPHeartBeat extends AbstractHeartBeat
{
   private final HTTPConnection connection;

   public HTTPHeartBeat(HTTPConnection connection, ConnectionNotificationEmitter emitter, Map environment)
   {
      super(emitter, environment);
      this.connection = connection;
   }

   protected void pulse() throws IOException
   {
      connection.getDefaultDomain(null);
   }
}
