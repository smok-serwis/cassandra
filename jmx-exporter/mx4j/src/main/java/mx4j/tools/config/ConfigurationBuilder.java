/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.config;

import java.util.List;
import javax.management.MBeanServer;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @version $Revision: 1.3 $
 */
public interface ConfigurationBuilder
{
   public static interface Node
   {
      public void setAttributes(NamedNodeMap attributes) throws ConfigurationException;

      public void setText(String text);

      public java.lang.Object configure(MBeanServer server) throws ConfigurationException;

      public Node getParent();

      public void setParent(Node parent);

      public List getChildren();

      public void addChild(Node child);
   }

   public static interface ObjectsHolder
   {
      public Object getObject(String key);

      public Object putObject(String key, Object value);

      public boolean containsKey(String key);
   }

   public Node createConfigurationNode(Element node) throws ConfigurationException;
}
