/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.ObjectName;
import javax.management.relation.MBeanServerNotificationFilter;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class MBeanServerNotificationFilterDeser extends AxisDeserializer
{
   private MBeanServerNotificationFilter filter = new MBeanServerNotificationFilter();

   protected void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (NotificationFilterSupportSer.NOTIFICATION_TYPE.equals(hint))
         filter.enableType((String)value);
      else if (MBeanServerNotificationFilterSer.ALL_DISABLED.equals(hint) && ((Boolean)value).booleanValue())
         filter.disableAllObjectNames();
      else if (MBeanServerNotificationFilterSer.ALL_ENABLED.equals(hint) && ((Boolean)value).booleanValue())
         filter.enableAllObjectNames();
      else if (MBeanServerNotificationFilterSer.ENABLED_OBJECT_NAME.equals(hint))
         filter.enableObjectName((ObjectName)value);
      else if (MBeanServerNotificationFilterSer.DISABLED_OBJECT_NAME.equals(hint)) filter.disableObjectName((ObjectName)value);
   }

   protected Object createObject() throws SAXException
   {
      return filter;
   }
}
