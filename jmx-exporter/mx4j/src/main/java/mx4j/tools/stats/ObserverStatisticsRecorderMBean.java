/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import javax.management.ObjectName;

/**
 * Management interface for ObserverStatisticsRecorder MBeans.
 *
 * @version $Revision: 1.3 $
 */
public interface ObserverStatisticsRecorderMBean extends StatisticsRecorderMBean
{
   /**
    * Sets the ObjectName to be observed
    */
   public void setObservedObject(ObjectName object);

   /**
    * Returns the observed ObjectName
    */
   public ObjectName getObservedObject();

   /**
    * Returns the observed Attribute
    */
   public String getObservedAttribute();

   /**
    * Sets the Attribute to be observed
    */
   public void setObservedAttribute(String attribute);
}
