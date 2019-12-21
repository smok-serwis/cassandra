/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;

/**
 * This class is used in the Axis deployment descriptor, and parses the SOAP header
 * (on server-side) that contains the connection ID sent by the client.
 *
 * @version $Revision: 1.5 $
 */
public class ConnectionIDRequestHandler extends BasicHandler
{
   public void invoke(MessageContext context) throws AxisFault
   {
      Message message = context.getRequestMessage();
      SOAPEnvelope envelope = message.getSOAPEnvelope();
      SOAPHeaderElement header = envelope.getHeaderByName(SOAPConstants.NAMESPACE_URI, SOAPConstants.CONNECTION_ID_HEADER_NAME);
      if (header == null) throw new AxisFault("Could not find mandatory header " + SOAPConstants.CONNECTION_ID_HEADER_NAME);

      try
      {
         String id = (String)header.getValueAsType(XMLType.XSD_STRING);
         if (id != null && id.length() > 0) context.setProperty(SOAPConstants.CONNECTION_ID_HEADER_NAME, id);
      }
      catch (Exception x)
      {
         throw AxisFault.makeFault(x);
      }
      finally
      {
         header.setProcessed(true);
      }
   }
}
