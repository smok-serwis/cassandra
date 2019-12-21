/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * GetAttributeCommandProcessor, processes a request for getting one attribute
 * of a specific MBean. It also support some formats for types like Arrays
 *
 * @version $Revision: 1.4 $
 */
public class GetAttributeCommandProcessor extends HttpCommandProcessorAdaptor
{
   public GetAttributeCommandProcessor()
   {
   }

   public Document executeRequest(HttpInputStream in) throws IOException, JMException
   {
      Document document = builder.newDocument();

      String name = in.getVariable("objectname");
      String attributeVariable = in.getVariable("attribute");
      String formatVariable = in.getVariable("format");
      ObjectName objectName = null;
      MBeanAttributeInfo targetAttribute = null;
      // special case
      boolean validMBean = false;
      if (name != null)
      {
         objectName = new ObjectName(name);
         if (server.isRegistered(objectName))
         {
            validMBean = true;
         }
      }
      if (validMBean && attributeVariable != null)
      {
         validMBean = false;
         MBeanInfo info = server.getMBeanInfo(objectName);
         MBeanAttributeInfo[] attributes = info.getAttributes();

         if (attributes != null)
         {
            for (int i = 0; i < attributes.length; i++)
            {
               if (attributes[i].getName().equals(attributeVariable))
               {
                  targetAttribute = attributes[i];
                  validMBean = true;
                  break;
               }
            }
         }
      }
      if (validMBean)
      {
         Element root = document.createElement("MBean");
         document.appendChild(root);

         root.setAttribute("objectname", objectName.toString());
         MBeanInfo info = server.getMBeanInfo(objectName);
         root.setAttribute("classname", info.getClassName());
         root.setAttribute("description", info.getDescription());

         Element attribute = document.createElement("Attribute");
         attribute.setAttribute("name", attributeVariable);
         attribute.setAttribute("classname", targetAttribute.getType());
         Object attributeValue = server.getAttribute(objectName, attributeVariable);
         attribute.setAttribute("isnull", (attributeValue == null) ? "true" : "false");
         root.appendChild(attribute);

         if ("array".equals(formatVariable) && attributeValue.getClass().isArray())
         {
            Element array = document.createElement("Array");
            array.setAttribute("componentclass", attributeValue.getClass().getComponentType().getName());
            int length = Array.getLength(attributeValue);
            array.setAttribute("length", "" + length);
            for (int i = 0; i < length; i++)
            {
               Element arrayElement = document.createElement("Element");
               arrayElement.setAttribute("index", "" + i);
               if (Array.get(attributeValue, i) != null)
               {
                  arrayElement.setAttribute("element", Array.get(attributeValue, i).toString());
                  arrayElement.setAttribute("isnull", "false");
               }
               else
               {
                  arrayElement.setAttribute("element", "null");
                  arrayElement.setAttribute("isnull", "true");
               }
               array.appendChild(arrayElement);
            }
            attribute.appendChild(array);
         }
         else if ("collection".equals(formatVariable) && attributeValue instanceof Collection)
         {
            Collection collection = (Collection)attributeValue;
            Element collectionElement = document.createElement("Collection");
            collectionElement.setAttribute("length", "" + collection.size());
            Iterator i = collection.iterator();
            int j = 0;
            while (i.hasNext())
            {
               Element collectionEntry = document.createElement("Element");
               collectionEntry.setAttribute("index", "" + j++);
               Object obj = i.next();
               if (obj != null)
               {
                  collectionEntry.setAttribute("elementclass", obj.getClass().getName());
                  collectionEntry.setAttribute("element", obj.toString());
               }
               else
               {
                  collectionEntry.setAttribute("elementclass", "null");
                  collectionEntry.setAttribute("element", "null");
               }
               collectionElement.appendChild(collectionEntry);
            }
            attribute.appendChild(collectionElement);
         }
         else if ("map".equals(formatVariable) && attributeValue instanceof Map)
         {
            Map map = (Map)attributeValue;
            Element mapElement = document.createElement("Map");
            mapElement.setAttribute("length", "" + map.size());
            Iterator i = map.keySet().iterator();
            int j = 0;
            while (i.hasNext())
            {
               Element mapEntry = document.createElement("Element");
               mapEntry.setAttribute("index", "" + j++);
               Object key = i.next();
               Object entry = map.get(key);
               if (entry != null && key != null)
               {
                  mapEntry.setAttribute("keyclass", key.getClass().getName());
                  mapEntry.setAttribute("key", key.toString());
                  mapEntry.setAttribute("elementclass", entry.getClass().getName());
                  mapEntry.setAttribute("element", entry.toString());
               }
               else
               {
                  mapEntry.setAttribute("keyclass", "null");
                  mapEntry.setAttribute("key", "null");
                  mapEntry.setAttribute("elementclass", "null");
                  mapEntry.setAttribute("element", "null");
               }
               mapElement.appendChild(mapEntry);
            }
            attribute.appendChild(mapElement);
         }
         else
         {
            attribute.setAttribute("value", (attributeValue == null) ? "null" : attributeValue.toString());
         }

      }
      return document;
   }
}
