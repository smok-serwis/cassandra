/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

/**
 * @version $Revision: 1.4 $
 */
public abstract class ObserverStatisticsRecorder extends AbstractStatisticsRecorder implements ObserverStatisticsRecorderMBean
{
   protected ObjectName observedName = null;

   protected String observedAttribute = null;

   public void setObservedObject(ObjectName object)
   {
      this.observedName = object;
   }

   public ObjectName getObservedObject()
   {
      return observedName;
   }

   public String getObservedAttribute()
   {
      return observedAttribute;
   }

   public void setObservedAttribute(String attribute)
   {
      this.observedAttribute = attribute;
   }

   protected void doStart() throws Exception
   {
      if (observedName == null || observedAttribute == null)
      {
         getLogger().warn(new StringBuffer(this.toString()).append(" cannot start with objectName ").append(observedName).append(" and attribute ").append(observedAttribute).toString());
         stop();
         return;
      }
      if (!server.isRegistered(observedName))
      {
         getLogger().warn(new StringBuffer(this.toString()).append(" cannot start since objectName is not registered").toString());
         stop();
         return;
      }

      MBeanInfo info = server.getMBeanInfo(observedName);
      MBeanAttributeInfo[] attributes = info.getAttributes();
      MBeanAttributeInfo theAttribute = null;
      boolean found = false;
      for (int i = 0; i < attributes.length; i++)
      {
         if (attributes[i].getName().equals(observedAttribute))
         {
            theAttribute = attributes[i];
            found = true;
            break;
         }
      }
      if (!found)
      {
         getLogger().warn(new StringBuffer(this.toString()).append(" cannot start with objectName ").append(observedName).append(" since attribute ").append(observedAttribute).append(" does not belong to the MBean interface").toString());
         stop();
         return;
      }
      if (!theAttribute.isReadable())
      {
         getLogger().warn(new StringBuffer(this.toString()).append(" cannot start with objectName ").append(observedName).append(" since attribute ").append(observedAttribute).append(" is not readable").toString());
         stop();
         return;
      }
      Object value = server.getAttribute(observedName, observedAttribute);
      if (!(value instanceof Number))
      {
         getLogger().warn(new StringBuffer(this.toString()).append(" cannot start with objectName ").append(observedName).append(" since attribute ").append(observedAttribute).append(" is not a number").toString());
         stop();
         return;
      }
      startObserving();
   }

   protected abstract void startObserving() throws Exception;

   protected abstract void stopObserving() throws Exception;

   protected void doStop() throws Exception
   {
      stopObserving();
   }

}
