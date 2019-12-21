/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanInfoDeser extends AxisDeserializer
{
   private String className;
   private String description;
   private MBeanAttributeInfo[] attributes;
   private MBeanConstructorInfo[] constructors;
   private MBeanOperationInfo[] operations;
   private MBeanNotificationInfo[] notifications;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (MBeanInfoSer.CLASS_NAME.equals(hint))
         className = (String)value;
      else if (MBeanInfoSer.DESCRIPTION.equals(hint))
         description = (String)value;
      else if (MBeanInfoSer.ATTRIBUTES.equals(hint))
         attributes = (MBeanAttributeInfo[])value;
      else if (MBeanInfoSer.CONSTRUCTORS.equals(hint))
         constructors = (MBeanConstructorInfo[])value;
      else if (MBeanInfoSer.OPERATIONS.equals(hint))
         operations = (MBeanOperationInfo[])value;
      else if (MBeanInfoSer.NOTIFICATIONS.equals(hint)) notifications = (MBeanNotificationInfo[])value;
   }

   protected Object createObject() throws SAXException
   {
      return new MBeanInfo(className, description, attributes, constructors, operations, notifications);
   }
}
