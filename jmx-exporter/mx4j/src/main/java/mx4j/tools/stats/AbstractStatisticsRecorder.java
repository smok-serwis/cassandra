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
import java.util.TreeMap;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import mx4j.log.Log;
import mx4j.log.Logger;

/**
 * Class AbstractStatisticsRecorder. Abstract Parent of the Stats collector
 * classes. It implements some basic services
 *
 * @version $Revision: 1.6 $
 * @see StatisticsRecorderMBean
 */
public abstract class AbstractStatisticsRecorder implements StatisticsRecorderMBean, MBeanRegistration
{
   /* Indicates whether the Monitor is active */
   protected boolean isActive = false;

   /* MBeanServer reference */
   protected MBeanServer server;

   /* Maximum amount of entries */
   protected int maxEntries = 256;

   /* Holds the entries */
   protected SortedMap entries = new TreeMap();

   /* Initial recording date */
   protected Date recordingStart;

   /* Indicates if the type of the recorded value is double */
   protected boolean isDouble = false;

   /* Statistical values */
   protected double minimumValue, maximumValue, averageValue;

   /* Count of recorded values */
   protected long count = 0;

   protected Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public void start()
   {
      Logger logger = getLogger();
      if (!isActive)
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Starting statistics recorder " + this);
         this.isActive = true;
         recordingStart = new Date();
         entries.clear();
         minimumValue = maximumValue = averageValue = 0;
         count = 0;
         isDouble = false;
         try
         {
            doStart();
         }
         catch (Exception e)
         {
            logger.error("Exception while starting recorder " + this, e);
         }
      }
   }

   public void stop()
   {
      Logger logger = getLogger();
      if (isActive)
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Starting statistics recorder " + this);
         this.isActive = false;
         try
         {
            doStop();
         }
         catch (Exception e)
         {
            logger.error("Exception starting recorder " + this, e);
         }
      }
   }

   public Number getAverage()
   {
      return createValue(averageValue);
   }

   public Number getMin()
   {
      return createValue(minimumValue);
   }

   public Number getMax()
   {
      return createValue(maximumValue);
   }

   public synchronized boolean isActive()
   {
      return isActive;
   }

   public int getMaxEntries()
   {
      return maxEntries;
   }

   public void setMaxEntries(int maxEntries)
   {
      if (maxEntries <= 0)
      {
         throw new IllegalArgumentException("Max entries has to be bigger than 0");
      }
      this.maxEntries = maxEntries;
   }

   public SortedMap getEntries()
   {
      return (SortedMap)((TreeMap)entries).clone();
   }

   public Date getRecordingStart()
   {
      return recordingStart;
   }

   public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
   {
      this.server = server;
      return name;
   }

   public void postRegister(Boolean registrationDone)
   {
   }

   public void preDeregister() throws Exception
   {
      this.stop();
   }

   public void postDeregister()
   {
   }

   /**
    * Subclasses may override this to offer a custom startup procedure
    */
   protected void doStart() throws Exception
   {
   }

   /**
    * Subclasses may override this to offer a custom stop procedure
    */
   protected void doStop() throws Exception
   {
   }

   /**
    * Adds an entry to the collection. It also reduces the size if too big
    * and updates the statics
    */
   protected synchronized void addEntry(Date key, Number value)
   {
      if (isActive)
      {
         entries.put(new PointTime(key, count++), value);
         if (entries.size() > maxEntries)
         {
            while (entries.size() > maxEntries)
            {
               entries.remove(entries.firstKey());
            }
         }
         calculateStats(value);
      }
   }

   /**
    * Updates the statistics
    */
   private void calculateStats(Number value)
   {
      if (!isDouble && (value instanceof Double || value instanceof Float))
      {
         isDouble = true;
      }
      double newValue = value.doubleValue();

      if (count == 1)
      {
         maximumValue = minimumValue = averageValue = newValue;
         return;
      }
      if (newValue > maximumValue)
      {
         maximumValue = newValue;
      }
      if (newValue < minimumValue)
      {
         minimumValue = newValue;
      }
      averageValue = (1 - 1 / (double)count) * averageValue + 1 / (double)count * newValue;
   }

   private Number createValue(double targetValue)
   {
      if (isDouble)
      {
         return new Double(targetValue);
      }
      else
      {
         return new Long((long)targetValue);
      }
   }

}
