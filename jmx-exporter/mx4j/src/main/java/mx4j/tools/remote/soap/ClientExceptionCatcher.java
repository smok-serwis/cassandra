/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.management.MBeanException;
import javax.management.RuntimeMBeanException;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.tools.remote.http.HTTPConnection;
import org.apache.axis.AxisFault;

/**
 * Handles exceptions thrown on server-side by rethrowing them on client side.
 * Axis, as of version 1.1, is not able to do this transparently, so we have to
 * manually do it here, by parsing the class name and message that are encoded
 * by Axis in the response.
 *
 * @version $Revision: 1.5 $
 */
class ClientExceptionCatcher implements InvocationHandler
{
   public static HTTPConnection newInstance(HTTPConnection target)
   {
      ClientExceptionCatcher handler = new ClientExceptionCatcher(target);
      return (HTTPConnection)Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{HTTPConnection.class}, handler);
   }

   private final HTTPConnection target;

   private ClientExceptionCatcher(HTTPConnection target)
   {
      this.target = target;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      try
      {
         try
         {
            return method.invoke(target, args);
         }
         catch (InvocationTargetException x)
         {
            throw x.getTargetException();
         }
      }
      catch (Throwable x)
      {
         throw handleException(x, method.getExceptionTypes());
      }
   }

   private Throwable handleException(Throwable x, Class[] declared)
   {
      if (x instanceof Error) return x;
      if (x instanceof AxisFault) x = extractThrowable((AxisFault)x);
      if (isDeclaredOrRuntime(x, declared)) return x;
      return new IOException(x.toString());
   }

   private Throwable extractThrowable(AxisFault fault)
   {
      String name = fault.getFaultString();
      if (name == null) return fault;

      // FaultStrings in Axis 1.1 are obtained with Throwable.toString(),
      // which is <exception class>: <message>.
      // Here we parse the string to re-create the exception
      int colon = name.indexOf(':');
      String className = colon < 0 ? name : name.substring(0, colon).trim();
      String message = colon < 0 ? null : name.substring(colon + 1).trim();

      Class cls = null;
      try
      {
         // Try to load the class: mostly these are JMX exceptions or java.* exceptions
         // so we can use this class' classloader
         cls = getClass().getClassLoader().loadClass(className);
      }
      catch (ClassNotFoundException x)
      {
         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Cannot load Throwable class " + className, x);
      }

      if (cls == null) return fault;

      Object exception = null;
      if (message != null)
      {
         try
         {
            // Try to find the suitable constructor
            Constructor ctor = cls.getConstructor(new Class[]{String.class});
            exception = ctor.newInstance(new Object[]{message});
         }
         catch (Throwable x)
         {
            Logger logger = getLogger();
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Cannot find constructor " + className + "(String message)", x);
         }
      }

      if (exception == null)
      {
         try
         {
            exception = cls.newInstance();
         }
         catch (Throwable x)
         {
            Logger logger = getLogger();
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Cannot find constructor " + className + "()", x);
         }
      }

      // Handle JMX exceptions with special case constructors
      if (MBeanException.class.getName().equals(className))
      {
         exception = new MBeanException(null, message);
      }
      else if (RuntimeMBeanException.class.getName().equals(className))
      {
         exception = new RuntimeMBeanException(null, message);
      }

      if (!(exception instanceof Throwable))
      {
         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Could not recreate exception thrown on server side: " + className);
         return fault;
      }

      return (Throwable)exception;
   }

   private boolean isDeclaredOrRuntime(Throwable x, Class[] declared)
   {
      if (x instanceof RuntimeException) return true;

      for (int i = 0; i < declared.length; ++i)
      {
         Class exception = declared[i];
         if (exception.isInstance(x)) return true;
      }
      return false;
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }
}
