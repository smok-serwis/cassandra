/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.burlap;

import java.io.InputStream;
import java.io.OutputStream;

import mx4j.tools.remote.caucho.CauchoClientInvoker;
import mx4j.tools.remote.caucho.CauchoInput;
import mx4j.tools.remote.caucho.CauchoOutput;

/**
 * @version $Revision: 1.3 $
 */
public class BurlapClientInvoker extends CauchoClientInvoker
{
   public BurlapClientInvoker(String endpoint)
   {
      super(endpoint);
   }

   protected CauchoInput createCauchoInput(InputStream stream)
   {
      return new BurlapCauchoInput(stream);
   }

   protected CauchoOutput createCauchoOutput(OutputStream stream)
   {
      return new BurlapCauchoOutput(stream);
   }
}
