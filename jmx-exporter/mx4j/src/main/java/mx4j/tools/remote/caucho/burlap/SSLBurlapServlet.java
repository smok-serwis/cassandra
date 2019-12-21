/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.burlap;

import mx4j.tools.remote.caucho.CauchoService;

/**
 * @version $Revision: 1.1 $
 */
public class SSLBurlapServlet extends BurlapServlet
{
   protected CauchoService createService()
   {
      return new CauchoService("burlap+ssl");
   }
}
