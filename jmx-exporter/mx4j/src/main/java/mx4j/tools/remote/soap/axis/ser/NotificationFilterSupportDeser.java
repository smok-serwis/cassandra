/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.NotificationFilterSupport;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class NotificationFilterSupportDeser extends AxisDeserializer
{
   private NotificationFilterSupport filter = new NotificationFilterSupport();

   protected void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (NotificationFilterSupportSer.NOTIFICATION_TYPE.equals(hint)) filter.enableType((String)value);
   }

   protected Object createObject() throws SAXException
   {
      return filter;
   }
}
