/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.Deserializer;

/**
 * @version $
 */
class JMXDeserializer extends Deserializer
{
   private Class type;

   JMXDeserializer(Class type)
   {
      this.type = type;
   }

   public Class getType()
   {
      return type;
   }

   public Object readMap(AbstractHessianInput in) throws IOException
   {
      try
      {
         byte[] bytes = in.readBytes();
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream(bais);
         Object result = ois.readObject();
         ois.close();
         return result;
      }
      catch (ClassNotFoundException x)
      {
         throw new IOException(x.toString());
      }
   }
}
