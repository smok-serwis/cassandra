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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * An MBean that wraps rmiregistry. <p>
 * Calling {@link #start} will launch rmiregistry in the same JVM; this way
 * rmiregistry will have in its classpath the same classes the JVM has.
 *
 * @version $Revision: 1.11 $
 */
public class NamingService implements NamingServiceMBean
{
   private int m_port;
   private Registry m_registry;
   private boolean m_running;

   /**
    * Creates a new instance of NamingService with the default rmiregistry port (1099).
    */
   public NamingService()
   {
      this(Registry.REGISTRY_PORT);
   }

   /**
    * Creates a new instance of NamingService with the specified port.
    */
   public NamingService(int port)
   {
      setPort(port);
   }

   public void setPort(int port)
   {
      if (isRunning()) throw new IllegalStateException("NamingService is running, cannot change the port");
      m_port = port;
   }

   public int getPort()
   {
      return m_port;
   }

   public boolean isRunning()
   {
      return m_running;
   }

   public void start() throws RemoteException
   {
      if (!isRunning())
      {
         m_registry = LocateRegistry.createRegistry(getPort());
         m_running = true;
      }
   }

   public void stop() throws NoSuchObjectException
   {
      if (isRunning())
      {
         m_running = !UnicastRemoteObject.unexportObject(m_registry, true);
      }
   }

   public String[] list() throws RemoteException
   {
      if (!isRunning()) throw new IllegalStateException("NamingService is not running");
      return m_registry.list();
   }

   public void unbind(String name) throws RemoteException, NotBoundException
   {
      if (!isRunning()) throw new IllegalStateException("NamingService is not running");
      m_registry.unbind(name);
   }
}
