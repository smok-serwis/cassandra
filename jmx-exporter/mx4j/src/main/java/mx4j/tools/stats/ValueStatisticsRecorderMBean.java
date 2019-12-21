/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

/**
 * Management interface for the ValueStatisticsRecorder MBean.
 *
 * @version $Revision: 1.3 $
 */
public interface ValueStatisticsRecorderMBean extends StatisticsRecorderMBean
{
   /**
    * Sets the value to be recorded
    */
   public void setValue(Number value);

   /**
    * Returns the recorded value
    */
   public Number getValue();
}
