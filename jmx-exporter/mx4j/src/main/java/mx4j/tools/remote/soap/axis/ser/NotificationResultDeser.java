/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.4 $
 */
public class NotificationResultDeser extends AxisDeserializer
{
   private long earliestSequenceNumber;
   private long nextSequenceNumber;
   private TargetedNotification[] targetedNotifications;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (NotificationResultSer.EARLIEST_NUMBER.equals(hint))
         earliestSequenceNumber = ((Long)value).longValue();
      else if (NotificationResultSer.NEXT_NUMBER.equals(hint))
         nextSequenceNumber = ((Long)value).longValue();
      else if (NotificationResultSer.NOTIFICATIONS.equals(hint)) targetedNotifications = (TargetedNotification[])value;
   }

   protected Object createObject() throws SAXException
   {
      return new NotificationResult(earliestSequenceNumber, nextSequenceNumber, targetedNotifications);
   }
}
