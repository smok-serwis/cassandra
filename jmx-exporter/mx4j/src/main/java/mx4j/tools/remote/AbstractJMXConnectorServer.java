/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.remote.MX4JRemoteUtils;

/**
 * Extends the implementation of JMXConnectorServer by implementing most
 * JMXConnectorServer methods following the JSR 160 specification and delegating
 * implementation specific operations using the template method pattern.
 *
 * @version $Revision: 1.8 $
 * @see ConnectionManager
 */
public abstract class AbstractJMXConnectorServer extends JMXConnectorServer
{
   private JMXServiceURL url;
   private final Map environment;
   private volatile boolean active;
   private volatile boolean stopped;

   public AbstractJMXConnectorServer(JMXServiceURL url, Map environment, MBeanServer server)
   {
      super(server);
      this.url = url;
      this.environment = environment;
   }

   public synchronized JMXServiceURL getAddress()
   {
      return url;
   }

   /**
    * Sets the JMXServiceURL that represent the address of this JMXConnectorServer
    */
   protected synchronized void setAddress(JMXServiceURL url)
   {
      this.url = url;
   }

   public synchronized Map getAttributes()
   {
      Map env = MX4JRemoteUtils.removeNonSerializableEntries(getEnvironment());
      return Collections.unmodifiableMap(env);
   }

   /**
    * Returns the environment Map as is, without removing non-serializable entries like {@link #getAttributes} does.
    */
   protected synchronized Map getEnvironment()
   {
      return environment;
   }

   public boolean isActive()
   {
      return active;
   }

   /**
    * Returns whether the {@link #stop} method of this JMXConnectorServer has been called.
    */
   protected boolean isStopped()
   {
      return stopped;
   }

   public synchronized void start() throws IOException, IllegalStateException
   {
      Logger logger = getLogger();

      if (isActive())
      {
         if (logger.isEnabledFor(Logger.TRACE)) logger.trace("This JMXConnectorServer has already been started");
         return;
      }
      if (isStopped())
      {
         if (logger.isEnabledFor(Logger.TRACE)) logger.trace("This JMXConnectorServer has already been stopped");
         throw new IOException("This RMIConnectorServer has already been stopped");
      }

      doStart();

      active = true;

      if (logger.isEnabledFor(Logger.INFO)) logger.info("JMXConnectorServer started at: " + getAddress());
   }

   /**
    * Template method implemented by subclasses to start this JMXConnectorServer
    */
   protected abstract void doStart() throws IOException, IllegalStateException;

   public synchronized void stop() throws IOException
   {
      if (!isActive() || isStopped()) return;

      stopped = true;
      active = false;

      doStop();

      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.INFO)) logger.info("JMXConnectorServer stopped at: " + getAddress());
   }

   /**
    * Template method implemented by subclasses to stop this JMXConnectorServer
    */
   protected abstract void doStop() throws IOException;

   protected Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public void connectionOpened(String connectionId, String message, Object userData)
   {
      super.connectionOpened(connectionId, message, userData);
   }

   public void connectionClosed(String connectionId, String message, Object userData)
   {
      super.connectionClosed(connectionId, message, userData);
   }

   public void connectionFailed(String connectionId, String message, Object userData)
   {
      super.connectionFailed(connectionId, message, userData);
   }
}
