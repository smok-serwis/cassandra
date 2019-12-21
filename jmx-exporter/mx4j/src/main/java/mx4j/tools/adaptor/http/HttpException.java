/*

 * Copyright (C) The MX4J Contributors.

 * All rights reserved.

 *

 * This software is distributed under the terms of the MX4J License version 1.0.

 * See the terms of the MX4J License in the documentation provided with this software.

 */

package mx4j.tools.adaptor.http;


import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * HttpException is emitted when an error parsing an HTTP request appears
 *
 * @version $Revision: 1.3 $
 */

public class HttpException extends IOException

{

   /**
    * Error code
    */

   protected int code;


   /**
    * Constructor for the HttpException object
    *
    * @param code        Error code
    * @param description Description
    */

   public HttpException(int code, String description)

   {

      super(description);

      this.code = code;

   }


   /**
    * Return the exception code
    */

   public int getCode()

   {

      return code;

   }


   public Document getResponseDoc()
   {

      try

      {

         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

         DocumentBuilder builder = factory.newDocumentBuilder();

         Document document = builder.newDocument();


         Element root = document.createElement("HttpException");

         root.setAttribute("code", Integer.toString(code));

         root.setAttribute("description", getMessage());

         document.appendChild(root);

         return document;

      }

      catch (ParserConfigurationException e)

      {

         return null;

      }

   }

}

