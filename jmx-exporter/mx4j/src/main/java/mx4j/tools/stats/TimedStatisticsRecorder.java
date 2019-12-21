/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimedStatisticsRecorder records statistics of an attribute
 * with a timer polling the value every certain interval
 *
 * @version $Revision: 1.4 $
 */
public class TimedStatisticsRecorder extends ObserverStatisticsRecorder implements TimedStatisticsRecorderMBean
{
   protected boolean registered = false;

   protected static Timer timer = new Timer();

   protected CollectTask task = new CollectTask();

   protected long granularity = 1000L;

   public TimedStatisticsRecorder()
   {
   }

   public void setGranularity(long granularity)
   {
      this.granularity = granularity;
   }

   public long getGranularity()
   {
      return granularity;
   }

   public String toString()
   {
      return "TimedStatisticsRecorder";
   }

   protected synchronized void startObserving() throws Exception
   {
      task = new CollectTask();
      timer.schedule(task, 0, granularity);
   }

   protected synchronized void stopObserving() throws Exception
   {
      task.cancel();
   }

   private class CollectTask extends TimerTask
   {
      public void run()
      {
         try
         {
            Number value = (Number)server.getAttribute(observedName, observedAttribute);
            addEntry(new Date(), value);
         }
         catch (Exception e)
         {
            getLogger().error(new StringBuffer(" Exception reading attribute ").append(observedAttribute).append(" of MBean ").append(observedName).toString(), e);
         }
      }
   }

}
