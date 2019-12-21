/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho.serialization;

import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;

/**
 * @version $Revision: 1.4 $
 */
public class JMXSerializerFactory extends SerializerFactory
{
   protected Serializer getDefaultSerializer(Class cls)
   {
      if (!cls.getName().startsWith("javax.management.")) return super.getDefaultSerializer(cls);
      return new JMXSerializer();
   }

   protected Deserializer getDefaultDeserializer(Class cls)
   {
      if (!cls.getName().startsWith("javax.management.")) return super.getDefaultDeserializer(cls);
      // A bug in the Hessian protocol requires JMX exception to be treated as normal objects
      if (Throwable.class.isAssignableFrom(cls)) return super.getDefaultDeserializer(cls);
      return new JMXDeserializer(cls);
   }
}
