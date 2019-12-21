/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.proxy;

import javax.management.JMRuntimeException;

/**
 * @version $Revision: 1.3 $
 */
public class RemoteMBeanProxyException extends JMRuntimeException
{
   private final Exception exception;

   public RemoteMBeanProxyException()
   {
      this(null, null);
   }

   public RemoteMBeanProxyException(String message)
   {
      this(message, null);
   }

   public RemoteMBeanProxyException(Exception exception)
   {
      this(null, exception);
   }

   public RemoteMBeanProxyException(String message, Exception exception)
   {
      super(message);
      this.exception = exception;
   }

   public Throwable getCause()
   {
      return exception;
   }
}
