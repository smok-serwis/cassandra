/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * Descriptions of the {@link StatisticsRecorderMBean} interface
 *
 * @version $Revision: 1.4 $
 * @see PointTime
 */
public class StatisticsRecorderMBeanDescription extends MBeanDescriptionAdapter
{
   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Max"))
      {
         return "Maximum observed value";
      }
      if (attribute.equals("Min"))
      {
         return "Minimum observed value";
      }
      if (attribute.equals("Average"))
      {
         return "Average of the observed values";
      }
      if (attribute.equals("MaxEntries"))
      {
         return "Amount of values stored in memory";
      }
      if (attribute.equals("RecordingStart"))
      {
         return "Date when the recording was inited";
      }
      if (attribute.equals("Entries"))
      {
         return "SortedMap of the recorded values indexed by PointTime values";
      }
      if (attribute.equals("Active"))
      {
         return "Indicates whether the MBean is recording";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      if (operation.equals("start"))
      {
         return "Starts the recording";
      }
      if (operation.equals("stop"))
      {
         return "Stops the recording";
      }
      return super.getOperationDescription(operation);
   }
}
