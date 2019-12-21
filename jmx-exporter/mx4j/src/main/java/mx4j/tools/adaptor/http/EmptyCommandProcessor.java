/*
 *  Copyright (C) The MX4J Contributors.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */
package mx4j.tools.adaptor.http;

import java.io.IOException;
import javax.management.JMException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * EmptyCommandProcessor, returns an empty xml tree
 *
 * @version $Revision: 1.3 $
 */
public class EmptyCommandProcessor extends HttpCommandProcessorAdaptor
{

   /**
    * Constructs a new EmptyCommandProcessor
    */
   public EmptyCommandProcessor()
   {
   }


   public Document executeRequest(HttpInputStream in)
           throws IOException, JMException
   {
      Document document = builder.newDocument();

      Element root = document.createElement("empty");
      document.appendChild(root);

      return document;
   }

}

