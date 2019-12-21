/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.burlap;

import java.io.IOException;
import java.io.InputStream;

import com.caucho.burlap.io.BurlapInput;
import mx4j.tools.remote.caucho.CauchoInput;
import mx4j.tools.remote.caucho.serialization.JMXSerializerFactory;

/**
 * @version $Revision: 1.4 $
 */
class BurlapCauchoInput implements CauchoInput
{
   private final BurlapInput input;

   BurlapCauchoInput(InputStream stream)
   {
      this.input = new BurlapInput();
      input.setSerializerFactory(new JMXSerializerFactory());
      input.init(stream);
   }

   public void startCall() throws IOException
   {
      input.readCall();
   }

   public void completeCall() throws IOException
   {
      input.completeCall();
   }

   public void startReply() throws Exception
   {
      try
      {
         input.startReply();
      } catch (Throwable x)
      {
         if (x instanceof Exception) throw (Exception) x;
         throw (Error) x;
      }
   }

   public void completeReply() throws IOException
   {
      input.completeReply();
   }

   public String readHeader() throws IOException
   {
      return input.readHeader();
   }

   public String readMethod() throws IOException
   {
      return input.readMethod();
   }

   public Object readObject(Class cls) throws IOException
   {
      if (cls == null || cls == Object.class) return input.readObject();
      return input.readObject(cls);
   }
}
