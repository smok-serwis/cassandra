/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManagerFactory;
import mx4j.log.Log;
import mx4j.log.Logger;

/**
 * TODO: Fix this class to avoid hardcoding Sun's provider, since it will not work with IBM's JDK.
 * This MBean creates SSLServerSocket instances. <p>
 * It can be configured to use a specific keystore and SSL protocol version to create SSLServerSockets
 * that will use the keystore information to encrypt data. <br>
 * <p/>
 * A keystore can be created with this command:
 * <pre>
 * keytool -genkey -v -keystore store.key -storepass storepwd -keypass keypwd -dname "CN=Simone Bordet, OU=Project Administrator, O=MX4J, L=Torino, S=TO, C=IT" -validity 365
 * </pre>
 * or with this minimal command (that will prompt you for further information):
 * <pre>
 * keytool -genkey -keystore store.key
 * </pre>
 * <p/>
 * A keystore may contains more than one entry, but only the first entry will be used
 * for encryption, no matter which is the alias for that entry.
 * <p/>
 * Following the first example of generation of the keystore, this MBean must be instantiated and then setup by
 * invoking the following methods:
 * <ul>
 * <li> {@link #setKeyStoreName}("store.key");
 * <li> {@link #setKeyStorePassword}("storepwd");
 * <li> {@link #setKeyManagerPassword}("keypwd");
 * </ul>
 * before {@link #createServerSocket} is called.
 *
 * @version $Revision: 1.5 $
 */
public class SSLAdaptorServerSocketFactory implements SSLAdaptorServerSocketFactoryMBean
{
   static
   {
      addProvider(new com.sun.net.ssl.internal.ssl.Provider());
   }

   private String m_keyStoreType = "JKS";
   private String m_trustStoreType = "JKS";
   private String m_keyStoreName;
   private String m_trustStoreName;
   private String m_keyStorePassword;
   private String m_trustStorePassword;
   private String m_keyManagerAlgorithm = "SunX509";
   private String m_trustManagerAlgorithm = "SunX509";
   private String m_keyManagerPassword;
   private String m_sslProtocol = "TLS";

   public static void addProvider(Provider provider)
   {
      Security.addProvider(provider);
   }

   public void setKeyStoreType(String keyStoreType)
   {
      if (keyStoreType == null || keyStoreType.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid KeyStore type");
      }
      m_keyStoreType = keyStoreType;
   }

   public void setTrustStoreType(String trustStoreType)
   {
      if (trustStoreType == null || trustStoreType.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid TrustStore type");
      }
      m_trustStoreType = trustStoreType;
   }

   public void setKeyStoreName(String name)
   {
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid KeyStore name");
      }
      m_keyStoreName = name;
   }

   public void setTrustStoreName(String name)
   {
      if (name == null || name.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid TrustStore name");
      }
      m_trustStoreName = name;
   }

   public void setKeyStorePassword(String password)
   {
      if (password == null || password.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid KeyStore password");
      }
      m_keyStorePassword = password;
   }

   public void setTrustStorePassword(String password)
   {
      if (password == null || password.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid TrustStore password");
      }
      m_trustStorePassword = password;
   }

   public void setKeyManagerAlgorithm(String algorithm)
   {
      if (algorithm == null || algorithm.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid KeyManager algorithm");
      }
      m_keyManagerAlgorithm = algorithm;
   }

   public void setTrustManagerAlgorithm(String algorithm)
   {
      if (algorithm == null || algorithm.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid TrustManager algorithm");
      }
      m_trustManagerAlgorithm = algorithm;
   }

   public void setKeyManagerPassword(String password)
   {
      if (password == null || password.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid KeyManager password");
      }
      m_keyManagerPassword = password;
   }

   public void setSSLProtocol(String protocol)
   {
      if (protocol == null || protocol.trim().length() == 0)
      {
         throw new IllegalArgumentException("Invalid SSL protocol");
      }
      m_sslProtocol = protocol;
   }

   /**
    * Returns a SSLServerSocket on the given port.
    */
   public ServerSocket createServerSocket(int port, int backlog, String host) throws IOException
   {
      if (m_keyStoreName == null)
      {
         throw new IOException("KeyStore file name cannot be null");
      }
      if (m_keyStorePassword == null)
      {
         throw new IOException("KeyStore password cannot be null");
      }

      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.TRACE))
      {
         logger.trace("Creating SSLServerSocket");
         logger.trace("\tKeyStore " + m_keyStoreName + ", type " + m_keyStoreType);
         logger.trace("\tKeyManager algorithm is " + m_keyManagerAlgorithm);
         logger.trace("\tTrustStore " + m_trustStoreName + ", type " + m_trustStoreType);
         logger.trace("\tTrustManager algorithm is " + m_trustManagerAlgorithm);
         logger.trace("\tSSL protocol version is " + m_sslProtocol);
      }

      try
      {
         KeyStore keystore = KeyStore.getInstance(m_keyStoreType);
         InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream(m_keyStoreName);
         // Must check for nullity, otherwise a new empty keystore is created by KeyStore.load
         if (keyStoreStream == null)
         {
            // Let's look at the file system, maybe that the name provided is in fact a file path
            File fle = new java.io.File(m_keyStoreName);
            if (fle.exists()) keyStoreStream = new FileInputStream(fle);
         }
         if (keyStoreStream == null) throw new IOException("Cannot find KeyStore " + m_keyStoreName);
         keystore.load(keyStoreStream, m_keyStorePassword.toCharArray());
         try
         {
            keyStoreStream.close();
         }
         catch (IOException x)
         {
         }

         KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(m_keyManagerAlgorithm);
         // Use the keystore password as default if not given
         keyFactory.init(keystore, m_keyManagerPassword == null ? m_keyStorePassword.toCharArray() : m_keyManagerPassword.toCharArray());

         TrustManagerFactory trustFactory = null;
         if (m_trustStoreName != null)
         {
            // User specified a trust store, retrieve it

            if (m_trustStorePassword == null)
            {
               throw new IOException("TrustStore password cannot be null");
            }

            KeyStore trustStore = KeyStore.getInstance(m_trustStoreType);
            InputStream trustStoreStream = getClass().getClassLoader().getResourceAsStream(m_trustStoreName);
            // Check for nullity
            if (trustStoreStream == null)
            {
               throw new IOException("Cannot find TrustStore " + m_trustStoreName);
            }
            trustStore.load(trustStoreStream, m_trustStorePassword.toCharArray());

            trustFactory = TrustManagerFactory.getInstance(m_trustManagerAlgorithm);
            trustFactory.init(trustStore);
         }

         SSLContext context = SSLContext.getInstance(m_sslProtocol);
         // Below call does not handle TrustManagers, needed when server must authenticate clients.
         context.init(keyFactory.getKeyManagers(), trustFactory == null ? null : trustFactory.getTrustManagers(), null);

         SSLServerSocketFactory ssf = context.getServerSocketFactory();
         SSLServerSocket serverSocket = (SSLServerSocket)ssf.createServerSocket(port, backlog, InetAddress.getByName(host));

         return serverSocket;
      }
      catch (IOException x)
      {
         logger.error("", x);
         throw x;
      }
      catch (UnrecoverableKeyException x)
      {
         // Wrong password for the key
         logger.error("Probably a bad key password", x);
         throw new IOException("Probably a bad key password: " + x.toString());
      }
      catch (Exception x)
      {
         logger.error("Unexpected exception", x);
         throw new IOException(x.toString());
      }
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }
}
