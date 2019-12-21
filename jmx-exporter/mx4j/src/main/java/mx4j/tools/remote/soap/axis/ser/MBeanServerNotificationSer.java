/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;

/**
 * @version $Revision: 1.3 $
 */
public class MBeanServerNotificationSer extends NotificationSer
{
   static final String MBEAN_NAME = "mbeanName";
   private static final QName MBEAN_NAME_QNAME = new QName("", MBEAN_NAME);

   protected void onSerialize(SerializationContext context, Notification notification) throws IOException
   {
      super.onSerialize(context, notification);
      MBeanServerNotification serverNotification = (MBeanServerNotification)notification;
      context.serialize(MBEAN_NAME_QNAME, null, serverNotification.getMBeanName());
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      // TODO: Use XML Schema syntax to specify that this is a subclass of Notification
      return super.writeSchema(aClass, types);
   }
}
