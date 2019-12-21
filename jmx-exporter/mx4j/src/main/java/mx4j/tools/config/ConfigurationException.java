/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.config;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @version $Revision: 1.3 $
 */
public class ConfigurationException extends Exception
{
   private Throwable cause;

   public ConfigurationException()
   {
      this(null, null);
   }

   public ConfigurationException(String message)
   {
      this(message, null);
   }

   public ConfigurationException(Throwable cause)
   {
      this(null, cause);
   }

   public ConfigurationException(String message, Throwable cause)
   {
      super(message);
      this.cause = cause;
   }

   public Throwable getCause()
   {
      return cause;
   }

   public void printStackTrace()
   {
      if (cause == null)
      {
         super.printStackTrace();
      }
      else
      {
         synchronized (System.err)
         {
            System.err.println(this);
            cause.printStackTrace();
         }
      }
   }

   public void printStackTrace(PrintStream stream)
   {
      if (cause == null)
      {
         super.printStackTrace(stream);
      }
      else
      {
         synchronized (stream)
         {
            stream.println(this);
            cause.printStackTrace(stream);
         }
      }
   }

   public void printStackTrace(PrintWriter writer)
   {
      if (cause == null)
      {
         super.printStackTrace(writer);
      }
      else
      {
         synchronized (writer)
         {
            writer.println(this);
            cause.printStackTrace(writer);
         }
      }
   }
}
