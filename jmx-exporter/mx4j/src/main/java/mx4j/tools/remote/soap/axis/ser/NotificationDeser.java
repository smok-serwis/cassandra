/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.Notification;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.4 $
 */
public class NotificationDeser extends AxisDeserializer
{
   private String type;
   private Object source;
   private long sequenceNumber;
   private long timeStamp;
   private String message;
   private Object userData;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (NotificationSer.CLASS_NAME.equals(hint))
         type = (String)value;
      else if (NotificationSer.SOURCE.equals(hint))
         source = value;
      else if (NotificationSer.SEQUENCE_NUMBER.equals(hint))
         sequenceNumber = ((Long)value).longValue();
      else if (NotificationSer.TIMESTAMP.equals(hint))
         timeStamp = ((Long)value).longValue();
      else if (NotificationSer.MESSAGE.equals(hint))
         message = (String)value;
      else if (NotificationSer.USER_DATA.equals(hint)) userData = value;
   }

   protected Object createObject() throws SAXException
   {
      Notification notification = new Notification(getType(), getSource(), getSequenceNumber(), getTimeStamp(), getMessage());
      notification.setUserData(getUserData());
      return notification;
   }

   protected String getType()
   {
      return type;
   }

   protected Object getSource()
   {
      return source;
   }

   protected long getSequenceNumber()
   {
      return sequenceNumber;
   }

   protected long getTimeStamp()
   {
      return timeStamp;
   }

   protected String getMessage()
   {
      return message;
   }

   protected Object getUserData()
   {
      return userData;
   }
}
