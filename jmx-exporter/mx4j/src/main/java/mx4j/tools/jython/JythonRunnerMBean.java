/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.jython;

import java.net.URL;
import javax.management.ObjectName;

/**
 * Management interface for the JythonRunner MBean.
 *
 * @version $Revision: 1.6 $
 */
public interface JythonRunnerMBean
{
   /**
    * Executes a given script.
    * If useText is true the text passed will be run as a script otherwise
    * the script will be loaded from the URL an executed
    */
   public void runScript();

   /**
    * Gets the specific notification type being listened
    */
   public String getNotificationType();

   /**
    * Sets the notification type being listened.
    * If null any notification will trigger the execution of the script.
    * Otherwise only notifications matching notificationName will trigger it
    */
   public void setNotificationType(String notificationName);

   /**
    * Sets the object being observed by this MBean.
    * The MBean will register itself as a listener of targetMBeanName
    */
   public void setObservedObject(ObjectName targetMBeanName);

   /**
    * Gets the object being observed by this MBean
    */
   public ObjectName getObservedObject();

   /**
    * Indicates wether to use the script given in the ScripText variable or the one given in the script File.
    */
   public boolean getUseText();

   /**
    * Sets the content of the script. If you want to use a file, use ScriptFile instead.
    */
   public void setScript(String text);

   /**
    * Returns the script as text.
    */
   public String getScript();

   /**
    * Returns the URL pointing to the script source
    */
   public URL getScriptURL();

   /**
    * Sets the script source as URL. If the cache script variable is true the file will be loaded only once, otherwise everytime the script is executed
    */
   public void setScriptURL(URL file);

   /**
    * Returns whether the script should be kept in the cache.
    * If true, no further attempts to read the script will be done afterwards. By default is false
    */
   public boolean getCacheScript();

   /**
    * Sets whether the script should be kept in the cache. If true, no further attempts to read the script will be done afterwards. By default is false
    */
   public void setCacheScript(boolean useCache);
}
