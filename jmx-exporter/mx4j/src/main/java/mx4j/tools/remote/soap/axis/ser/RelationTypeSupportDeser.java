/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.util.ArrayList;
import java.util.List;
import javax.management.relation.RelationTypeSupport;
import javax.management.relation.RoleInfo;

import org.apache.axis.Constants;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.3 $
 */
public class RelationTypeSupportDeser extends AxisDeserializer
{
   String relationName;
   List roleInfos = new ArrayList();

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (RelationTypeSupportSer.NAME.equals(hint)) relationName = (String)value;
      if (Constants.QNAME_LITERAL_ITEM.getLocalPart().equals(hint)) roleInfos.add(value);
   }

   protected Object createObject() throws SAXException
   {
      try
      {
         RoleInfo[] infAry = new RoleInfo[roleInfos.size()];
         roleInfos.toArray(infAry);
         return new RelationTypeSupport(relationName, infAry);
      }
      catch (Exception e)
      {
         throw new SAXException(e);
      }
   }

}
