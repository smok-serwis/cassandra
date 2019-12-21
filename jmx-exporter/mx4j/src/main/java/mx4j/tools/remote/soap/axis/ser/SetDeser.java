/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.util.HashSet;
import java.util.Set;

import org.apache.axis.Constants;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.5 $
 */
public class SetDeser extends AxisDeserializer
{
   private Set set = new HashSet();

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (Constants.QNAME_LITERAL_ITEM.getLocalPart().equals(hint)) set.add(value);
   }

   protected Object createObject() throws SAXException
   {
      return set;
   }
}
