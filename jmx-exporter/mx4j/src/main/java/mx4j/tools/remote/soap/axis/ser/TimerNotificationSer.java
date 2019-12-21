/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.management.Notification;
import javax.management.timer.TimerNotification;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;

/**
 * @version $Revision: 1.3 $
 */
public class TimerNotificationSer extends NotificationSer
{
   static final String NOTIFICATION_ID = "notificationID";
   private static final QName NOTIFICATION_ID_QNAME = new QName("", NOTIFICATION_ID);

   protected void onSerialize(SerializationContext context, Notification notification) throws IOException
   {
      super.onSerialize(context, notification);
      TimerNotification serverNotification = (TimerNotification)notification;
      context.serialize(NOTIFICATION_ID_QNAME, null, serverNotification.getNotificationID());
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element elem = super.writeSchema(aClass, types);

      Element notID = types.createElement(SCHEMA_ELEMENT);
      notID.setAttribute("name", NOTIFICATION_ID);
      notID.setAttribute("type", XMLType.XSD_INT.getLocalPart());
      elem.appendChild(notID);

      return elem;
   }

}
