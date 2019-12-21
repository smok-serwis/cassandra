/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanNotificationInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanNotificationInfoDeser extends AxisDeserializer
{
   private String name;
   private String description;
   private String[] notificationTypes;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (MBeanNotificationInfoSer.NAME.equals(hint))
         name = (String)value;
      else if (MBeanNotificationInfoSer.DESCRIPTION.equals(hint))
         description = (String)value;
      else if (MBeanNotificationInfoSer.NOTIFICATION_TYPES.equals(hint)) notificationTypes = (String[])value;
   }

   protected Object createObject() throws SAXException
   {
      return new MBeanNotificationInfo(notificationTypes, name, description);
   }
}
