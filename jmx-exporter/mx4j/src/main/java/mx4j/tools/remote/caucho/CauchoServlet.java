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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx4j.tools.remote.http.HTTPConnection;

/**
 * @version $Revision: 1.4 $
 */
public abstract class CauchoServlet extends HttpServlet
{
   private Map methods;

   public void init() throws ServletException
   {
      methods = new HashMap();
      mapMethods(HTTPConnection.class, methods);
   }

   protected void mapMethods(Class cls, Map methods)
   {
      Method[] mthds = cls.getMethods();
      for (int i = 0; i < mthds.length; ++i)
      {
         Method mthd = mthds[i];
         String key = mangleMethodName(mthd);
         methods.put(key, mthd);
      }
   }

   protected Method findMethod(String methodName)
   {
      return (Method) methods.get(methodName);
   }

   protected String mangleMethodName(Method method)
   {
      return CauchoService.mangleMethodName(method);
   }

   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      if (!"POST".equalsIgnoreCase(request.getMethod())) throw new ServletException("Caucho protocol requires POST");

      BufferedInputStream is = new BufferedInputStream(request.getInputStream(), 48);
      CauchoInput input = createCauchoInput(is);
      BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream(), 48);
      CauchoOutput output = createCauchoOutput(bos);

      invoke(request, input, output);

      bos.flush();
   }

   protected abstract CauchoInput createCauchoInput(InputStream stream);

   protected abstract CauchoOutput createCauchoOutput(OutputStream stream);

   protected abstract Object getService();

   protected void invoke(HttpServletRequest request, CauchoInput input, CauchoOutput output) throws IOException
   {
      input.startCall();
      Map headers = readHeaders(input);
      String methodName = input.readMethod();
      Method method = findMethod(methodName);
      if (method == null)
      {
         output.startReply();
         NoSuchMethodException x = new NoSuchMethodException(methodName);
         output.writeFault(x);
         output.completeReply();
      } else
      {
         Object[] values = readArguments(input, method);
         input.completeCall();

         Object result = null;
         try
         {
            result = invoke(request.getRequestURL().toString(), getService(), method, headers, values);
         } catch (Throwable x)
         {
            output.startReply();
            output.writeFault(x);
            output.completeReply();
            return;
         }
         output.startReply();
         output.writeObject(result);
         output.completeReply();
      }
   }

   protected Map readHeaders(CauchoInput input) throws IOException
   {
      Map headers = new HashMap();
      String header = null;
      while ((header = input.readHeader()) != null) headers.put(header, input.readObject(null));
      return headers;
   }

   protected Object[] readArguments(CauchoInput input, Method method) throws IOException
   {
      Class[] types = method.getParameterTypes();
      Object[] values = new Object[types.length];
      for (int i = 0; i < types.length; ++i) values[i] = input.readObject(types[i]);
      return values;
   }

   protected Object invoke(String url, Object target, Method method, Map headers, Object[] values) throws Exception
   {
      if (target == null) throw new IOException("Service is not available");
      String connectionId = (String) headers.get(CauchoService.CONNECTION_ID_HEADER_NAME);
      CauchoService.setConnectionContext(url, connectionId);
      try
      {
         return method.invoke(target, values);
      } catch (InvocationTargetException x)
      {
         Throwable t = x.getTargetException();
         if (t instanceof Exception) throw (Exception) t;
         throw (Error) t;
      } finally
      {
         CauchoService.resetConnectionContext();
      }
   }
}
