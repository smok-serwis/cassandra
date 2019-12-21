/*
 *  Copyright (C) The MX4J Contributors.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * Management interface descriptions for the HttpAdaptor MBean.
 *
 * @version $Revision: 1.3 $
 */
public class HttpAdaptorMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "HttpAdaptor MBean";
   }

   public String getConstructorDescription(Constructor ctor)
   {
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor()"))
      {
         return "Parameterless constructor";
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(int)"))
      {
         return "Constructor with a given port";
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(java.lang.String)"))
      {
         return "Constructor with a given host";
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(int,java.lang.String)"))
      {
         return "Constructor with a given port and host";
      }
      return super.getConstructorDescription(ctor);
   }

   public String getConstructorParameterName(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(int)"))
      {
         switch (index)
         {
            case 0:
               return "port";
         }
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(java.lang.String)"))
      {
         switch (index)
         {
            case 0:
               return "host";
         }
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(int,java.lang.String)"))
      {
         switch (index)
         {
            case 0:
               return "port";
            case 1:
               return "host";
         }
      }
      return super.getConstructorParameterName(ctor, index);
   }

   public String getConstructorParameterDescription(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(int)"))
      {
         switch (index)
         {
            case 0:
               return "Listening port";
         }
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(java.lang.String)"))
      {
         switch (index)
         {
            case 0:
               return "Listening host";
         }
      }
      if (ctor.toString().equals("public mx4j.tools.adaptor.http.HttpAdaptor(int,java.lang.String)"))
      {
         switch (index)
         {
            case 0:
               return "Listening port";
            case 1:
               return "Listening host";
         }
      }
      return super.getConstructorParameterDescription(ctor, index);
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("AuthenticationMethod"))
      {
         return "Authentication method (none/basic/digest)";
      }
      if (attribute.equals("ProcessorClass"))
      {
         return "PostProcessor MBean";
      }
      if (attribute.equals("ProcessorNameString"))
      {
         return "PostProcessor MBean's object name as string";
      }
      if (attribute.equals("Processor"))
      {
         return "PostProcessor MBean";
      }
      if (attribute.equals("ProcessorName"))
      {
         return "PostProcessor MBean's object name";
      }
      if (attribute.equals("SocketFactory"))
      {
         return "Server Socket factory";
      }
      if (attribute.equals("SocketFactoryName"))
      {
         return "Server Socket factory's objectname";
      }
      if (attribute.equals("SocketFactoryNameString"))
      {
         return "Server Socket factory's objectname as string";
      }
      if (attribute.equals("Active"))
      {
         return "Indicates whether the server is active";
      }
      if (attribute.equals("StartDate"))
      {
         return "Indicates the date when the server was started";
      }
      if (attribute.equals("RequestsCount"))
      {
         return "Total of requested served so far";
      }
      if (attribute.equals("Version"))
      {
         return "HttpAdaptor's version";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("addCommandProcessor"))
      {
         return "Adds a command processor object assigned to a given path";
      }
      if (name.equals("addCommandProcessor"))
      {
         return "Adds a command processor object (given a classname) assigned to a given path";
      }
      if (name.equals("removeCommandProcessor"))
      {
         return "Removes a command processor for a given path";
      }
      if (name.equals("start"))
      {
         return "Starts the HttpAdaptor";
      }
      if (name.equals("stop"))
      {
         return "Stops the HttpAdaptor";
      }
      if (name.equals("addAuthorization"))
      {
         return "Adds an authorized pair name/password";
      }
      return super.getOperationDescription(operation);
   }

   public String getOperationParameterName(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("addCommandProcessor"))
      {
         switch (index)
         {
            case 0:
               return "path";
            case 1:
               return "processor";
         }
      }
      if (name.equals("addCommandProcessor"))
      {
         switch (index)
         {
            case 0:
               return "path";
            case 1:
               return "processorClass";
         }
      }
      if (name.equals("removeCommandProcessor"))
      {
         switch (index)
         {
            case 0:
               return "path";
         }
      }
      if (name.equals("addAuthorization"))
      {
         switch (index)
         {
            case 0:
               return "username";
            case 1:
               return "password";
         }
      }
      return super.getOperationParameterName(method, index);
   }

   public String getOperationParameterDescription(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("addCommandProcessor"))
      {
         switch (index)
         {
            case 0:
               return "Path assigned to the new command processor";
            case 1:
               return "HttpCommandProcessor object";
         }
      }
      if (name.equals("addCommandProcessor"))
      {
         switch (index)
         {
            case 0:
               return "Path assigned to the new command processor";
            case 1:
               return "HttpCommandProcessor classname to be instantiated and assigned to the give path";
         }
      }
      if (name.equals("removeCommandProcessor"))
      {
         switch (index)
         {
            case 0:
               return "Path to be removed";
         }
      }
      if (name.equals("addAuthorization"))
      {
         switch (index)
         {
            case 0:
               return "Username";
            case 1:
               return "Password";
         }
      }
      return super.getOperationParameterDescription(method, index);
   }
}
