/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.config;

import java.io.Reader;

/**
 * @version $Revision: 1.3 $
 */
public interface ConfigurationLoaderMBean
{
   public void startup(Reader reader) throws ConfigurationException;

   public void shutdown() throws ConfigurationException;
}
