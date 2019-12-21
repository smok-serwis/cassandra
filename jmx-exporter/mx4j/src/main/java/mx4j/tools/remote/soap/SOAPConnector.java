/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import mx4j.tools.remote.http.HTTPConnection;
import mx4j.tools.remote.http.HTTPConnectionMBeanServerConnection;
import mx4j.tools.remote.http.HTTPConnector;

/**
 * @version $Revision: 1.20 $
 */
public class SOAPConnector extends HTTPConnector
{
   public SOAPConnector(JMXServiceURL url, Map environment) throws IOException
   {
      super(url, environment);
   }

   protected MBeanServerConnection doGetMBeanServerConnection(Subject delegate) throws IOException
   {
      HTTPConnection catcher = ClientExceptionCatcher.newInstance(getHTTPConnection());
      return new HTTPConnectionMBeanServerConnection(catcher, delegate, getRemoteNotificationClientHandler());
   }
}
