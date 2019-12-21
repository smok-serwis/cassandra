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
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.security.auth.Subject;

import mx4j.remote.NotificationTuple;
import mx4j.remote.RemoteNotificationClientHandler;
import mx4j.tools.remote.JMXConnection;
import mx4j.tools.remote.JMXConnectionMBeanServerConnection;

/**
 * Implementation of an adapter that converts MBeanServerConnection calls
 * to HTTPConnection calls.
 * It handles remote notifications, but it does not handle unmarshalling of
 * arguments (and all related classloading problems).
 * NotificationFilters are always invoked on client side.
 *
 * @version $Revision: 1.3 $
 */
public class HTTPConnectionMBeanServerConnection extends JMXConnectionMBeanServerConnection
{
   private final RemoteNotificationClientHandler notificationHandler;

   public HTTPConnectionMBeanServerConnection(JMXConnection connection, Subject delegate, RemoteNotificationClientHandler notificationHandler)
   {
      super(connection, delegate);
      this.notificationHandler = notificationHandler;
   }

   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, IOException
   {
      NotificationTuple tuple = new NotificationTuple(observed, listener, filter, handback);
      // Filters are always invoked on client side, for now
      tuple.setInvokeFilter(true);
      if (notificationHandler.contains(tuple)) return;
      Integer id = ((HTTPConnection)getConnection()).addNotificationListener(observed, null, getDelegateSubject());
      notificationHandler.addNotificationListener(id, tuple);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Integer[] ids = notificationHandler.getNotificationListeners(new NotificationTuple(observed, listener));
      if (ids == null) throw new ListenerNotFoundException("Could not find listener " + listener);
      ((HTTPConnection)getConnection()).removeNotificationListeners(observed, ids, getDelegateSubject());
      notificationHandler.removeNotificationListeners(ids);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      Integer id = notificationHandler.getNotificationListener(new NotificationTuple(observed, listener, filter, handback));
      if (id == null) throw new ListenerNotFoundException("Could not find listener " + listener + " with filter " + filter + " and handback " + handback);
      Integer[] ids = new Integer[]{id};
      ((HTTPConnection)getConnection()).removeNotificationListeners(observed, ids, getDelegateSubject());
      notificationHandler.removeNotificationListeners(ids);
   }
}
