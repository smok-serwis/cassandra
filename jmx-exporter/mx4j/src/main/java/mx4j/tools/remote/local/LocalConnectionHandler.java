/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.local;

import java.io.IOException;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.security.auth.Subject;

import mx4j.tools.remote.JMXConnectionHandler;

/**
 * @version $Revision: 1.3 $
 */
class LocalConnectionHandler extends JMXConnectionHandler implements LocalConnection
{
   LocalConnectionHandler(String connectionId, LocalConnectionManager manager, LocalConnection target)
   {
      super(target, manager, connectionId);
   }

   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback, Subject delegate) throws InstanceNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      ((LocalConnection)getConnection()).addNotificationListener(observed, listener, filter, handback, delegate);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      ((LocalConnection)getConnection()).removeNotificationListener(observed, listener, delegate);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      if (isClosed()) throw new IOException("Connection has been closed");
      ((LocalConnection)getConnection()).removeNotificationListener(observed, listener, filter, handback, delegate);
   }
}
