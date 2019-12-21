/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;
import javax.management.relation.MBeanServerNotificationFilter;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;

/**
 * @version $Revision: 1.3 $
 */
public class MBeanServerNotificationFilterSer extends NotificationFilterSupportSer
{
   static final String ENABLED_OBJECT_NAME = "enabledObjectName";
   static final String DISABLED_OBJECT_NAME = "disabledObjectName";
   static final String ALL_DISABLED = "allDisabled";
   static final String ALL_ENABLED = "allEnabled";
   private static final QName ENABLED_OBJECT_NAME_QNAME = new QName("", ENABLED_OBJECT_NAME);
   private static final QName DISABLED_OBJECT_NAME_QNAME = new QName("", DISABLED_OBJECT_NAME);
   private static final QName ALL_DISABLED_QNAME = new QName("", ALL_DISABLED);
   private static final QName ALL_ENABLED_QNAME = new QName("", ALL_ENABLED);

   protected void onSerialize(SerializationContext context, NotificationFilterSupport filter) throws IOException
   {
      super.onSerialize(context, filter);

      MBeanServerNotificationFilter serverFilter = (MBeanServerNotificationFilter)filter;
      Vector enabledNames = serverFilter.getEnabledObjectNames();
      Vector disabledNames = serverFilter.getDisabledObjectNames();

      // A special logic should be implemented: an empty vector has a different meaning than a null vector
      // See JMX specification (javadocs) for further details
      if (enabledNames != null)
      {
         if (enabledNames.size() == 0)
         {
            context.serialize(ALL_DISABLED_QNAME, null, Boolean.TRUE);
         }
         else
         {
            context.serialize(ALL_DISABLED_QNAME, null, Boolean.FALSE);
         }
         for (Iterator i = enabledNames.iterator(); i.hasNext();)
         {
            ObjectName enabled = (ObjectName)i.next();
            context.serialize(ENABLED_OBJECT_NAME_QNAME, null, enabled);
         }
      }
      if (disabledNames != null)
      {
         if (disabledNames.size() == 0)
         {
            context.serialize(ALL_ENABLED_QNAME, null, Boolean.TRUE);
         }
         else
         {
            context.serialize(ALL_ENABLED_QNAME, null, Boolean.FALSE);
         }
         for (Iterator i = disabledNames.iterator(); i.hasNext();)
         {
            ObjectName disabled = (ObjectName)i.next();
            context.serialize(DISABLED_OBJECT_NAME_QNAME, null, disabled);
         }
      }
   }

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      // TODO: Use XML Schema syntax to specify that this is a subclass of NotificationFilterSupport
      return null;
   }
}
