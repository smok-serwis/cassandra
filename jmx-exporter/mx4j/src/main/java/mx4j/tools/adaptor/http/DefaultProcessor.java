/*
 *  Copyright (C) The MX4J Contributors.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

import mx4j.log.Log;
import mx4j.log.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DefaultPostProcessor doesn't alter the result, just publising the xml file
 *
 * @version $Revision: 1.4 $
 */
public class DefaultProcessor implements ProcessorMBean
{
   private final static String ENCODING = "UTF-8";

   private boolean canonical = false;

   public String getName()
   {
      return "Default XML Processor";
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public void writeResponse(HttpOutputStream out, HttpInputStream in, Document document)
           throws IOException
   {
      out.setCode(HttpConstants.STATUS_OKAY);
      out.setHeader("Content-Type", "text/xml");
      out.sendHeaders();

      print(new PrintWriter(out), document);

      ByteArrayOutputStream o = new ByteArrayOutputStream();

      print(new PrintWriter(o), document);

      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.DEBUG)) logger.debug(new String(o.toByteArray()));
   }

   public void writeError(HttpOutputStream out, HttpInputStream in, Exception e)
           throws IOException
   {
      if (e instanceof HttpException)
      {
         out.setCode(((HttpException)e).getCode());
         out.setHeader("Content-Type", "text/xml");
         out.sendHeaders();
         print(new PrintWriter(out), ((HttpException)e).getResponseDoc());
      }
   }

   public String preProcess(String path)
   {
      // The only special case. The root is routed to the the server request
      if (path.equals("/"))
      {
         path = "/server";
      }
      return path;
   }

   public String notFoundElement(String path, HttpOutputStream out, HttpInputStream in)
           throws IOException, HttpException
   {
      // no processing. Unknown elements are not found
      throw new HttpException(HttpConstants.STATUS_NOT_FOUND, "Path " + path + " not found");
   }

   // ripped from Xerces samples
   protected void print(PrintWriter out, Node node)
   {
      // is there anything to do?
      if (node == null) return;

      int type = node.getNodeType();
      switch (type)
      {
         // print document
         case Node.DOCUMENT_NODE:
            {
               if (!canonical)
               {
                  out.println("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");
               }

               NodeList children = node.getChildNodes();
               for (int iChild = 0; iChild < children.getLength(); iChild++)
               {
                  print(out, children.item(iChild));
               }
               out.flush();
               break;
            }

            // print element with attributes
         case Node.ELEMENT_NODE:
            {
               out.print('<');
               out.print(node.getNodeName());

               Attr attrs[] = sortAttributes(node.getAttributes());
               for (int i = 0; i < attrs.length; i++)
               {
                  Attr attr = attrs[i];
                  out.print(' ');
                  out.print(attr.getNodeName());
                  out.print("=\"");
                  out.print(normalize(attr.getNodeValue()));
                  out.print('"');
               }
               out.print('>');

               NodeList children = node.getChildNodes();
               if (children != null)
               {
                  int len = children.getLength();
                  for (int i = 0; i < len; i++)
                  {
                     print(out, children.item(i));
                  }
               }
               break;
            }

            // handle entity reference nodes
         case Node.ENTITY_REFERENCE_NODE:
            {
               if (canonical)
               {
                  NodeList children = node.getChildNodes();
                  if (children != null)
                  {
                     int len = children.getLength();
                     for (int i = 0; i < len; i++)
                     {
                        print(out, children.item(i));
                     }
                  }
               }
               else
               {
                  out.print('&');
                  out.print(node.getNodeName());
                  out.print(';');
               }
               break;
            }

            // print cdata sections
         case Node.CDATA_SECTION_NODE:
            {
               if (canonical)
               {
                  out.print(normalize(node.getNodeValue()));
               }
               else
               {
                  out.print("<![CDATA[");
                  out.print(node.getNodeValue());
                  out.print("]]>");
               }
               break;
            }

            // print text
         case Node.TEXT_NODE:
            {
               out.print(normalize(node.getNodeValue()));
               break;
            }

            // print processing instruction
         case Node.PROCESSING_INSTRUCTION_NODE:
            {
               out.print("<?");
               out.print(node.getNodeName());
               String data = node.getNodeValue();
               if (data != null && data.length() > 0)
               {
                  out.print(' ');
                  out.print(data);
               }
               out.println("?>");
               break;
            }
      }

      // Close the element
      if (type == Node.ELEMENT_NODE)
      {
         out.print("</");
         out.print(node.getNodeName());
         out.print('>');
      }

      out.flush();
   }

   /**
    * Returns a sorted list of attributes.
    *
    * @param attrs Description of Parameter
    * @return Description of the Returned Value
    */
   protected Attr[] sortAttributes(NamedNodeMap attrs)
   {
      int len = (attrs != null) ? attrs.getLength() : 0;
      Attr array[] = new Attr[len];
      for (int i = 0; i < len; ++i) array[i] = (Attr)attrs.item(i);

      Arrays.sort(array, new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            Attr attr1 = (Attr)o1;
            Attr attr2 = (Attr)o2;
            return attr1.getNodeName().compareTo(attr2.getNodeName());
         }
      });
      return array;
   }

   /**
    * Normalizes the given string.
    *
    * @param s Description of Parameter
    * @return Description of the Returned Value
    */
   protected String normalize(String s)
   {
      StringBuffer str = new StringBuffer();

      int len = (s != null) ? s.length() : 0;
      for (int i = 0; i < len; i++)
      {
         char ch = s.charAt(i);
         switch (ch)
         {
            case '<':
               {
                  str.append("&lt;");
                  break;
               }

            case '>':
               {
                  str.append("&gt;");
                  break;
               }

            case '&':
               {
                  str.append("&amp;");
                  break;
               }

            case '"':
               {
                  str.append("&quot;");
                  break;
               }

            case '\'':
               {
                  str.append("&apos;");
                  break;
               }

            case '\r':
            case '\n':
               {
                  if (canonical)
                  {
                     str.append("&#");
                     str.append(Integer.toString(ch));
                     str.append(';');
                  }
                  else
                  {
                     str.append(ch);
                  }
                  break;
               }

            default:
               {
                  str.append(ch);
               }
         }
      }

      return str.toString();
   }
}
