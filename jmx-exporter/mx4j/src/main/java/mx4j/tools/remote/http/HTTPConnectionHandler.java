/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.io.IOException;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;

import mx4j.tools.remote.ConnectionManager;
import mx4j.tools.remote.JMXConnection;
import mx4j.tools.remote.JMXConnectionHandler;

/**
 * @version $
 */
public class HTTPConnectionHandler extends JMXConnectionHandler implements HTTPConnection
{
   public HTTPConnectionHandler(JMXConnection connection, ConnectionManager manager, String connectionId)
   {
      super(connection, manager, connectionId);
   }

   public String connect(Object credentials) throws IOException, SecurityException
   {
      throw new Error("Method connect() must not be forwarded to the invocation chain");
   }

   public Integer addNotificationListener(ObjectName name, Object filter, Subject delegate) throws InstanceNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return ((HTTPConnection)getConnection()).addNotificationListener(name, filter, delegate);
   }

   public void removeNotificationListeners(ObjectName name, Integer[] listenerIDs, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      ((HTTPConnection)getConnection()).removeNotificationListeners(name, listenerIDs, delegate);
   }

   public NotificationResult fetchNotifications(long clientSequenceNumber, int maxNotifications, long timeout) throws IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      return ((HTTPConnection)getConnection()).fetchNotifications(clientSequenceNumber, maxNotifications, timeout);
   }
}
