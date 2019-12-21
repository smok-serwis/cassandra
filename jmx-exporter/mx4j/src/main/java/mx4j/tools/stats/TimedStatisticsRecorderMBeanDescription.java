/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

/**
 * Management interface description for TimedStatisticsRecorder MBeans.
 *
 * @version $Revision: 1.3 $
 */
public class TimedStatisticsRecorderMBeanDescription extends ObserverStatisticsRecorderMBeanDescription
{
   public String getAttributeDescription(String attribute)
   {
      if ("Granularity".equals(attribute))
      {
         return "How often the MBean will poll the variable value";
      }
      return super.getAttributeDescription(attribute);
   }
}
