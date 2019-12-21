/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.util.Set;
import javax.security.auth.Subject;

import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.4 $
 */
public class SubjectDeser extends AxisDeserializer
{
   private boolean readOnly;
   private Set principals;
   private Set publicCredentials;
   private Set privateCredentials;

   public void onSetChildValue(Object value, Object hint) throws SAXException
   {
      if (SubjectSer.READ_ONLY.equals(hint))
         readOnly = ((Boolean)value).booleanValue();
      else if (SubjectSer.PRINCIPALS.equals(hint))
         principals = (Set)value;
      else if (SubjectSer.PUBLIC_CREDENTIALS.equals(hint))
         publicCredentials = (Set)value;
      else if (SubjectSer.PRIVATE_CREDENTIALS.equals(hint)) privateCredentials = (Set)value;
   }

   protected Object createObject() throws SAXException
   {
      return new Subject(readOnly, principals, publicCredentials, privateCredentials);
   }
}
