/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho;

import java.lang.reflect.Method;

import mx4j.tools.remote.http.HTTPService;

/**
 * @version $
 */
public class CauchoService extends HTTPService
{
   static final String CONNECTION_ID_HEADER_NAME = "connectionContext";

   private static ThreadLocal connectionContext = new ThreadLocal();

   private final String protocol;

   public CauchoService(String protocol)
   {
      this.protocol = protocol;
   }

   protected String getProtocol()
   {
      return protocol;
   }

   protected String findRequestURL()
   {
      ConnectionContext context = (ConnectionContext)connectionContext.get();
      return context == null ? null : context.url;
   }

   protected String findConnectionId()
   {
      ConnectionContext context = (ConnectionContext)connectionContext.get();
      return context == null ? null : context.connectionId;
   }

   static void setConnectionContext(String url, String connectionId)
   {
      connectionContext.set(new ConnectionContext(url, connectionId));
   }

   static void resetConnectionContext()
   {
      connectionContext.set(null);
   }

   static String mangleMethodName(Method method)
   {
      return method.getName() + "__" + method.getParameterTypes().length;
   }

   private static class ConnectionContext
   {
      private String url;
      private String connectionId;

      private ConnectionContext(String url, String connectionId)
      {
         this.url = url;
         this.connectionId = connectionId;
      }
   }
}
