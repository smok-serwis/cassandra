/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;


/**
 * Management interface descriptions for the ObserverStatisticsRecorder MBean.
 *
 * @version $Revision: 1.3 $
 */
public class ObserverStatisticsRecorderMBeanDescription extends StatisticsRecorderMBeanDescription
{
   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("ObservedAttribute"))
      {
         return "The Attribute to be observed";
      }
      if (attribute.equals("ObservedObject"))
      {
         return "The ObjectName to be observed";
      }
      return super.getAttributeDescription(attribute);
   }
}
