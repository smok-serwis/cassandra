/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.resolver.burlap;

import java.io.IOException;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import mx4j.tools.remote.caucho.burlap.BurlapClientInvoker;
import mx4j.tools.remote.caucho.burlap.BurlapServlet;
import mx4j.tools.remote.http.HTTPResolver;

/**
 * @version $Revision: 1.1 $
 */
public class Resolver extends HTTPResolver
{
   public Object lookupClient(JMXServiceURL url, Map environment) throws IOException
   {
      String endpoint = getEndpoint(url, environment);
      return new BurlapClientInvoker(endpoint);
   }

   protected String getServletClassName()
   {
      return BurlapServlet.class.getName();
   }
}
