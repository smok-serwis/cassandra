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

import mx4j.tools.remote.JMXConnection;

/**
 * @version $Revision: 1.3 $
 */
public interface HTTPConnection extends JMXConnection
{
   /**
    * This method is called when a call initiated by {@link javax.management.remote.JMXConnector#connect}
    * arrives on server side. For HTTP connections, the socket is handled by the web container, but
    * the remote procedure call that arrives along with the HTTP request is parsed and then (normally)
    * forwarded to a JavaBean (that will implement this interface).
    * Implementations of this method will normally call {@link mx4j.tools.remote.ConnectionManager#connect}.
    *
    * @param credentials The credential for authentication
    * @return The connection id for the newly created connection
    * @throws IOException       If a communication problem occurs
    * @throws SecurityException If the authentication fails
    */
   public String connect(Object credentials)
           throws IOException,
                  SecurityException;

   public Integer addNotificationListener(ObjectName name, Object filter, Subject delegate)
           throws InstanceNotFoundException,
                  IOException;

   public void removeNotificationListeners(ObjectName name, Integer[] listenerIDs, Subject delegate)
           throws InstanceNotFoundException,
                  ListenerNotFoundException,
                  IOException;

   public NotificationResult fetchNotifications(long clientSequenceNumber, int maxNotifications, long timeout)
           throws IOException;
}
