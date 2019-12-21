/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.util.Map;
import javax.security.auth.Subject;

import mx4j.tools.remote.SubjectInvoker;

/**
 * @version $Revision: 1.4 $
 */
public class HTTPSubjectInvoker extends SubjectInvoker
{
   public static HTTPConnection newInstance(HTTPConnection target, Subject subject, AccessControlContext context, Map environment)
   {
      HTTPSubjectInvoker handler = new HTTPSubjectInvoker(target, subject, context, environment);
      return (HTTPConnection)Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{HTTPConnection.class}, handler);
   }

   private HTTPSubjectInvoker(HTTPConnection target, Subject subject, AccessControlContext context, Map environment)
   {
      super(target, subject, context, environment);
   }

   protected boolean isPlainInvoke(Method method)
   {
      boolean plain = super.isPlainInvoke(method);
      if (plain) return plain;

      String methodName = method.getName();
      // HTTPConnection methods that does not require the delegate subject
      if ("fetchNotifications".equals(methodName)) return true;
      if ("close".equals(methodName)) return true;
      return false;
   }
}
