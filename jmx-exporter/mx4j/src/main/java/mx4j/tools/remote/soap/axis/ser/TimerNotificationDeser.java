/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.timer.TimerNotification;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class TimerNotificationDeser extends NotificationDeser
{
   private Integer notificationID;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      super.onSetChildValue(value, hint);
      if (TimerNotificationSer.NOTIFICATION_ID.equals(hint)) notificationID = (Integer)value;
   }

   protected Object createObject() throws SAXException
   {
      TimerNotification notification = new TimerNotification(getType(), getSource(), getSequenceNumber(), getTimeStamp(), getMessage(), notificationID);
      return notification;
   }

}
