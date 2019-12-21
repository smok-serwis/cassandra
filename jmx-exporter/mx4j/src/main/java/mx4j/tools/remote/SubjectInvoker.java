/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import javax.management.remote.JMXServerErrorException;
import javax.security.auth.Subject;

import mx4j.remote.MX4JRemoteUtils;

/**
 * @version $Revision: 1.4 $
 */
public abstract class SubjectInvoker implements InvocationHandler
{
   private final Object target;
   private final Subject subject;
   private final AccessControlContext context;
   private Map environment;

   protected SubjectInvoker(Object target, Subject subject, AccessControlContext context, Map environment)
   {
      this.target = target;
      this.subject = subject;
      this.context = context;
      this.environment = environment;
   }

   protected boolean isPlainInvoke(Method method)
   {
      String methodName = method.getName();
      // java.lang.Object methods
      if ("toString".equals(methodName)) return true;
      if ("hashCode".equals(methodName)) return true;
      if ("equals".equals(methodName)) return true;
      return false;
   }

   protected Object handleSpecialInvoke(Object target, Method method, Object[] args) throws Exception
   {
      throw new NoSuchMethodException(method.toString());
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      if (isPlainInvoke(method)) return chain(target, method, args);
      if (method.getParameterTypes()[args.length - 1] == Subject.class)
      {
         Subject delegate = (Subject)args[args.length - 1];
         return subjectInvoke(target, method, args, delegate);
      }
      else
      {
         return handleSpecialInvoke(target, method, args);
      }
   }

   protected Object subjectInvoke(final Object proxy, final Method method, final Object[] args, Subject delegate) throws Exception
   {
      return MX4JRemoteUtils.subjectInvoke(subject, delegate, context, environment, new PrivilegedExceptionAction()
      {
         public Object run() throws Exception
         {
            return chain(proxy, method, args);
         }
      });
   }

   protected Object chain(Object proxy, Method method, Object[] args) throws Exception
   {
      try
      {
         return method.invoke(proxy, args);
      }
      catch (InvocationTargetException x)
      {
         Throwable t = x.getTargetException();
         if (t instanceof Exception) throw (Exception)t;
         throw new JMXServerErrorException("Error thrown during invocation", (Error)t);
      }
   }
}
