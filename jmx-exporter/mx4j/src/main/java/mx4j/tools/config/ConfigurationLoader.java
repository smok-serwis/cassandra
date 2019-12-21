/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.config;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @version $Revision: 1.3 $
 */
public class ConfigurationLoader implements ConfigurationLoaderMBean, MBeanRegistration
{
   private MBeanServer server;
   private ConfigurationBuilder builder;
   private ConfigurationBuilder.Node root;

   public ConfigurationLoader()
   {
      this(null, new DefaultConfigurationBuilder());
   }

   public ConfigurationLoader(ConfigurationBuilder builder)
   {
      this(null, builder);
   }

   public ConfigurationLoader(MBeanServer server)
   {
      this(server, new DefaultConfigurationBuilder());
   }

   public ConfigurationLoader(MBeanServer server, ConfigurationBuilder builder)
   {
      this.server = server;
      if (builder == null) throw new IllegalArgumentException("ConfigurationBuilder cannot be null");
      this.builder = builder;
   }

   public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
   {
      this.server = server;
      return name;
   }

   public void postRegister(Boolean registered)
   {
   }

   public void preDeregister() throws Exception
   {
   }

   public void postDeregister()
   {
   }

   public void startup(Reader source) throws ConfigurationException
   {
      if (server == null) throw new ConfigurationException("Cannot startup the configuration, MBeanServer is not specified");

      try
      {
         DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
         InputSource src = new InputSource(source);
         Document document = documentBuilder.parse(src);

         Element xmlRoot = document.getDocumentElement();
         root = builder.createConfigurationNode(xmlRoot);
         parse(xmlRoot, root);
         root.configure(server);
      }
      catch (ConfigurationException x)
      {
         throw x;
      }
      catch (Exception x)
      {
         throw new ConfigurationException(x);
      }
   }

   public void shutdown() throws ConfigurationException
   {
      root.configure(null);
   }

   private void parse(Element xmlNode, ConfigurationBuilder.Node node) throws ConfigurationException
   {
      NamedNodeMap attributes = xmlNode.getAttributes();
      if (attributes != null && attributes.getLength() > 0)
      {
         node.setAttributes(attributes);
      }

      List elements = getChildrenElements(xmlNode);
      if (elements != null)
      {
         for (int i = 0; i < elements.size(); ++i)
         {
            Element xmlChild = (Element)elements.get(i);
            ConfigurationBuilder.Node child = builder.createConfigurationNode(xmlChild);
            node.addChild(child);
            parse(xmlChild, child);
         }
      }

      String value = getNodeValue(xmlNode);
      node.setText(value);
   }

   private List getChildrenElements(Element xmlNode)
   {
      NodeList xmlChildren = xmlNode.getChildNodes();
      if (xmlChildren == null) return null;

      ArrayList children = new ArrayList();
      for (int i = 0; i < xmlChildren.getLength(); ++i)
      {
         Node xmlChild = xmlChildren.item(i);
         if (xmlChild.getNodeType() == Node.ELEMENT_NODE) children.add(xmlChild);
      }
      return children;
   }

   private String getNodeValue(Element xmlNode)
   {
      NodeList xmlChildren = xmlNode.getChildNodes();
      if (xmlChildren == null) return null;

      for (int i = 0; i < xmlChildren.getLength(); ++i)
      {
         Node xmlChild = xmlChildren.item(i);
         if (xmlChild.getNodeType() == Node.TEXT_NODE)
         {
            return xmlChild.getNodeValue();
         }
      }
      return null;
   }
}
