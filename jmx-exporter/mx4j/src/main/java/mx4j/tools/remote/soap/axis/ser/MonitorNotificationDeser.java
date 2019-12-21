/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.ObjectName;
import javax.management.monitor.MonitorNotification;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.4 $
 */
public class MonitorNotificationDeser extends NotificationDeser
{
   private ObjectName monitoredName;
   private String monitoredAttribute;
   private Object gaugeValue;
   private Object triggerValue;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      super.onSetChildValue(value, hint);
      if (MonitorNotificationSer.DERIVED_GAUGE.equals(hint))
         gaugeValue = value;
      else if (MonitorNotificationSer.OBSERVED_ATTRIBUTE.equals(hint))
         monitoredAttribute = (String)value;
      else if (MonitorNotificationSer.OBSERVED_OBJECT.equals(hint))
         monitoredName = (ObjectName)value;
      else if (MonitorNotificationSer.TRIGGER.equals(hint)) triggerValue = value;
   }

   protected Object createObject() throws SAXException
   {
      // MonitorNotification's constructor is package private:
      // MonitorNotification(String type, Object source, long sequenceNumber, long timeStamp, String message, ObjectName monitoredName, String attribute, Object gauge, Object trigger)
      try
      {
         return AccessController.doPrivileged(new PrivilegedExceptionAction()
         {
            public Object run() throws Exception
            {
               Constructor ctor = MonitorNotification.class.getDeclaredConstructor(new Class[]{String.class, Object.class, long.class, long.class, String.class, ObjectName.class, String.class, Object.class, Object.class});
               // Necessary to invoke package-level constructor
               ctor.setAccessible(true);
               return ctor.newInstance(new Object[]{getType(), getSource(), new Long(getSequenceNumber()), new Long(getTimeStamp()), getMessage(), monitoredName, monitoredAttribute, gaugeValue, triggerValue});
            }
         });
      }
      catch (PrivilegedActionException x)
      {
         throw new SAXException(x.getException());
      }
   }
}
