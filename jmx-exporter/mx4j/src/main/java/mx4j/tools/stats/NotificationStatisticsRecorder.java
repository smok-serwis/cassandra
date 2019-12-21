/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import java.util.Date;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * NotificationStatisticsRecorder records statistics of an attribute
 * based on notifications emitted when it changes. The observed MBean has to
 * emit notifications when the value change
 *
 * @version $Revision: 1.4 $
 */
public class NotificationStatisticsRecorder extends ObserverStatisticsRecorder implements NotificationListener
{
   protected boolean registered = false;

   protected void startObserving() throws Exception
   {
      AttributeChangeNotificationFilter filter = new AttributeChangeNotificationFilter();
      filter.enableAttribute(observedAttribute);
      server.addNotificationListener(observedName, this, filter, null);
      registered = true;
   }

   protected void stopObserving() throws Exception
   {
      if (registered)
      {
         server.removeNotificationListener(observedName, this);
      }
   }

   public void handleNotification(Notification notification, Object object)
   {
      AttributeChangeNotification anot = (AttributeChangeNotification)notification;
      addEntry(new Date(), (Number)anot.getNewValue());
   }

   public String toString()
   {
      return "NotificationStatisticsRecorder";
   }
}
