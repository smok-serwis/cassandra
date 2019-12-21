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
import javax.management.remote.NotificationResult;

import mx4j.remote.AbstractRemoteNotificationClientHandler;
import mx4j.remote.ConnectionNotificationEmitter;
import mx4j.remote.HeartBeat;

/**
 * @version $Revision: 1.3 $
 */
public class HTTPRemoteNotificationClientHandler extends AbstractRemoteNotificationClientHandler
{
   private final HTTPConnection connection;

   public HTTPRemoteNotificationClientHandler(HTTPConnection connection, ConnectionNotificationEmitter emitter, HeartBeat heartbeat, Map environment)
   {
      super(emitter, heartbeat, environment);
      this.connection = connection;
   }

   protected NotificationResult fetchNotifications(long sequence, int maxNumber, long timeout) throws IOException
   {
      return connection.fetchNotifications(sequence, maxNumber, timeout);
   }
}
