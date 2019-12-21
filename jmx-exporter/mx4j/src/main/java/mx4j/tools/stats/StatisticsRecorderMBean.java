/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import java.util.Date;
import java.util.SortedMap;

/**
 * Management interface to be implemented by recorder MBeans.
 * RecorderMBeans store a value and also keep statistics about the given value.
 * Different implementations can determine how to acquire and calculate the value.
 * <p/>
 * The MBean doesn't starts automatically. It has to wait for a {@link #start} call
 *
 * @version $Revision: 1.4 $
 * @see PointTime
 */
public interface StatisticsRecorderMBean
{
   /**
    * Returns the Maximum Value
    */
   public Number getMax();

   /**
    * Returns the Average Value
    */
   public Number getAverage();

   /**
    * Returns the Minimum Value
    */
   public Number getMin();

   /**
    * Returns how many entries may be recorded. When the maximum amount is
    * reached the default behaviour is to forget the oldest one
    */
   public int getMaxEntries();

   /**
    * Sets the maximum entries stored in this recorder
    */
   public void setMaxEntries(int maxEntries);

   /**
    * Returns the date when it started recording
    */
   public Date getRecordingStart();

   /**
    * Returs a sorted map of the recorded values indexed by PointTime
    */
   public SortedMap getEntries();

   /**
    * Indicates whether the MBean is recording values
    */
   public boolean isActive();

   /**
    * Starts recording a variable
    */
   public void start();

   /**
    * Stops recording a variable
    */
   public void stop();
}
