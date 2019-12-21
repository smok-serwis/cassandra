/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.ssl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * @version $Revision: 1.3 $
 */
public class SSLAdaptorServerSocketFactoryMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "Factory for SSLServerSockets used by adaptors";
   }

   public String getConstructorDescription(Constructor ctor)
   {
      return "Creates a new SSLServerSocket factory for adaptors";
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("KeyStoreType"))
      {
         return "The type of the keystore, default is 'JKS'";
      }
      if (attribute.equals("TrustStoreType"))
      {
         return "The type of the truststore, default is 'JKS'";
      }
      if (attribute.equals("KeyStoreName"))
      {
         return "The keystore name";
      }
      if (attribute.equals("TrustStoreName"))
      {
         return "The truststore name";
      }
      if (attribute.equals("KeyStorePassword"))
      {
         return "The keystore password";
      }
      if (attribute.equals("TrustStorePassword"))
      {
         return "The truststore password";
      }
      if (attribute.equals("KeyManagerAlgorithm"))
      {
         return "The key algorithm, default is 'SunX509'";
      }
      if (attribute.equals("TrustManagerAlgorithm"))
      {
         return "The trust algorithm, default is 'SunX509'";
      }
      if (attribute.equals("KeyManagerPassword"))
      {
         return "The key password";
      }
      if (attribute.equals("SSLProtocol"))
      {
         return "The SSL protocol version, default is 'TLS'";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("createServerSocket"))
      {
         return "Creates a new SSLServerSocket";
      }
      return super.getOperationDescription(operation);
   }

   public String getOperationParameterName(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("createServerSocket"))
      {
         switch (index)
         {
            case 0:
               return "port";
            case 1:
               return "backlog";
            case 2:
               return "host";
         }
      }
      return super.getOperationParameterName(method, index);
   }

   public String getOperationParameterDescription(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("createServerSocket"))
      {
         switch (index)
         {
            case 0:
               return "The port on which the SSLServerSocket listens for incoming connections";
            case 1:
               return "The backlog for this SSLServerSocket";
            case 2:
               return "The host name or IP address on which the SSLServerSocket is opened";
         }
      }
      return super.getOperationParameterDescription(method, index);
   }
}
