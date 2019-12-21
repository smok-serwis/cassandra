/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.http;

import java.io.IOException;
import java.util.Map;
import javax.management.remote.JMXServiceURL;

/**
 * An Interface for the Web Container so that we can plug in any web container.
 *
 * @version $Revision: 1.3 $
 */
public interface WebContainer
{
   /**
    * Starts the web container
    */
   public void start(JMXServiceURL url, Map environment) throws IOException;

   /**
    * Stops the web container
    */
   public void stop() throws IOException;

   /**
    * Deploys the given servlet class mapping it to the URL specified by the given JMXServiceURL.
    */
   public void deploy(String servletClassName, JMXServiceURL url, Map environment) throws IOException;

   /**
    * Undeploys the servlet mapped to the URL specified by the given JMXServiceURL.
    */
   public void undeploy(String servletClassName, JMXServiceURL url, Map environment);
}
