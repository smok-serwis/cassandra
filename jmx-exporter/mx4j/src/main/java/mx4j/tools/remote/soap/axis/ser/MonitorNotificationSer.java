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
import javax.management.monitor.MonitorNotification;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;

/**
 * @version $Revision: 1.4 $
 */
public class MonitorNotificationSer extends NotificationSer
{
   static final String TYPE = "MonitorNotification";
   static final String DERIVED_GAUGE = "derivedGauge";
   static final String OBSERVED_ATTRIBUTE = "observedAttribute";
   static final String OBSERVED_OBJECT = "observedObject";
   static final String TRIGGER = "trigger";
   private static final QName DERIVED_GAUGE_QNAME = new QName("", DERIVED_GAUGE);
   private static final QName OBSERVED_ATTRIBUTE_QNAME = new QName("", OBSERVED_ATTRIBUTE);
   private static final QName OBSERVED_OBJECT_QNAME = new QName("", OBSERVED_OBJECT);
   private static final QName TRIGGER_QNAME = new QName("", TRIGGER);

   protected void onSerialize(SerializationContext context, Notification notification) throws IOException
   {
      super.onSerialize(context, notification);
      MonitorNotification monNot = (MonitorNotification)notification;
      context.serialize(DERIVED_GAUGE_QNAME, null, monNot.getDerivedGauge());
      context.serialize(OBSERVED_ATTRIBUTE_QNAME, null, monNot.getObservedAttribute());
      context.serialize(OBSERVED_OBJECT_QNAME, null, monNot.getObservedObject());
      context.serialize(TRIGGER_QNAME, null, monNot.getTrigger());
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = super.writeSchema(aClass, types);

      Element derivedGaugeElement = types.createElement(SCHEMA_ELEMENT);
      derivedGaugeElement.setAttribute("name", DERIVED_GAUGE);
      derivedGaugeElement.setAttribute("type", XMLType.XSD_ANYTYPE.getLocalPart());
      complexType.appendChild(derivedGaugeElement);

      Element observedAttElement = types.createElement(SCHEMA_ELEMENT);
      observedAttElement.setAttribute("name", OBSERVED_ATTRIBUTE);
      observedAttElement.setAttribute("type", XMLType.XSD_STRING.getLocalPart());
      complexType.appendChild(observedAttElement);

      Element observedObjectElement = types.createElement(SCHEMA_ELEMENT);
      observedObjectElement.setAttribute("name", OBSERVED_OBJECT);
      observedObjectElement.setAttribute("type", ObjectNameSer.TYPE);
      complexType.appendChild(observedObjectElement);

      Element triggerElement = types.createElement(SCHEMA_ELEMENT);
      triggerElement.setAttribute("name", TRIGGER);
      triggerElement.setAttribute("type", XMLType.XSD_ANYTYPE.getLocalPart());
      complexType.appendChild(triggerElement);

      return complexType;
   }
}
