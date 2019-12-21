/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.burlap;

import java.io.IOException;
import java.io.OutputStream;

import com.caucho.burlap.io.BurlapOutput;
import mx4j.tools.remote.caucho.CauchoOutput;
import mx4j.tools.remote.caucho.serialization.JMXSerializerFactory;

/**
 * @version $Revision: 1.3 $
 */
class BurlapCauchoOutput implements CauchoOutput
{
   private final BurlapOutput output;

   BurlapCauchoOutput(OutputStream stream)
   {
      this.output = new BurlapOutput();
      output.setSerializerFactory(new JMXSerializerFactory());
      output.init(stream);
   }

   public void startReply() throws IOException
   {
      output.startReply();
   }

   public void completeReply() throws IOException
   {
      output.completeReply();
   }

   public void startCall() throws IOException
   {
      output.startCall();
   }

   public void completeCall() throws IOException
   {
      output.completeCall();
   }

   public void writeHeader(String header) throws IOException
   {
      output.writeHeader(header);
   }

   public void writeMethod(String methodName) throws IOException
   {
      output.writeMethod(methodName);
   }

   public void writeObject(Object object) throws IOException
   {
      output.writeObject(object);
   }

   public void writeFault(Throwable fault) throws IOException
   {
      output.writeFault(fault.getClass().getName(), fault.getMessage(), fault);
   }
}
