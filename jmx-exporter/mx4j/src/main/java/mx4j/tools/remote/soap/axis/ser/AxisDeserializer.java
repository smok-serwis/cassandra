/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.soap.axis.ser;

import javax.xml.namespace.QName;

import mx4j.log.Log;
import mx4j.log.Logger;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.4 $
 */
public abstract class AxisDeserializer extends DeserializerImpl
{
   protected Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException
   {
      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.TRACE))
      {
         logger.trace("Enter: " + getClass().getName() + ".onStartElement()");
         logger.trace("namespace: " + namespace);
         logger.trace("localName: " + localName);
         logger.trace("prefix: " + prefix);
      }
   }

   public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException
   {
      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.TRACE))
      {
         logger.trace("Enter: " + getClass().getName() + ".onStartChild()");
         logger.trace("namespace: " + namespace);
         logger.trace("localName: " + localName);
         logger.trace("prefix: " + prefix);
      }
/*
      if (context.isNil(attributes))
      {
         setChildValue(null, localName);
         return null;
      }
*/
      QName itemType = context.getTypeFromAttributes(namespace, localName, attributes);
      Deserializer deserializer = null;
      if (itemType != null) deserializer = context.getDeserializerForType(itemType);
      if (deserializer == null) deserializer = new DeserializerImpl();

      deserializer.registerValueTarget(new DeserializerTarget(this, localName));
      addChildDeserializer(deserializer);

      return (SOAPHandler)deserializer;
   }

   public void setChildValue(Object value, Object hint) throws SAXException
   {
      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.TRACE))
      {
         logger.trace("Enter: " + getClass().getName() + ".setChildValue()");
         logger.trace("value: " + value);
         logger.trace("hint: " + hint);
      }
      onSetChildValue(value, hint);
   }

   protected abstract void onSetChildValue(Object value, Object hint) throws SAXException;

   public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException
   {
      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.TRACE))
      {
         logger.trace("Enter: " + getClass().getName() + ".onEndElement()");
         logger.trace("namespace: " + namespace);
         logger.trace("localName: " + localName);
      }
      setValue(createObject());
   }

   protected abstract Object createObject() throws SAXException;
}
