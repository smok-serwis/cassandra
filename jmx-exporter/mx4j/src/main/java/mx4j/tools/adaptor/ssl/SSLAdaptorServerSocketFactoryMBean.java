/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.ssl;

import mx4j.tools.adaptor.AdaptorServerSocketFactory;

/**
 * Management interface for the SSL ServerSocket factory
 *
 * @version $Revision: 1.3 $
 */
public interface SSLAdaptorServerSocketFactoryMBean extends AdaptorServerSocketFactory
{
   /**
    * Sets the type of the keystore, by default is "JKS".
    */
   public void setKeyStoreType(String keyStoreType);

   /**
    * Sets the type of the truststore, by default is "JKS".
    */
   public void setTrustStoreType(String trustStoreType);

   /**
    * Sets the name of the keystore; if the keystore is of type JKS, then this is a file name, that will be
    * resolved by the ClassLoader of this class, via getResourceAsStream.
    */
   public void setKeyStoreName(String name);

   /**
    * Sets the name of the truststore; if the truststore is of type JKS, then this is a file name, that will be
    * resolved by the ClassLoader of this class, via getResourceAsStream.
    */
   public void setTrustStoreName(String name);

   /**
    * Sets the password to access the keystore specified by {@link #setKeyStoreName}. <p>
    * It correspond to the value of the -storepass option of keytool.
    */
   public void setKeyStorePassword(String password);

   /**
    * Sets the password to access the truststore specified by {@link #setTrustStoreName}. <p>
    * It correspond to the value of the -storepass option of keytool.
    */
   public void setTrustStorePassword(String password);

   /**
    * Sets the key manager algorithm, by default is "SunX509".
    */
   public void setKeyManagerAlgorithm(String algorithm);

   /**
    * Sets the trust manager algorithm, by default is "SunX509".
    */
   public void setTrustManagerAlgorithm(String algorithm);

   /**
    * Sets the password to access the key present in the keystore. <p>
    * It correspond to the value of the -keypass option of keytool; if not specified, the KeyStore password is taken.
    */
   public void setKeyManagerPassword(String password);

   /**
    * Sets the SSL protocol version, by default is "TLS".
    */
   public void setSSLProtocol(String protocol);
}
