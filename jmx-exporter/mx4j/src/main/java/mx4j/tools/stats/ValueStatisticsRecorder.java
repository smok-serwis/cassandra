/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.stats;

import java.util.Date;

/**
 * ValueStatisticsRecorder records statistics of an attribute
 * based on changes on the variable value. If you want to use you have to explictly
 * set the variable value.
 * <p/>
 * Example usage:
 * <pre>
 * ObjectName name = new ObjectName("Domain:name=value");
 * server.createMBean("mx4j.tools.stats.ValueStatisticsRecorder", name, null);
 * server.invoke(name, "start", null, null);
 * // Every time the value is set the statistics are updated
 * server.setAttribute(name, "Attribute", new Double(10));
 * </pre>
 *
 * @version $Revision: 1.5 $
 */
public class ValueStatisticsRecorder extends AbstractStatisticsRecorder implements ValueStatisticsRecorderMBean
{
   protected Number value = null;

   public void setValue(Number value)
   {
      this.value = value;
      addEntry(new Date(), value);
   }

   public Number getValue()
   {
      return value;
   }

   public String toString()
   {
      return "ValueStatisticsRecorder";
   }

}
