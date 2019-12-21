/*

 * Copyright (C) The MX4J Contributors.

 * All rights reserved.

 *

 * This software is distributed under the terms of the MX4J License version 1.0.

 * See the terms of the MX4J License in the documentation provided with this software.

 */

package mx4j.tools.adaptor.http;


import java.io.IOException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;


/**
 * HttpCommandProcessor sets the structure of a command processor
 *
 * @version $Revision: 1.3 $
 */

public interface HttpCommandProcessor

{


   /**
    * Executes an HTTP request. It assumes the request is well formed
    *
    * @param out Input request
    * @return An XML Document
    * @throws IOException
    */

   public Document executeRequest(HttpInputStream in) throws IOException, JMException;


   /**
    * Sets the target MBeanServer
    */

   public void setMBeanServer(MBeanServer server);


   /**
    * Sets the Document Builder factory
    */

   public void setDocumentBuilder(DocumentBuilder builder);

}

