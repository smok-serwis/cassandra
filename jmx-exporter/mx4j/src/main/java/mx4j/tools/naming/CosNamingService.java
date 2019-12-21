/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.naming;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import mx4j.log.Log;
import mx4j.log.Logger;

/**
 * An MBean that wraps tnameserv. <p>
 * Calling {@link #start} will start tnameserv in a separate process via
 * {@link java.lang.Runtime#exec(String) Runtime.exec(String command)}.
 *
 * @version $Revision: 1.12 $
 */
public class CosNamingService implements CosNamingServiceMBean
{
   private int m_port;
   private volatile boolean m_running;
   private Process m_process;
   private InputStreamConsumer m_output;
   private InputStreamConsumer m_error;
   private volatile Exception exception;

   /**
    * Creates a new instance of CosNamingService with the default port (900).
    */
   public CosNamingService()
   {
      this(900);
   }

   /**
    * Creates a new instance of CosNamingService with the specified port.
    */
   public CosNamingService(int port)
   {
      m_port = port;
   }

   /**
    * Sets the port on which tnameserv listens for incoming connections.
    *
    * @see #getPort
    */
   public void setPort(int port)
   {
      m_port = port;
   }

   /**
    * Returns the port on which tnameserv listens for incoming connections
    *
    * @see #setPort
    */
   public int getPort()
   {
      return m_port;
   }

   /**
    * Returns whether this MBean has been started and not yet stopped.
    *
    * @see #start
    */
   public boolean isRunning()
   {
      return m_running;
   }

   /**
    * Starts this MBean: tnameserv can now accept incoming calls
    *
    * @see #stop
    * @see #isRunning
    */
   public synchronized void start() throws Exception
   {
      if (isRunning()) return;

      final Logger logger = getLogger();

// We start another thread because Process.waitFor() blocks until the process is destroyed.
      Thread thread = new Thread(new Runnable()
      {
         public void run()
         {
            String home = getJavaHomeBin();

            String command = (home == null ? "" : home) + "tnameserv -ORBInitialPort " + getPort();
            try
            {
               m_process = Runtime.getRuntime().exec(command);
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Process created: " + m_process);
            }
            catch (IOException x)
            {
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Could not create process", x);
               exception = x;
               return;
            }

            m_output = new InputStreamConsumer(m_process.getInputStream());
            m_error = new InputStreamConsumer(m_process.getErrorStream());
            m_output.start();
            m_error.start();

            m_running = true;

            try
            {
// Blocks until the process is destroyed
               int result = m_process.waitFor();
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Exit value is: " + result);

// If we're still running after waitFor() returns, means stop() has not been called
// so the process has returned unexpectedly
               if (isRunning())
               {
                  stop();
                  if (logger.isEnabledFor(Logger.INFO)) logger.info("Unexpected exception (maybe the port " + getPort() + " is already in use)");
               }
            }
            catch (InterruptedException x)
            {
               if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Process has been interrupted", x);
               stop();
            }
         }
      }, "CosNamingService Thread");

      thread.setDaemon(true);
      thread.start();

      while (!m_running && exception == null) wait(10);

      if (exception != null) throw exception;

      if (logger.isEnabledFor(Logger.TRACE)) logger.trace("CosNamingService started");
   }

   private String getJavaHomeBin()
   {
      String home = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
         public Object run()
         {
            return System.getProperty("java.home");
         }
      });
      if (home != null && !home.endsWith(File.separator)) home += File.separator;
      if (home != null) home += "bin" + File.separator;
      return home;
   }

   /**
    * Stops this MBean: tnameserv cannot accept anymore incoming calls
    *
    * @see #start
    */
   public synchronized void stop()
   {
      if (!isRunning()) return;

      m_running = false;
      m_output.interrupt();
      m_error.interrupt();
      m_process.destroy();
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   private class InputStreamConsumer extends Thread
   {
      private final InputStream m_stream;
      private final byte[] m_buffer = new byte[128];

      public InputStreamConsumer(InputStream stream)
      {
         super("Stream Consumer Thread");
         m_stream = new BufferedInputStream(stream);
         setDaemon(true);
      }

      public void run()
      {
         Logger logger = getLogger();
         while (!isInterrupted())
         {
            try
            {
               int read = -1;
               while ((read = m_stream.read(m_buffer)) >= 0)
               {
                  if (logger.isEnabledFor(Logger.INFO)) logger.info(new String(m_buffer, 0, read));
               }
            }
            catch (InterruptedIOException x)
            {
               Thread.currentThread().interrupt();
               break;
            }
            catch (IOException x)
            {
               if (logger.isEnabledFor(Logger.INFO)) logger.info("Error while consuming process stream", x);
               break;
            }
         }
      }
   }
}
