/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package mx4j.tools.adaptor.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMException;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.relation.RelationServiceMBean;
import javax.management.relation.RoleInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * CreateMBeanCommandProcessor, processes a request for creating and registering
 * an MBean
 *
 * @version $Revision: 1.4 $
 */
public class RelationCommandProcessor extends HttpCommandProcessorAdaptor
{
   private ObjectName m_relationObjectName = null;
   private RelationServiceMBean m_proxy = null;

   public RelationCommandProcessor()
   {
   }

   public Document executeRequest(HttpInputStream in) throws IOException, JMException
   {
      Document document = builder.newDocument();
      Element root = document.createElement("RelationServer");
      document.appendChild(root);

      // check relationService is registered by finding its class if not return a default message
      if (!checkRelationServiceIsRegistered())
      {
         Element defaultElement = document.createElement("default");
         Text defaultNode = document.createTextNode("RelationService is not registered!");
         defaultElement.appendChild(defaultNode);
         root.appendChild(defaultElement);
         return document;
      }
      m_proxy = (RelationServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, m_relationObjectName, RelationServiceMBean.class, true);

      // get all the defined relationTypeNames
      List allRelationNames = m_proxy.getAllRelationTypeNames();
      addRelationTypeNames(root, document, allRelationNames);
      return document;
   }

   /**
    * put everything into a map as we need to maintain the correct relationTypeName to relationId
    * TODO: determine how to make relationService tab useful!!
    */
   protected void addRelationTypeNames(Element node, Document document, List allRelationNames) throws JMException
   {
      Map namesMap = new HashMap();
      Iterator i = allRelationNames.iterator();
      while (i.hasNext())
      {
         String name = (String)i.next();
         List values = m_proxy.findRelationsOfType(name);
         if (namesMap.containsKey(name))
         {

            ((List)namesMap.get(name)).add(values);
         }
         else
         {
            namesMap.put(name, values);
         }
      }
      // loop through the map assigning keys to Elements: values to child elements
      i = namesMap.keySet().iterator();
      while (i.hasNext())
      {
         String relationName = (String)i.next();
         Element typeNameElement = document.createElement("relation-type-name");
         typeNameElement.setAttribute("name", relationName);
         node.appendChild(typeNameElement);

         List infos = m_proxy.getRoleInfos(relationName);
         Iterator x = infos.iterator();
         // build the metadata for the relationTypeName
         while (x.hasNext())
         {
            // get the Relation meta-data for specified RelationTypeName
            RoleInfo roleInfo = (RoleInfo)x.next();
            String roleName = roleInfo.getName();
            String mbeanClassName = roleInfo.getRefMBeanClassName();
            String description = roleInfo.getDescription();
            int minDegree = roleInfo.getMinDegree();
            int maxDegree = roleInfo.getMaxDegree();
            boolean reading = roleInfo.isReadable();
            boolean writing = roleInfo.isWritable();

            // creating the element relation-meta-data
            Element roleInfoElement = document.createElement("relation-meta-data");

            // create the roleName element and text node child of relation-meta-data
            Element roleNameElement = document.createElement("role-name");
            Text roleNameNode = document.createTextNode(roleName);
            roleNameElement.appendChild(roleNameNode);
            roleInfoElement.appendChild(roleNameElement);

            // create the mbeanclass element child of relation-meta-data
            Element mbeanClassElement = document.createElement("mbean-classname");
            Text mbeanClassNode = document.createTextNode(mbeanClassName);
            mbeanClassElement.appendChild(mbeanClassNode);
            roleInfoElement.appendChild(mbeanClassElement);

            // create the description element child of relation-meta-data
            Element descriptionElement = document.createElement("description");
            if (description == null) description = "no description";
            Text descriptionNode = document.createTextNode(description);
            descriptionElement.appendChild(descriptionNode);
            roleInfoElement.appendChild(descriptionElement);

            // create text node for min element
            Element minDegreeElement = document.createElement("min-degree");
            Text minDegreeNode = document.createTextNode(Integer.toString(minDegree));
            minDegreeElement.appendChild(minDegreeNode);
            roleInfoElement.appendChild(minDegreeElement);

            Element maxDegreeElement = document.createElement("max-degree");
            Text maxDegreeNode = document.createTextNode(Integer.toString(maxDegree));
            maxDegreeElement.appendChild(maxDegreeNode);
            roleInfoElement.appendChild(maxDegreeElement);

            Element readingElement = document.createElement("is-readable");
            String readable = null;
            if (reading)
               readable = "true";
            else
               readable = "false";
            Text readingNode = document.createTextNode(readable);
            readingElement.appendChild(readingNode);
            roleInfoElement.appendChild(readingElement);

            Element writingElement = document.createElement("is-writable");
            String writable = null;
            if (writing)
               writable = "true";
            else
               writable = "false";
            Text writingNode = document.createTextNode(writable);
            writingElement.appendChild(writingNode);
            roleInfoElement.appendChild(writingElement);

            typeNameElement.appendChild(roleInfoElement);

         }
         // get the list of relationIds (the values of the Map)
         List references = (List)namesMap.get(relationName);
         Iterator j = references.iterator();
         while (j.hasNext())
         {
            String relationId = (String)j.next();
            Element idElement = document.createElement("relation-id");
            Text idNode = document.createTextNode(relationId);
            idElement.appendChild(idNode);
            typeNameElement.appendChild(idElement);
         }
      }
   }

   // checks the relationService is registered
   protected boolean checkRelationServiceIsRegistered()
   {
      Set allMBeans = server.queryMBeans(null, null);
      for (Iterator i = allMBeans.iterator(); i.hasNext();)
      {
         ObjectInstance instance = (ObjectInstance)i.next();
         if (instance.getClassName().equals("javax.management.relation.RelationService"))
         {
            // create a reference the the RelationService ObjetcName in order to create the proxy
            m_relationObjectName = instance.getObjectName();
            return true;
         }
      }
      return false;
   }
}
