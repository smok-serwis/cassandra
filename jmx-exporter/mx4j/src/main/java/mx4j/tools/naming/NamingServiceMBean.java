/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.naming;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Management interface for the NamingService MBean.
 *
 * @version $Revision: 1.7 $
 */
public interface NamingServiceMBean
{
   /**
    * Sets the port on which rmiregistry listens for incoming connections.
    * Can be called only if this service is not {@link #isRunning() running}.
    *
    * @see #getPort
    */
   public void setPort(int port);

   /**
    * Returns the port on which rmiregistry listens for incoming connections
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
    * Starts this MBean: rmiregistry can now accept incoming calls
    *
    * @see #stop
    * @see #isRunning
    */
   public void start() throws RemoteException;

   /**
    * Stops this MBean: rmiregistry cannot accept anymore incoming calls
    *
    * @see #start
    */
   public void stop() throws NoSuchObjectException;

   /**
    * Returns an array of the names bound in the rmiregistry
    *
    * @see java.rmi.registry.Registry#list()
    */
   public String[] list() throws RemoteException;

   /**
    * Removes the binding for the specified <code>name</code> in the rmiregistry
    *
    * @see java.rmi.registry.Registry#unbind(String)
    */
   public void unbind(String name) throws RemoteException, NotBoundException;
}
