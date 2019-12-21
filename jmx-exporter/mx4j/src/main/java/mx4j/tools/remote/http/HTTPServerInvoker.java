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
import javax.management.MBeanServerConnection;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;

import mx4j.remote.NotificationTuple;
import mx4j.remote.RemoteNotificationServerHandler;
import mx4j.tools.remote.AbstractServerInvoker;

/**
 * Implementation of the HTTPConnector interface that forwards the calls
 * to an MBeanServerConnection object.
 * It handles remote notifications, but it does not handle unmarshalling of
 * arguments (and all related classloading problems).
 *
 * @version $Revision: 1.3 $
 */
public class HTTPServerInvoker extends AbstractServerInvoker implements HTTPConnection
{
   private final RemoteNotificationServerHandler notificationHandler;

   public HTTPServerInvoker(MBeanServerConnection server, RemoteNotificationServerHandler handler)
   {
      super(server);
      this.notificationHandler = handler;
   }

   public String connect(Object credentials) throws IOException, SecurityException
   {
      return null;
   }

   public void close() throws IOException
   {
      NotificationTuple[] tuples = notificationHandler.close();
      for (int i = 0; i < tuples.length; ++i)
      {
         NotificationTuple tuple = tuples[i];
         try
         {
            getServer().removeNotificationListener(tuple.getObjectName(), tuple.getNotificationListener(), tuple.getNotificationFilter(), tuple.getHandback());
         }
         catch (InstanceNotFoundException ignored)
         {
         }
         catch (ListenerNotFoundException ignored)
         {
         }
      }
   }

   public Integer addNotificationListener(ObjectName name, Object filter, Subject delegate) throws InstanceNotFoundException, IOException
   {
      Integer id = notificationHandler.generateListenerID(name, null);
      NotificationListener listener = notificationHandler.getServerNotificationListener();
      getServer().addNotificationListener(name, listener, null, id);
      notificationHandler.addNotificationListener(id, new NotificationTuple(name, listener, null, id));
      return id;
   }

   public void removeNotificationListeners(ObjectName name, Integer[] listenerIDs, Subject delegate) throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      for (int i = 0; i < listenerIDs.length; ++i)
      {
         Integer id = listenerIDs[i];
         NotificationTuple tuple = notificationHandler.removeNotificationListener(id);
         getServer().removeNotificationListener(name, tuple.getNotificationListener(), tuple.getNotificationFilter(), tuple.getHandback());
      }
   }

   public NotificationResult fetchNotifications(long clientSequenceNumber, int maxNotifications, long timeout) throws IOException
   {
      return notificationHandler.fetchNotifications(clientSequenceNumber, maxNotifications, timeout);
   }
}
