/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import mx4j.log.Log;
import mx4j.log.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @version $Revision: 1.8 $
 */
public class DefaultConfigurationBuilder implements ConfigurationBuilder
{
   public static final String SHUTDOWN_COMMAND = "shutdown";
   public static final String RESTART_COMMAND = "restart";

   private static final String NULL = "null";

   public Node createConfigurationNode(Element node) throws ConfigurationException
   {
      String loweredName = node.getNodeName().toLowerCase();
      StringBuffer buffer = new StringBuffer(loweredName);
      buffer.replace(0, 1, loweredName.substring(0, 1).toUpperCase());
      String className = getClass().getName() + "$" + buffer.toString();
      try
      {
         Logger logger = getLogger();
         if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Creating configuration node " + className);
         return (ConfigurationBuilder.Node)getClass().getClassLoader().loadClass(className).newInstance();
      }
      catch (Exception x)
      {
         throw new ConfigurationException(x);
      }
   }

   private static Logger getLogger()
   {
      return Log.getLogger(DefaultConfigurationBuilder.class.getName());
   }

   public abstract static class AbstractNode implements Node
   {
      private String text;
      private Node parent;
      private List children;

      public void setText(String text)
      {
         this.text = text;
      }

      public void setParent(Node parent)
      {
         this.parent = parent;
      }

      public void addChild(Node child)
      {
         if (children == null) children = new ArrayList();
         child.setParent(this);
         children.add(child);
      }

      protected String getText()
      {
         return text;
      }

      public Node getParent()
      {
         return parent;
      }

      public List getChildren()
      {
         return children;
      }

      public void setAttributes(NamedNodeMap attributes) throws ConfigurationException
      {
         Logger logger = getLogger();
         for (int i = 0; i < attributes.getLength(); ++i)
         {
            org.w3c.dom.Node attribute = attributes.item(i);
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();
            String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            try
            {
               if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Calling " + setterName + " with " + value + " on " + this);
               Method setter = getClass().getMethod(setterName, new Class[]{String.class});
               setter.invoke(this, new java.lang.Object[]{value});
            }
            catch (InvocationTargetException x)
            {
               throw new ConfigurationException(x.getTargetException());
            }
            catch (Exception x)
            {
               throw new ConfigurationException(x);
            }
         }
      }
   }

   public static class Configuration extends AbstractNode implements ObjectsHolder, Runnable
   {
      private Map objects;
      private int port = -1;
      private MBeanServer server;
      private Thread thread;

      public void setPort(String portString)
      {
         this.port = Integer.parseInt(portString);
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         if (server != null)
         {
            this.server = server;
            return startup(server);
         }
         else
         {
            return shutdown(this.server);
         }
      }

      private java.lang.Object startup(MBeanServer server) throws ConfigurationException
      {
         Logger logger = getLogger();
         List children = getChildren();
         if (children != null)
         {
            for (int i = 0; i < children.size(); ++i)
            {
               Node child = (Node)children.get(i);
               if (child instanceof DefaultConfigurationBuilder.Startup) child.configure(server);
            }
         }
         if (port > 0)
         {
            thread = new Thread(this, "Configuration Shutdown");
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Starting " + thread.getName() + " Thread on port " + port);
            thread.start();
         }
         return null;
      }

      private java.lang.Object shutdown(MBeanServer server) throws ConfigurationException
      {
         Logger logger = getLogger();
         List children = getChildren();
         if (children != null)
         {
            for (int i = 0; i < children.size(); ++i)
            {
               Node child = (Node)children.get(i);
               if (child instanceof DefaultConfigurationBuilder.Shutdown) child.configure(server);
            }
         }
         if (port > 0)
         {
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Stopping " + thread.getName() + " Thread on port " + port);
            thread.interrupt();
         }
         return null;
      }

      public void run()
      {
         Logger logger = getLogger();
         ServerSocket server = null;
         try
         {
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Started " + thread.getName() + " Thread on port " + port);

            server = new ServerSocket(port, 50, InetAddress.getByName(null));
            server.setSoTimeout(1000);

            byte[] buffer = new byte[64];
            StringBuffer command = new StringBuffer();
            while (!thread.isInterrupted())
            {
               Socket client = null;
               try
               {
                  client = server.accept();
               }
               catch (InterruptedIOException x)
               {
                  continue;
               }
               if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Client connected " + client);
               InputStream is = new BufferedInputStream(client.getInputStream());

               command.setLength(0);
               int read = -1;
               while ((read = is.read(buffer)) >= 0) command.append(new String(buffer, 0, read));

               String cmd = command.toString();
               if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Got command '" + cmd + "'");

               if (SHUTDOWN_COMMAND.equals(cmd))
               {
                  try
                  {
                     configure(null);
                     break;
                  }
                  catch (ConfigurationException x)
                  {
                     if (logger.isEnabledFor(Logger.WARN)) logger.warn("Bad configuration for shutdown", x);
                  }
               }
            }
         }
         catch (Exception x)
         {
            if (logger.isEnabledFor(Logger.INFO)) logger.info("Caught Exception in " + thread.getName() + " Thread, exiting", x);
         }
         finally
         {
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Stopped " + thread.getName() + " Thread on port " + port);
            try
            {
               if (server != null) server.close();
            }
            catch (IOException x)
            {
            }
         }
      }

      public java.lang.Object getObject(String key)
      {
         if (objects == null) return null;
         return objects.get(key);
      }

      public java.lang.Object putObject(String key, java.lang.Object value)
      {
         if (objects == null) objects = new HashMap();
         return objects.put(key, value);
      }

      public boolean containsKey(String key)
      {
         if (objects == null) return false;
         return objects.containsKey(key);
      }
   }

   public static class Startup extends AbstractNode
   {
      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         List children = getChildren();
         if (children != null)
         {
            for (int i = 0; i < children.size(); ++i)
            {
               Node child = (Node)children.get(i);
               child.configure(server);
            }
         }
         return null;
      }
   }

   public static class Shutdown extends AbstractNode
   {
      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         List children = getChildren();
         if (children != null)
         {
            for (int i = 0; i < children.size(); ++i)
            {
               Node child = (Node)children.get(i);
               child.configure(server);
            }
         }
         return null;
      }
   }

   public static class Object extends AbstractNode
   {
      private String id;

      public void setObjectid(String id)
      {
         this.id = id;
      }

      public String getObjectid()
      {
         return id;
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         List children = getChildren();
         java.lang.Object result = null;
         if (children != null && children.size() > 0)
         {
            Node child = (Node)children.get(0);
            result = child.configure(server);
         }
         putObject(this, id, result);
         return result;
      }
   }

   public static class New extends AbstractNode
   {
      private String classname;

      public void setClassname(String classname)
      {
         this.classname = classname;
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         try
         {
            Class cls = loadClass(classname);
            Constructor ctor = cls.getConstructor(getMethodSignature(this));
            return ctor.newInstance(getMethodArguments(this, server));
         }
         catch (InvocationTargetException x)
         {
            throw new ConfigurationException(x.getTargetException());
         }
         catch (ConfigurationException x)
         {
            throw x;
         }
         catch (Exception x)
         {
            throw new ConfigurationException(x);
         }
      }
   }

   public static class Arg extends AbstractNode
   {
      private static final String OBJECT_TYPE = "object";
      private static final String STRING_TYPE = "string";
      private static final String BOOLEAN_TYPE = "boolean";
      private static final String BYTE_TYPE = "byte";
      private static final String CHAR_TYPE = "char";
      private static final String DOUBLE_TYPE = "double";
      private static final String FLOAT_TYPE = "float";
      private static final String INT_TYPE = "int";
      private static final String LONG_TYPE = "long";
      private static final String SHORT_TYPE = "short";

      private String type;
      private String refobjectid;

      public void setType(String type)
      {
         this.type = type;
      }

      public void setRefobjectid(String refobjectid)
      {
         this.refobjectid = refobjectid;
      }

      public Class getJavaType() throws ConfigurationException
      {
         if (STRING_TYPE.equalsIgnoreCase(type)) return String.class;
         if (OBJECT_TYPE.equalsIgnoreCase(type)) return java.lang.Object.class;
         if (BOOLEAN_TYPE.equalsIgnoreCase(type)) return boolean.class;
         if (BYTE_TYPE.equalsIgnoreCase(type)) return byte.class;
         if (CHAR_TYPE.equalsIgnoreCase(type)) return char.class;
         if (DOUBLE_TYPE.equalsIgnoreCase(type)) return double.class;
         if (FLOAT_TYPE.equalsIgnoreCase(type)) return float.class;
         if (INT_TYPE.equalsIgnoreCase(type)) return int.class;
         if (LONG_TYPE.equalsIgnoreCase(type)) return long.class;
         if (SHORT_TYPE.equalsIgnoreCase(type)) return short.class;
         return loadClass(type);
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         if (refobjectid != null) return getObject(this, refobjectid);

         List children = getChildren();
         if (children != null && children.size() > 0)
         {
            Node child = (Node)children.get(0);
            return child.configure(server);
         }

         String text = getText();
         if (text == null || NULL.equals(text)) return null;

         if (STRING_TYPE.equalsIgnoreCase(type)) return text;
         if (OBJECT_TYPE.equalsIgnoreCase(type)) return text;
         if (BOOLEAN_TYPE.equalsIgnoreCase(type)) return Boolean.valueOf(text);
         if (BYTE_TYPE.equalsIgnoreCase(type)) return Byte.valueOf(text);
         if (CHAR_TYPE.equalsIgnoreCase(type)) return new Character(text.length() < 1 ? 0 : text.charAt(0));
         if (DOUBLE_TYPE.equalsIgnoreCase(type)) return Double.valueOf(text);
         if (FLOAT_TYPE.equalsIgnoreCase(type)) return Float.valueOf(text);
         if (INT_TYPE.equalsIgnoreCase(type)) return Integer.valueOf(text);
         if (LONG_TYPE.equalsIgnoreCase(type)) return Long.valueOf(text);
         if (SHORT_TYPE.equalsIgnoreCase(type)) return Short.valueOf(text);

         try
         {
            Constructor ctor = getJavaType().getConstructor(new Class[]{String.class});
            return ctor.newInstance(new java.lang.Object[]{text});
         }
         catch (InvocationTargetException x)
         {
            throw new ConfigurationException(x.getTargetException());
         }
         catch (ConfigurationException x)
         {
            throw x;
         }
         catch (Exception x)
         {
            throw new ConfigurationException(x);
         }
      }
   }

   public static class Register extends AbstractNode
   {
      private ObjectName objectname;

      public void setObjectname(String name) throws MalformedObjectNameException
      {
         if (name != null && !NULL.equals(name)) this.objectname = ObjectName.getInstance(name);
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         List children = getChildren();
         if (children != null && children.size() > 0)
         {
            Node child = (Node)children.get(0);
            try
            {
               return server.registerMBean(child.configure(server), objectname);
            }
            catch (ConfigurationException x)
            {
               throw x;
            }
            catch (Exception x)
            {
               throw new ConfigurationException(x);
            }
         }
         return null;
      }
   }

   public static class Unregister extends AbstractNode
   {
      private ObjectName objectname;

      public void setObjectname(String name) throws MalformedObjectNameException
      {
         this.objectname = ObjectName.getInstance(name);
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         try
         {
            server.unregisterMBean(objectname);
            return null;
         }
         catch (Exception x)
         {
            throw new ConfigurationException(x);
         }
      }
   }

   public static class Create extends AbstractNode
   {
      private String classname;
      private ObjectName objectname;
      private String loadername;

      public void setClassname(String classname)
      {
         this.classname = classname;
      }

      public void setObjectname(String name) throws MalformedObjectNameException
      {
         if (name != null && !NULL.equals(name)) this.objectname = ObjectName.getInstance(name);
      }

      public void setLoadername(String name) throws MalformedObjectNameException
      {
         this.loadername = name;
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         try
         {
            if (loadername != null)
            {
               ObjectName loader = null;
               if (!NULL.equals(loadername)) loader = ObjectName.getInstance(loadername);
               return server.createMBean(classname, objectname, loader, getMethodArguments(this, server), getJMXMethodSignature(this));
            }
            else
            {
               return server.createMBean(classname, objectname, getMethodArguments(this, server), getJMXMethodSignature(this));
            }
         }
         catch (ConfigurationException x)
         {
            throw x;
         }
         catch (Exception x)
         {
            throw new ConfigurationException(x);
         }
      }
   }

   public static class Call extends AbstractNode
   {
      private String classname;
      private ObjectName objectname;
      private String refobjectid;
      private String method;
      private String operation;
      private String attribute;

      public void setClassname(String classname)
      {
         this.classname = classname;
      }

      public void setObjectname(String name) throws MalformedObjectNameException
      {
         if (name != null && !NULL.equals(name)) this.objectname = ObjectName.getInstance(name);
      }

      public void setRefobjectid(String refid)
      {
         this.refobjectid = refid;
      }

      public void setMethod(String method)
      {
         this.method = method;
      }

      public void setOperation(String operation)
      {
         this.operation = operation;
      }

      public void setAttribute(String attribute)
      {
         this.attribute = attribute;
      }

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException
      {
         if (classname != null)
         {
            // Static call
            Class cls = loadClass(classname);
            try
            {
               Method mthd = cls.getMethod(method, getMethodSignature(this));
               return mthd.invoke(null, getMethodArguments(this, server));
            }
            catch (InvocationTargetException x)
            {
               throw new ConfigurationException(x.getTargetException());
            }
            catch (ConfigurationException x)
            {
               throw x;
            }
            catch (Exception x)
            {
               throw new ConfigurationException(x);
            }
         }
         else
         {
            if (objectname != null)
            {
               // JMX call
               if (operation != null)
               {
                  try
                  {
                     return server.invoke(objectname, operation, getMethodArguments(this, server), getJMXMethodSignature(this));
                  }
                  catch (ConfigurationException x)
                  {
                     throw x;
                  }
                  catch (Exception x)
                  {
                     throw new ConfigurationException(x);
                  }
               }
               else if (attribute != null)
               {
                  try
                  {
                     List children = getChildren();
                     if (children == null || children.size() < 1)
                     {
                        return server.getAttribute(objectname, attribute);
                     }
                     else
                     {
                        java.lang.Object arg = getMethodArguments(this, server)[0];
                        server.setAttribute(objectname, new Attribute(attribute, arg));
                        return null;
                     }
                  }
                  catch (ConfigurationException x)
                  {
                     throw x;
                  }
                  catch (Exception x)
                  {
                     throw new ConfigurationException(x);
                  }
               }
               else
               {
                  throw new ConfigurationException("Missing 'attribute' or 'operation' attribute in JMX call");
               }
            }
            else
            {
               // Standard call
               java.lang.Object target = null;
               if (refobjectid != null)
               {
                  target = getObject(this, refobjectid);
                  if (target == null) throw new ConfigurationException("Could not find object with id " + refobjectid);
                  try
                  {
                     Method mthd = target.getClass().getMethod(method, getMethodSignature(this));
                     return mthd.invoke(target, getMethodArguments(this, server));
                  }
                  catch (InvocationTargetException x)
                  {
                     throw new ConfigurationException(x.getTargetException());
                  }
                  catch (ConfigurationException x)
                  {
                     throw x;
                  }
                  catch (Exception x)
                  {
                     throw new ConfigurationException(x);
                  }
               }
               else
               {
                  throw new ConfigurationException("Missing 'refobjectid' attribute in call element");
               }
            }
         }
      }
   }

   private static Class[] getMethodSignature(Node node) throws ConfigurationException
   {
      List children = node.getChildren();
      if (children == null) return null;
      ArrayList signature = new ArrayList();
      for (int i = 0; i < children.size(); ++i)
      {
         Node child = (Node)children.get(i);
         if (child instanceof Arg)
         {
            Arg arg = (Arg)child;
            signature.add(arg.getJavaType());
         }
      }
      return (Class[])signature.toArray(new Class[signature.size()]);
   }

   private static String[] getJMXMethodSignature(Node node) throws ConfigurationException
   {
      Class[] signature = getMethodSignature(node);
      if (signature == null) return null;

      ArrayList jmxSignature = new ArrayList();
      for (int i = 0; i < signature.length; ++i)
      {
         jmxSignature.add(signature[i].getName());
      }
      return (String[])jmxSignature.toArray(new String[jmxSignature.size()]);
   }

   private static java.lang.Object[] getMethodArguments(Node node, MBeanServer server) throws ConfigurationException
   {
      List children = node.getChildren();
      if (children == null) return null;
      ArrayList arguments = new ArrayList();
      for (int i = 0; i < children.size(); ++i)
      {
         Node child = (Node)children.get(i);
         if (child instanceof Arg)
         {
            Arg arg = (Arg)child;
            arguments.add(arg.configure(server));
         }
      }
      return arguments.toArray();
   }

   private static Class loadClass(String className) throws ConfigurationException
   {
      try
      {
         return Thread.currentThread().getContextClassLoader().loadClass(className);
      }
      catch (ClassNotFoundException x)
      {
         throw new ConfigurationException(x);
      }
   }

   private static java.lang.Object getObject(Node node, String key)
   {
      while (node != null)
      {
         if (node instanceof ObjectsHolder)
         {
            ObjectsHolder holder = (ObjectsHolder)node;
            if (holder.containsKey(key)) return holder.getObject(key);
         }
         node = node.getParent();
      }
      return null;
   }

   private static void putObject(Node node, String key, java.lang.Object value)
   {
      while (node != null)
      {
         if (node instanceof ObjectsHolder)
         {
            ObjectsHolder holder = (ObjectsHolder)node;
            holder.putObject(key, value);
         }
         node = node.getParent();
      }
   }
}
