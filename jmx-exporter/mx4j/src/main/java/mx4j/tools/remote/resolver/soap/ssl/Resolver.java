/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.resolver.soap.ssl;

import java.util.Map;

/**
 * @version $Revision: 1.1 $
 */
public class Resolver extends mx4j.tools.remote.resolver.soap.Resolver
{
   protected String getEndpointProtocol(Map environment)
   {
      return "https";
   }
}
