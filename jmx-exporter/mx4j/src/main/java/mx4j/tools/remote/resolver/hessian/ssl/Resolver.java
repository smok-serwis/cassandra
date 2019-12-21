/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.resolver.hessian.ssl;

import java.util.Map;

import mx4j.tools.remote.caucho.hessian.SSLHessianServlet;

/**
 * @version $Revision: 1.1 $
 */
public class Resolver extends mx4j.tools.remote.resolver.hessian.Resolver
{
   protected String getEndpointProtocol(Map environment)
   {
      return "https";
   }

   protected String getServletClassName()
   {
      return SSLHessianServlet.class.getName();
   }
}
