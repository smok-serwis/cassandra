/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.Notification;
import javax.management.remote.TargetedNotification;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.4 $
 */
public class TargetedNotificationDeser extends AxisDeserializer
{
   private Notification notification;
   private Integer listenerID;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (TargetedNotificationSer.NOTIFICATION.equals(hint))
         notification = (Notification)value;
      else if (TargetedNotificationSer.LISTENER_ID.equals(hint)) listenerID = (Integer)value;
   }

   protected Object createObject() throws SAXException
   {
      return new TargetedNotification(notification, listenerID);
   }
}
