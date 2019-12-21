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
 * Class PointTime encapsulates the time and order of a value. We want to
 * index the recorded time but in the event of a repeated time, it will
 * have another index which should be unique. The class is Comparable
 * and the order is given first by the date and if those are equals by
 * the index
 *
 * @version $Revision: 1.3 $
 */
public class PointTime implements Comparable
{
   private Date date;
   private long index;

   public PointTime(Date date, long index)
   {
      this.date = date;
      this.index = index;
   }

   public Date getDate()
   {
      return date;
   }

   public long getIndex()
   {
      return index;
   }

   public int compareTo(Object o)
   {
      PointTime p = (PointTime)o;
      if (date.equals(p.date))
      {
         return (int)(index - p.index);
      }
      else
      {
         return date.compareTo(p.date);
      }
   }

   public boolean equals(Object o)
   {
      if (o == null)
      {
         throw new NullPointerException();
      }
      if (!(o instanceof PointTime))
      {
         return false;
      }
      PointTime p = (PointTime)o;
      return p.date.equals(date) && (p.index == index);
   }
}
