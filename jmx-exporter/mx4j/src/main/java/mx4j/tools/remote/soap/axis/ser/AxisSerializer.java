/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;

/**
 * @version $Revision: 1.4 $
 */
public abstract class AxisSerializer implements Serializer
{
   protected static final String SCHEMA_COMPLEX_TYPE = "complexType";
   protected static final String SCHEMA_ALL = "all";
   protected static final String SCHEMA_ELEMENT = "element";
   protected static final String SCHEMA_SEQUENCE = "sequence";

   public Element writeSchema(Class javaType, Types types) throws Exception
   {
      return null;
   }

   public String getMechanismType()
   {
      return Constants.AXIS_SAX;
   }
}
