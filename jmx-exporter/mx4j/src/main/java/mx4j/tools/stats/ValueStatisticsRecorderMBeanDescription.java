/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;


/**
 * MBean description. * @see StatisticsMBean
 *
 * @version $Revision: 1.3 $
 */
public class ValueStatisticsRecorderMBeanDescription extends StatisticsRecorderMBeanDescription
{
   public String getAttributeDescription(String attribute)
   {
      if ("Value".equals(attribute))
      {
         return "The value to be recorded";
      }
      return super.getAttributeDescription(attribute);
   }
}
