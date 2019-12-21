/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanServerNotification;
import javax.management.ObjectName;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class MBeanServerNotificationDeser extends NotificationDeser
{
   private ObjectName mbeanName;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      super.onSetChildValue(value, hint);
      if (MBeanServerNotificationSer.MBEAN_NAME.equals(hint)) mbeanName = (ObjectName)value;
   }

   protected Object createObject() throws SAXException
   {
      MBeanServerNotification notification = new MBeanServerNotification(getType(), getSource(), getSequenceNumber(), mbeanName);
      notification.setTimeStamp(getTimeStamp());
      notification.setUserData(getUserData());
      return notification;
   }
}
