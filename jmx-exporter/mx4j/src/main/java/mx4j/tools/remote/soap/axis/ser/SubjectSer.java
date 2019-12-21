/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import java.io.IOException;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * @version $Revision: 1.4 $
 */
public class SubjectSer extends AxisSerializer
{
   static final String TYPE = "Subject";
   static final String READ_ONLY = "readOnly";
   static final String PRINCIPALS = "principals";
   static final String PUBLIC_CREDENTIALS = "publicCredentials";
   static final String PRIVATE_CREDENTIALS = "privateCredentials";
   private static final QName READ_ONLY_QNAME = new QName("", READ_ONLY);
   private static final QName PRINCIPALS_QNAME = new QName("", PRINCIPALS);
   private static final QName PUBLIC_CREDENTIALS_QNAME = new QName("", PUBLIC_CREDENTIALS);
   private static final QName PRIVATE_CREDENTIALS_QNAME = new QName("", PRIVATE_CREDENTIALS);

   public void serialize(QName name, Attributes attributes, Object value, SerializationContext context) throws IOException
   {
      Subject subject = (Subject)value;
      context.startElement(name, attributes);
      context.serialize(READ_ONLY_QNAME, null, new Boolean(subject.isReadOnly()));
      context.serialize(PRINCIPALS_QNAME, null, subject.getPrincipals());
      context.serialize(PUBLIC_CREDENTIALS_QNAME, null, subject.getPublicCredentials());
      context.serialize(PRIVATE_CREDENTIALS_QNAME, null, subject.getPrivateCredentials());
      context.endElement();
   }

   public Element writeSchema(Class aClass, Types types) throws Exception
   {
      Element complexType = types.createElement(SCHEMA_COMPLEX_TYPE);
      complexType.setAttribute("name", TYPE);
      Element allElement = types.createElement(SCHEMA_ALL);
      complexType.appendChild(allElement);

      Element readOnlyElement = types.createElement(SCHEMA_ELEMENT);
      readOnlyElement.setAttribute("name", READ_ONLY);
      readOnlyElement.setAttribute("type", XMLType.XSD_BOOLEAN.getLocalPart());
      allElement.appendChild(readOnlyElement);

      Element principalsElement = types.createElement(SCHEMA_ELEMENT);
      principalsElement.setAttribute("name", PRINCIPALS);
      principalsElement.setAttribute("type", SetSer.TYPE);
      allElement.appendChild(principalsElement);

      Element publicCredentialsElement = types.createElement(SCHEMA_ELEMENT);
      publicCredentialsElement.setAttribute("name", PUBLIC_CREDENTIALS);
      publicCredentialsElement.setAttribute("type", SetSer.TYPE);
      allElement.appendChild(publicCredentialsElement);

      Element privateCredentialsElement = types.createElement(SCHEMA_ELEMENT);
      privateCredentialsElement.setAttribute("name", PRIVATE_CREDENTIALS);
      privateCredentialsElement.setAttribute("type", SetSer.TYPE);
      allElement.appendChild(privateCredentialsElement);

      return complexType;
   }
}
