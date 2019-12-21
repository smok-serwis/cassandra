/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.local;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.security.auth.Subject;

import mx4j.remote.NotificationTuple;
import mx4j.tools.remote.AbstractServerInvoker;

/**
 * @version $Revision: 1.3 $
 */
class LocalServerInvoker extends AbstractServerInvoker implements LocalConnection
{
   private final Set listeners = new HashSet();

   LocalServerInvoker(MBeanServer server)
   {
      super(server);
   }

   public void close() throws IOException
   {
      NotificationTuple[] tuples = null;
      synchronized (listeners)
      {
         tuples = (NotificationTuple[])listeners.toArray(new NotificationTuple[listeners.size()]);
         listeners.clear();
      }
      for (int i = 0; i < tuples.length; i++)
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

   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback, Subject delegate)
           throws InstanceNotFoundException, IOException
   {
      NotificationTuple tuple = new NotificationTuple(observed, listener, filter, handback);
      synchronized (listeners)
      {
         listeners.add(tuple);
      }
      getServer().addNotificationListener(observed, listener, filter, handback);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, Subject delegate)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      NotificationTuple tuple = new NotificationTuple(observed, listener);
      synchronized (listeners)
      {
         listeners.remove(tuple);
      }
      getServer().removeNotificationListener(observed, listener);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback, Subject delegate)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException
   {
      NotificationTuple tuple = new NotificationTuple(observed, listener, filter, handback);
      synchronized (listeners)
      {
         listeners.remove(tuple);
      }
      getServer().removeNotificationListener(observed, listener, filter, handback);
   }
}
