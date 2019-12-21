/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap;

import mx4j.tools.remote.http.HTTPService;
import org.apache.axis.MessageContext;

/**
 * The server-side service (the WebService actually) where remote calls end up
 * after being parsed by Axis.
 * This is, in Axis jargon, the 'pivot' of the invocation chain.
 * It is used in the Axis deployment descriptor, and as such must be a public class.
 *
 * @version $Revision: 1.3 $
 */
public class SOAPService extends HTTPService
{
   protected String findRequestURL()
   {
      MessageContext context = MessageContext.getCurrentContext();
      return (String)context.getProperty(MessageContext.TRANS_URL);
   }

   protected String getProtocol()
   {
      return "soap";
   }

   protected String findConnectionId()
   {
      MessageContext context = MessageContext.getCurrentContext();
      return (String)context.getProperty(SOAPConstants.CONNECTION_ID_HEADER_NAME);
   }
}
