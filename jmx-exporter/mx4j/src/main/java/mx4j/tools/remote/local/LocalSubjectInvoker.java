/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.local;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.util.Map;
import javax.security.auth.Subject;

import mx4j.tools.remote.SubjectInvoker;

/**
 * @version $Revision: 1.4 $
 */
class LocalSubjectInvoker extends SubjectInvoker
{
   static LocalConnection newInstance(LocalConnection target, Subject subject, AccessControlContext context, Map environment)
   {
      LocalSubjectInvoker handler = new LocalSubjectInvoker(target, subject, context, environment);
      return (LocalConnection)Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{LocalConnection.class}, handler);
   }

   private LocalSubjectInvoker(LocalConnection target, Subject subject, AccessControlContext context, Map environment)
   {
      super(target, subject, context, environment);
   }

   protected boolean isPlainInvoke(Method method)
   {
      boolean plain = super.isPlainInvoke(method);
      if (plain) return plain;

      String methodName = method.getName();
      // LocalConnection methods that does not require the delegate subject
      if ("close".equals(methodName)) return true;
      return false;
   }
}
