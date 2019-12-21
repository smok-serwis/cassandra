/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.jython;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import mx4j.log.Log;
import mx4j.log.Logger;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * This MBean enables you to run scripts written in jython. Scripts can be run
 * using the managed operation runScript or by listening notifcations from
 * another MBean. To us it you need to install jython 2.1 or higher from
 * &lt;a href="http://www.jython.org"&gt;here&lt/a&gt
 * <p/>
 * If you want to use a jython library remember to add the jython jar to the
 * classpath in the right location or modify the python.path address
 * <p/>
 * The scripts have always the &quot;server&quot; embedded variable which points
 * to the current server. It also automatically import some JMX modules as:
 * <p/>
 * <ul>
 * <li>from javax.management import *
 * <li>from javax.management.loading import *
 * </ul>
 *
 * @version $Revision: 1.9 $
 */
public class JythonRunner implements JythonRunnerMBean, NotificationListener, MBeanRegistration
{
   private MBeanServer server = null;

   private ObjectName targetMBeanName, objectName;

   private String notificationName;

   private boolean useText = true;

   private boolean useCache = false;

   private String scriptText;

   private URL scriptFile;

   private PyCode cache = null;

   private static PythonInterpreter interpreter;

   public void handleNotification(Notification notification, Object handback)
   {
      if (notificationName != null && !notification.getType().equals(notificationName)) return;

      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Notification " + notification + " hit, sending message");
      runScript();
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   /**
    * Executes a given script. If useText is true the text passed will be run as a script
    * otherwise the script will be loaded from the URL an executed
    */
   public void runScript()
   {
      PythonInterpreter interp = getPythonInterpreter();
      interp.set("server", server);
      String script = null;
      if (useText)
      {
         script = scriptText;
      }
      else
      {
         try
         {
            script = loadStream(scriptFile.openStream());
         }
         catch (IOException e)
         {
            Logger log = getLogger();
            log.error("Exception during url opening", e);
         }
      }
      interp.exec(script);
   }

   public static PythonInterpreter getPythonInterpreter()
   {
      if (interpreter == null)
      {
         interpreter = new PythonInterpreter();
         PySystemState sys = Py.getSystemState();
         sys.add_package("javax.management");
         sys.add_package("javax.management.loading");
         sys.add_package("javax.management.modelmbean");
         sys.add_package("javax.management.monitor");
         sys.add_package("javax.management.openmbean");
         sys.add_package("javax.management.remote");
         sys.add_package("javax.management.remote.rmi");
         sys.add_package("javax.management.relation");
         sys.add_package("javax.management.timer");
         try
         {
            String script = loadStream(JythonRunner.class.getClassLoader().getResourceAsStream("mx4j/tools/jython/jmxUtils.py"));
            interpreter.exec(script);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      return interpreter;
   }

   protected static String loadStream(InputStream in) throws IOException
   {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String line = null;
      StringBuffer buffer = new StringBuffer();
      while ((line = reader.readLine()) != null)
      {
         buffer.append(line);
         buffer.append("\n");
      }
      return buffer.toString();
   }

   /**
    * Gets the specific notification type being listened
    */
   public String getNotificationType()
   {
      return notificationName;
   }

   /**
    * Sets the notification being listed. If null any notification will trigger
    * the execution of the script. Otherwise only notifications matching notificationName
    * will trigger it
    */
   public void setNotificationType(String notificationName)
   {
      this.notificationName = notificationName;
   }

   /**
    * Sets the object being observed by this MBean. The MBean will register
    * itself as a listener of targetMBeanName
    */
   public void setObservedObject(ObjectName targetMBeanName)
   {
      this.targetMBeanName = targetMBeanName;
      registerListener();
   }

   /**
    * Gets the object being observed by this MBean
    */
   public ObjectName getObservedObject()
   {
      return targetMBeanName;
   }

   /**
    * Indicates wether to use the script given in the ScripText variable or
    * the one given in the script File.
    */
   public boolean getUseText()
   {
      return this.useText;
   }

   /**
    * Sets the content of the script. If you want to use a file, use ScriptFile
    * instead.
    */
   public void setScript(String text)
   {
      this.scriptText = text;
      this.useText = true;
   }

   /**
    * Returns the script as text.
    */
   public String getScript()
   {
      return this.scriptText;
   }

   /**
    * Returns the URL pointing to the script source
    */
   public URL getScriptURL()
   {
      return scriptFile;
   }

   /**
    * Sets the script source as URL. If the cache script variable is true
    * the file will be loaded only once, otherwise everytime the script is
    * executed
    */
   public void setScriptURL(URL file)
   {
      this.scriptFile = file;
      this.useText = false;
   }

   /**
    * Returns whether the script should be kept in the cache. If true, no further
    * attempts to read the script will be done afterwards. By default is false
    */
   public boolean getCacheScript()
   {
      return useCache;
   }

   /**
    * Sets whether the script should be kept in the cache. If true, no further
    * attempts to read the script will be done afterwards. By default is false
    */
   public void setCacheScript(boolean useCache)
   {
      this.useCache = useCache;
   }

   /**
    * Gathers some basic data
    */
   public ObjectName preRegister(MBeanServer server, ObjectName name)
           throws java.lang.Exception
   {
      this.server = server;
      this.objectName = name;
      return name;
   }


   public void postRegister(Boolean registrationDone)
   {
   }


   public void preDeregister() throws java.lang.Exception
   {
      unregisterListener();
   }


   public void postDeregister()
   {
   }

   protected void registerListener()
   {
      try
      {
         if (targetMBeanName != null && server.isInstanceOf(targetMBeanName, "javax.management.NotificationBroadcaster"))
         {
            server.addNotificationListener(targetMBeanName, this, new MessageFilter(), null);
         }
      }
      catch (InstanceNotFoundException e)
      {
         Logger log = getLogger();
         log.error("Exception during notification registration", e);
      }
   }

   protected void unregisterListener()
   {
      try
      {
         if (targetMBeanName != null && server.isInstanceOf(targetMBeanName, "javax.management.NotificationBroadcaster"))
         {
            server.removeNotificationListener(targetMBeanName, this);
         }
      }
      catch (InstanceNotFoundException e)
      {
         Logger log = getLogger();
         log.error("Exception during notification unregistration", e);
      }
      catch (ListenerNotFoundException e)
      {
      }
   }

   private class MessageFilter implements NotificationFilter
   {
      public boolean isNotificationEnabled(Notification notification)
      {
         return notificationName == null || (notification.getType() != null && notification.getType().equals(notificationName));
      }
   }
}
