/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.hessian;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;

import mx4j.tools.remote.caucho.CauchoInput;
import mx4j.tools.remote.caucho.CauchoOutput;
import mx4j.tools.remote.caucho.CauchoService;
import mx4j.tools.remote.caucho.CauchoServlet;

/**
 * @version $Revision: 1.4 $
 */
public class HessianServlet extends CauchoServlet
{
   private CauchoService service;

   public void init() throws ServletException
   {
      super.init();
      service = createService();
   }

   protected CauchoService createService()
   {
      return new CauchoService("hessian");
   }

   public void destroy()
   {
      this.service = null;
   }

   protected Object getService()
   {
      return service;
   }

   protected CauchoInput createCauchoInput(InputStream stream)
   {
      return new HessianCauchoInput(stream);
   }

   protected CauchoOutput createCauchoOutput(OutputStream stream)
   {
      return new HessianCauchoOutput(stream);
   }
}
