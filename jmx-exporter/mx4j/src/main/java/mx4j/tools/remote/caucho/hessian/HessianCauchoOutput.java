/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.hessian;

import java.io.IOException;
import java.io.OutputStream;

import com.caucho.hessian.io.HessianOutput;
import mx4j.tools.remote.caucho.CauchoOutput;
import mx4j.tools.remote.caucho.serialization.JMXSerializerFactory;

/**
 * @version $Revision: 1.3 $
 */
class HessianCauchoOutput implements CauchoOutput
{
   private final OutputStream stream;
   private final HessianOutput output;

   HessianCauchoOutput(OutputStream stream)
   {
      this.stream = stream;
      this.output = new HessianOutput();
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

   /**
    * A bug in the Hessian protocol requires this hack: headers cannot be written using
    * HessianOutput, since the HessianOutput.startCall() already writes the method name,
    * while HessianInput expects the headers before the method name
    *
    * @see #writeMethod
    */
   public void startCall() throws IOException
   {
      stream.write(99);
      stream.write(0);
      stream.write(1);
   }

   public void completeCall() throws IOException
   {
      output.completeCall();
   }

   public void writeHeader(String header) throws IOException
   {
      output.writeHeader(header);
   }

   /**
    * Same hack as {@link #startCall}: this method is missing from HessianOutput
    */
   public void writeMethod(String methodName) throws IOException
   {
      stream.write(109);
      int len = methodName.length();
      stream.write(len >> 8);
      stream.write(len);
      output.printString(methodName, 0, len);
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
