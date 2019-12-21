/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.naming;

/**
 * Management interface for the CosNamingService MBean.
 *
 * @version $Revision: 1.4 $
 */
public interface CosNamingServiceMBean
{
   /**
    * Sets the port on which tnameserv listens for incoming connections.
    *
    * @see #getPort
    */
   public void setPort(int port);

   /**
    * Returns the port on which tnameserv listens for incoming connections
    *
    * @see #setPort
    */
   public int getPort();

   /**
    * Returns whether this MBean has been started and not yet stopped.
    *
    * @see #start
    */
   public boolean isRunning();

   /**
    * Starts this MBean: tnameserv can now accept incoming calls
    *
    * @see #stop
    * @see #isRunning
    */
   public void start() throws Exception;

   /**
    * Stops this MBean: tnameserv cannot accept anymore incoming calls
    *
    * @see #start
    */
   public void stop();
}
