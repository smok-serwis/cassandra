/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.BaseDeserializerFactory;

/**
 * @version $Revision: 1.6 $
 */
public class ObjectNameDeserFactory extends BaseDeserializerFactory
{
   public ObjectNameDeserFactory(Class javaType, QName xmlType)
   {
      super(ObjectNameDeser.class, xmlType, javaType);
   }
}
