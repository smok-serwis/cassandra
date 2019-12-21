/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLConnection;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.tools.remote.http.HTTPClientInvoker;
import mx4j.tools.remote.http.HTTPConnection;

/**
 * @version $Revision: 1.4 $
 */
public abstract class CauchoClientInvoker extends HTTPClientInvoker
{
   private final String endpoint;
   private final HTTPConnection service;

   public CauchoClientInvoker(String endpoint)
   {
      this.endpoint = endpoint;
      CauchoServiceProxy proxy = new CauchoServiceProxy();
      service = (HTTPConnection)Proxy.newProxyInstance(proxy.getClass().getClassLoader(), new Class[]{HTTPConnection.class}, proxy);
   }

   protected HTTPConnection getService()
   {
      return service;
   }

   protected abstract CauchoInput createCauchoInput(InputStream stream);

   protected abstract CauchoOutput createCauchoOutput(OutputStream stream);

   private class CauchoServiceProxy implements InvocationHandler
   {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         URLConnection connection = new URL(endpoint).openConnection();
         connection.setDoInput(true);
         connection.setDoOutput(true);
         connection.setUseCaches(false);
         OutputStream os = new BufferedOutputStream(connection.getOutputStream());
         try
         {
            CauchoOutput output = createCauchoOutput(os);
            startCall(output);
            writeHeaders(output);
            writeMethod(output, method);
            writeArguments(output, args);
            completeCall(output);
            os.flush();

            InputStream is = new BufferedInputStream(connection.getInputStream());
            try
            {
               CauchoInput input = createCauchoInput(is);
               input.startReply();
               Object result = input.readObject(null);
               input.completeReply();
               return result;
            }
            catch (Throwable x)
            {
               Logger logger = Log.getLogger(CauchoClientInvoker.class.getName());
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("", x);
               throw x;
            }
            finally
            {
               is.close();
            }
         }
         finally
         {
            os.close();
         }
      }

      private void startCall(CauchoOutput output) throws IOException
      {
         output.startCall();
      }

      private void writeHeaders(CauchoOutput output) throws IOException
      {
         output.writeHeader(CauchoService.CONNECTION_ID_HEADER_NAME);
         output.writeObject(getConnectionId());
      }

      private void writeMethod(CauchoOutput output, Method method) throws IOException
      {
         String methodName = mangleMethodName(method);
         output.writeMethod(methodName);
      }

      private String mangleMethodName(Method method)
      {
         return CauchoService.mangleMethodName(method);
      }

      private void writeArguments(CauchoOutput output, Object[] args) throws IOException
      {
         if (args != null) for (int i = 0; i < args.length; ++i) output.writeObject(args[i]);
      }

      private void completeCall(CauchoOutput output) throws IOException
      {
         output.completeCall();
      }
   }
}
