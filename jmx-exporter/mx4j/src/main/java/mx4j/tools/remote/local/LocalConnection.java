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

import mx4j.tools.remote.JMXConnection;

/**
 * @version $Revision: 1.6 $
 */
public interface LocalConnection extends JMXConnection
{
   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback, Subject delegate)
           throws InstanceNotFoundException, IOException;

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, Subject delegate)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException;

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback, Subject delegate)
           throws InstanceNotFoundException, ListenerNotFoundException, IOException;
}
