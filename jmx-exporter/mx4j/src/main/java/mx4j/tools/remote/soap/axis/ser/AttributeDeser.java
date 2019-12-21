/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.management.Attribute;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.7 $
 */
public class AttributeDeser extends AxisDeserializer
{
   private String attributeName;
   private Object attributeValue;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (AttributeSer.NAME.equals(hint))
         attributeName = (String)value;
      else if (AttributeSer.VALUE.equals(hint))
         attributeValue = value;
   }

   protected Object createObject() throws SAXException
   {
      return new Attribute(attributeName, attributeValue);
   }
}
