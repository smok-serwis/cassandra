/*
 *  Copyright (C) The MX4J Contributors.
 *  All rights reserved.
 *
 *  This software is distributed under the terms of the MX4J License version 1.0.
 *  See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.JMException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.tools.adaptor.AdaptorServerSocketFactory;
import mx4j.tools.adaptor.PlainAdaptorServerSocketFactory;
import mx4j.util.Base64Codec;
import org.w3c.dom.Document;

/**
 * HttpAdaptor sets the basic adaptor listening for HTTP requests
 *
 * @version $Revision: 1.13 $
 */
public class HttpAdaptor implements HttpAdaptorMBean, MBeanRegistration
{
   private static final String VERSION = "3.0.1";

   /**
    * Port to listen for connections
    */
   private int port = 8080;

   /**
    * Host where to set the server socket
    */
   private String host = "localhost";

   /**
    * Target server
    */
   private MBeanServer server;

   /**
    * Server socket
    */
   private ServerSocket serverSocket;

   /**
    * Indicates whether the server is running
    */
   private boolean alive;

   /**
    * Map of commands indexed by the request path
    */
   private Map commands = new HashMap();

   /**
    * Target processor
    */
   private ProcessorMBean processor = null;

   /**
    * Target processor name
    */
   private ObjectName processorName = null;

   /**
    * Default processor
    */
   private ProcessorMBean defaultProcessor = new DefaultProcessor();

   private String authenticationMethod = "none";

   // Should be dependant on the server?
   private String realm = "MX4J";

   private Map authorizations = new HashMap();

   private AdaptorServerSocketFactory socketFactory = null;

   private ObjectName factoryName;

   private String processorClass;

   private Date startDate;

   private long requestsCount;

   private String[][] defaultCommandProcessors = {
      {"server", "mx4j.tools.adaptor.http.ServerCommandProcessor"},
      {"serverbydomain", "mx4j.tools.adaptor.http.ServerByDomainCommandProcessor"},
      {"mbean", "mx4j.tools.adaptor.http.MBeanCommandProcessor"},
      {"setattributes", "mx4j.tools.adaptor.http.SetAttributesCommandProcessor"},
      {"setattribute", "mx4j.tools.adaptor.http.SetAttributeCommandProcessor"},
      {"getattribute", "mx4j.tools.adaptor.http.GetAttributeCommandProcessor"},
      {"delete", "mx4j.tools.adaptor.http.DeleteMBeanCommandProcessor"},
      {"invoke", "mx4j.tools.adaptor.http.InvokeOperationCommandProcessor"},
      {"create", "mx4j.tools.adaptor.http.CreateMBeanCommandProcessor"},
      {"constructors", "mx4j.tools.adaptor.http.ConstructorsCommandProcessor"},
      {"relation", "mx4j.tools.adaptor.http.RelationCommandProcessor"},
      {"empty", "mx4j.tools.adaptor.http.EmptyCommandProcessor"}};

   private DocumentBuilder builder;

   /**
    * Default Constructor added so that we can have some additional
    * constructors as well.
    */
   public HttpAdaptor()
   {
   }

   /**
    * Overloaded constructor to allow the port to be set.
    * The reason this was added was to allow the loading of this adaptor by
    * the dynamic loading service of the MBean server and have the port set
    * from a param in the mlet file. Example: (replaced lt & gt symbol with [])
    * <br>[mlet code="mx4j.tools.adaptor.http.HttpAdaptor"
    * <br>      archive="mx4j.jar"
    * <br>      name="Server:name=HttpAdaptor"]
    * <br>   [arg type="int" value="12345"]
    * <br>[/mlet]
    * <p/>
    * <p>This constructor uses the default host or the host must be set later.
    *
    * @param port The port on which the HttpAdaptor should listen
    */
   public HttpAdaptor(int port)
   {
      this.port = port;
   }


   /**
    * Overloaded constructor to allow the host to be set.
    * The reason this was added was to allow the loading of this adaptor by
    * the dynamic loading service of the MBean server and have the host set
    * from a param in the mlet file. Example: (replaced lt & gt symbol with [])
    * <br>[mlet code="mx4j.tools.adaptor.http.HttpAdaptor"
    * <br>      archive="mx4j.jar"
    * <br>      name="Server:name=HttpAdaptor"]
    * <br>   [arg type="java.lang.String" value="someserver.somehost.com"]
    * <br>[/mlet]
    * <p/>
    * <p>This constructor uses the default port or the port must be set later.
    *
    * @param host The host on which the HttpAdaptor should listen
    */
   public HttpAdaptor(String host)
   {
      this.host = host;
   }


   /**
    * Overloaded constructor to allow the port to be set.
    * The reason this was added was to allow the loading of this adaptor by
    * the dynamic loading service of the MBean server and have the port set
    * from a param in the mlet file. Example: (replaced lt & gt symbol with [])
    * NOTE that the port must come before the host in the arg list of the mlet
    * <br>[mlet code="mx4j.tools.adaptor.http.HttpAdaptor"
    * <br>      archive="mx4j.jar"
    * <br>      name="Server:name=HttpAdaptor"]
    * <br>   [arg type="int" value="12345"]
    * <br>   [arg type="java.lang.String" value="someserver.somehost.com"]
    * <br>[/mlet]
    *
    * @param port The port on which the HttpAdaptor should listen
    * @param host The host on which the HttpAdaptor should listen
    */
   public HttpAdaptor(int port, String host)
   {
      this.port = port;
      this.host = host;
   }


   /**
    * Sets the value of the server's port
    *
    * @param port the new port's value
    */
   public void setPort(int port)
   {
      if (alive)
      {
         throw new IllegalArgumentException("Not possible to change port with the server running");
      }
      this.port = port;
   }


   /**
    * Returns the port where the server is running on. Default is 8080
    *
    * @return HTTPServer's port
    */
   public int getPort()
   {
      return port;
   }


   /**
    * Sets the host name where the server will be listening
    *
    * @param host Server's host
    */
   public void setHost(String host)
   {
      if (alive)
      {
         throw new IllegalArgumentException("Not possible to change port with the server running");
      }
      this.host = host;
   }


   /**
    * Return the host name the server will be listening to. If null the server
    * listen at the localhost
    *
    * @return the current hostname
    */
   public String getHost()
   {
      return host;
   }


   /**
    * Sets the Authentication Method.
    *
    * @param method none/basic/digest
    */
   public void setAuthenticationMethod(String method)
   {
      if (alive)
      {
         throw new IllegalArgumentException("Not possible to change authentication method with the server running");
      }
      if (method == null || !(method.equals("none") || method.equals("basic") || method.equals("digest")))
      {
         throw new IllegalArgumentException("Only accept methods none/basic/digest");
      }
      this.authenticationMethod = method;
   }


   /**
    * Authentication Method
    *
    * @return authentication method
    */
   public String getAuthenticationMethod()
   {
      return authenticationMethod;
   }


   /**
    * Sets the object which will post process the XML results. The last value set
    * between the setPostProcessor and setPostProcessorName will be the valid one
    *
    * @param processor a Post processor object
    */
   public void setProcessor(ProcessorMBean processor)
   {
      this.processor = processor;
      this.processorName = null;
   }


   /**
    * Sets the classname of the object which will post process the XML results. The adaptor
    * will try to build the object and use the processor name ObjectName to register it
    * The class name has to implements mx4j.tools.adaptor.http.ProcessorMBean and be MBean
    * compliant
    *
    * @param processorClass a Post processor object
    */
   public void setProcessorClass(String processorClass)
   {
      this.processorClass = processorClass;
   }


   /**
    * Sets the object name of the PostProcessor MBean. If ProcessorClass is set the processor
    * will be created
    *
    * @param processorName a Post processor object
    */
   public void setProcessorNameString(String processorName) throws MalformedObjectNameException
   {
      this.processorName = new ObjectName(processorName);
   }


   /**
    * Sets the object name which will post process the XML result. The last value
    * set between the setPostProcessor and setPostProcessorName will be the valid
    * one. The MBean will be verified to be of instance HttpPostProcessor
    *
    * @param processorName The new processorName value
    */
   public void setProcessorName(ObjectName processorName)
   {
      this.processor = null;
      this.processorName = processorName;
   }

   public ProcessorMBean getProcessor()
   {
      return this.processor;
   }

   public ObjectName getProcessorName()
   {
      return this.processorName;
   }

   /**
    * Sets the object which create the server sockets
    *
    * @param factory the socket factory
    */
   public void setSocketFactory(AdaptorServerSocketFactory factory)
   {
      this.factoryName = null;
      this.socketFactory = factory;
   }


   /**
    * Sets the factory's object name which will create the server sockets
    *
    * @param factoryName the socket factory
    */
   public void setSocketFactoryName(ObjectName factoryName)
   {
      this.socketFactory = null;
      this.factoryName = factoryName;
   }


   /**
    * Sets the factory's object name which will create the server sockets
    *
    * @param factoryName the socket factory
    */
   public void setSocketFactoryNameString(String factoryName) throws MalformedObjectNameException
   {
      this.socketFactory = null;
      this.factoryName = new ObjectName(factoryName);
   }


   /**
    * Indicates whether the server's running
    *
    * @return The active value
    */
   public boolean isActive()
   {
      return alive;
   }


   /**
    * Starting date
    *
    * @return The date when the server was started
    */
   public Date getStartDate()
   {
      return startDate;
   }


   /**
    * Requests count
    *
    * @return The total of requests served so far
    */
   public long getRequestsCount()
   {
      return requestsCount;
   }


   /**
    * Gets the HttpAdaptor version
    *
    * @return HttpAdaptor's version
    */
   public String getVersion()
   {
      return VERSION;
   }


   /**
    * Adds a command processor object
    */
   public void addCommandProcessor(String path, HttpCommandProcessor processor)
   {
      commands.put(path, processor);
      if (alive)
      {
         processor.setMBeanServer(server);
         processor.setDocumentBuilder(builder);
      }
   }


   /**
    * Adds a command processor object by class
    */
   public void addCommandProcessor(String path, String processorClass)
   {
      try
      {
         HttpCommandProcessor processor = (HttpCommandProcessor)Class.forName(processorClass).newInstance();
         addCommandProcessor(path, processor);
      }
      catch (Exception e)
      {
         Logger log = getLogger();
         log.error("Exception creating Command Processor of class " + processorClass, e);
      }
   }


   /**
    * Removes a command processor object by class
    */
   public void removeCommandProcessor(String path)
   {
      if (commands.containsKey(path))
      {
         commands.remove(path);
      }
   }


   /**
    * Starts the server
    */
   public void start()
           throws IOException
   {
      final Logger logger = getLogger();

      if (server != null)
      {
         serverSocket = createServerSocket();

         if (serverSocket == null)
         {
            logger.error("Server socket is null");
            return;
         }

         if (processorClass != null && processorName != null)
         {
            if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Building processor class of type " + processorClass + " and name " + processorName);
            try
            {
               server.createMBean(processorClass, processorName, null);
            }
            catch (JMException e)
            {
               logger.error("Exception creating processor class", e);
            }
         }

         Iterator i = commands.values().iterator();
         while (i.hasNext())
         {
            HttpCommandProcessor processor = (HttpCommandProcessor)i.next();
            processor.setMBeanServer(server);
            processor.setDocumentBuilder(builder);
         }

         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("HttpAdaptor server listening on port " + port);
         alive = true;
         Thread serverThread = new Thread(new Runnable()
         {
            public void run()
            {
               if (logger.isEnabledFor(Logger.INFO)) logger.info("HttpAdaptor version " + VERSION + " started on port " + port);

               startDate = new Date();
               requestsCount = 0;

               while (alive)
               {
                  try
                  {
                     Socket client = null;
                     client = serverSocket.accept();
                     if (!alive)
                     {
                        client.close();
                        break;
                     }
                     requestsCount++;
                     new HttpClient(client).start();
                  }
                  catch (InterruptedIOException e)
                  {
                     continue;
                  }
                  catch (IOException e)
                  {
                     continue;
                  }
                  catch (Exception e)
                  {
                     logger.warn("Exception during request processing", e);
                     continue;
                  }
                  catch (Error e)
                  {
                     logger.error("Error during request processing", e);
                     continue;
                  }
               }
               try
               {
                  serverSocket.close();
               }
               catch (IOException e)
               {
                  logger.warn("Exception closing the server", e);
               }
               serverSocket = null;
               alive = false;
               if (logger.isEnabledFor(Logger.INFO)) logger.info("HttpAdaptor version " + VERSION + " stopped on port " + port);
            }
         });
         serverThread.start();
      }
      else
      {
         if (logger.isEnabledFor(Logger.INFO)) logger.info("Start failed, no server target server has been set");
      }
   }


   /**
    * Restarts the server. Useful when changing the Server parameters
    *
    * @deprecated as of RC 1
    */
   public void restart()
           throws IOException
   {
      stop();
      start();
   }


   /**
    * Stops the HTTP daemon
    */
   public void stop()
   {
      try
      {
         if (alive)
         {
            alive = false;
            // force the close with a socket call
            new Socket(host, port);
         }
      }
      catch (IOException e)
      {
         getLogger().warn(e.getMessage());
      }
      try
      {
         if (serverSocket != null)
         {
            serverSocket.close();
         }
      }
      catch (IOException e)
      {
         getLogger().warn(e.getMessage());
      }
   }


   /**
    * Adds an authorization pair as username/password
    */
   public void addAuthorization(String username, String password)
   {
      if (username == null || password == null)
      {
         throw new IllegalArgumentException("username and passwords cannot be null");
      }
      authorizations.put(username, password);
   }


   /**
    * Gathers some basic data
    */
   public ObjectName preRegister(MBeanServer server, ObjectName name)
           throws java.lang.Exception
   {
      this.server = server;
      buildCommands();
      return name;
   }


   public void postRegister(Boolean registrationDone)
   {
   }


   public void preDeregister()
           throws java.lang.Exception
   {
      // stop the server
      stop();
   }


   public void postDeregister()
   {
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   private ServerSocket createServerSocket() throws IOException
   {
      if (socketFactory == null)
      {
         if (factoryName == null)
         {
            socketFactory = new PlainAdaptorServerSocketFactory();
            return socketFactory.createServerSocket(port, 50, host);
         }
         else
         {
            try
            {
               return (ServerSocket)server.invoke(factoryName, "createServerSocket", new Object[]{new Integer(port), new Integer(50), host}, new String[]{"int", "int", "java.lang.String"});
            }
            catch (Exception x)
            {
               Logger log = getLogger();
               log.error("Exception invoking AdaptorServerSocketFactory via MBeanServer", x);
            }
         }
      }
      else
      {
         return socketFactory.createServerSocket(port, 50, host);
      }

      return null;
   }


   private boolean isUsernameValid(String username, String password)
   {
      if (authorizations.containsKey(username))
      {
         return password.equals(authorizations.get(username));
      }
      return false;
   }


   protected HttpCommandProcessor getProcessor(String path)
   {
      return (HttpCommandProcessor)commands.get(path);
   }


   /**
    * Build the commands
    */
   protected void buildCommands()
   {
      Logger log = getLogger();
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         builder = factory.newDocumentBuilder();
         for (int i = 0; i < defaultCommandProcessors.length; i++)
         {
            try
            {
               HttpCommandProcessor processor = (HttpCommandProcessor)Class.forName(defaultCommandProcessors[i][1]).newInstance();
               commands.put(defaultCommandProcessors[i][0], processor);
            }
            catch (Exception e)
            {
               log.warn("Exception building command procesor", e);
            }
         }
      }
      catch (ParserConfigurationException e)
      {
         log.error("Exception building the Document Factories", e);
      }
   }


   protected void postProcess(HttpOutputStream out, HttpInputStream in, Document document)
           throws IOException, JMException
   {
      boolean processed = false;
      // inefficient but handles modifications at runtime
      if (processorName != null)
      {
         if (server.isRegistered(processorName) &&
                 server.isInstanceOf(processorName, "mx4j.tools.adaptor.http.ProcessorMBean"))
         {
            server.invoke(processorName,
                    "writeResponse",
                    new Object[]{out, in, document},
                    new String[]{"mx4j.tools.adaptor.http.HttpOutputStream", "mx4j.tools.adaptor.http.HttpInputStream", "org.w3c.dom.Document"});
            processed = true;
         }
         else
         {
            Logger log = getLogger();
            if (log.isEnabledFor(Logger.TRACE)) log.trace(processorName + " not found");
         }
      }
      if (!processed && processor != null)
      {
         processor.writeResponse(out, in, document);
         processed = true;
      }
      if (!processed)
      {
         defaultProcessor.writeResponse(out, in, document);
      }
   }


   protected void findUnknownElement(String path, HttpOutputStream out, HttpInputStream in)
           throws IOException, JMException
   {
      boolean processed = false;
      // inefficient but handles modifications at runtime
      if (processorName != null)
      {
         if (server.isRegistered(processorName) &&
                 server.isInstanceOf(processorName, "mx4j.tools.adaptor.http.ProcessorMBean"))
         {
            server.invoke(processorName,
                    "notFoundElement",
                    new Object[]{path, out, in},
                    new String[]{"java.lang.String", "mx4j.tools.adaptor.http.HttpOutputStream", "mx4j.tools.adaptor.http.HttpInputStream"});
            processed = true;
         }
         else
         {
            Logger log = getLogger();
            if (log.isEnabledFor(Logger.TRACE)) log.trace(processorName + " not found");
         }
      }
      if (!processed && processor != null)
      {
         processor.notFoundElement(path, out, in);
         processed = true;
      }
      if (!processed)
      {
         defaultProcessor.notFoundElement(path, out, in);
      }
   }


   protected String preProcess(String path)
           throws IOException, JMException
   {
      boolean processed = false;
      // inefficient but handles modifications at runtime
      if (processorName != null)
      {
         Logger log = getLogger();
         if (log.isEnabledFor(Logger.TRACE)) log.trace("Preprocess using " + processorName);
         if (server.isRegistered(processorName) &&
                 server.isInstanceOf(processorName, "mx4j.tools.adaptor.http.ProcessorMBean"))
         {
            if (log.isEnabledFor(Logger.TRACE)) log.trace("Preprocessing");
            path = (String)server.invoke(processorName,
                    "preProcess",
                    new Object[]{path},
                    new String[]{"java.lang.String"});
            processed = true;
         }
         else
         {
            if (log.isEnabledFor(Logger.TRACE)) log.trace(processorName + " not found");
         }
      }
      if (!processed && processor != null)
      {
         path = processor.preProcess(path);
         processed = true;
      }
      if (!processed)
      {
         path = defaultProcessor.preProcess(path);
      }
      return path;
   }


   protected void postProcess(HttpOutputStream out, HttpInputStream in, Exception e)
           throws IOException, JMException
   {
      boolean processed = false;
      // inefficient but handles modifications at runtime
      if (processorName != null)
      {
         if (server.isRegistered(processorName) &&
                 server.isInstanceOf(processorName, "mx4j.tools.adaptor.http.ProcessorMBean"))
         {
            server.invoke(processorName,
                    "writeError",
                    new Object[]{out, in, e},
                    new String[]{"mx4j.tools.adaptor.http.HttpOutputStream", "mx4j.tools.adaptor.http.HttpInputStream", "java.lang.Exception"});
            processed = true;
         }
         else
         {
            Logger log = getLogger();
            if (log.isEnabledFor(Logger.TRACE)) log.trace(processorName + " not found");
         }
      }
      if (!processed && processor != null)
      {
         processor.writeError(out, in, e);
         processed = true;
      }
      if (!processed)
      {
         defaultProcessor.writeError(out, in, e);
      }
   }


   private class HttpClient extends Thread
   {
      private Socket client;


      HttpClient(Socket client)
      {
         this.client = client;
      }

      public boolean isValid(String authorizationString)
      {
         if (authenticationMethod.startsWith("basic"))
         {
            authorizationString = authorizationString.substring(5, authorizationString.length());
            String decodeString = new String(Base64Codec.decodeBase64(authorizationString.getBytes()));
            if (decodeString.indexOf(":") > 0)
            {
               try
               {
                  StringTokenizer tokens = new StringTokenizer(decodeString, ":");
                  String username = tokens.nextToken();
                  String password = tokens.nextToken();
                  return isUsernameValid(username, password);
               }
               catch (Exception e)
               {
                  return false;
               }
            }
         }
         return false;
      }


      private boolean handleAuthentication(HttpInputStream in, HttpOutputStream out) throws IOException
      {
         if (authenticationMethod.equals("basic"))
         {
            String result = in.getHeader("authorization");
            if (result != null)
            {
               if (isValid(result))
               {
                  return true;
               }
               throw new HttpException(HttpConstants.STATUS_FORBIDDEN, "Authentication failed");
            }

            out.setCode(HttpConstants.STATUS_AUTHENTICATE);
            out.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
            out.sendHeaders();
            out.flush();
            return false;
         }
         if (authenticationMethod.equals("digest"))
         {
            // not implemented
         }
         return true;
      }


      public void run()
      {
         Logger log = getLogger();
         HttpInputStream httpIn = null;
         HttpOutputStream httpOut = null;
         try
         {
            // get input streams
            InputStream in = client.getInputStream();
            httpIn = new HttpInputStream(in);
            httpIn.readRequest();

            // Find a suitable command processor
            String path = httpIn.getPath();
            String queryString = httpIn.getQueryString();
            if (log.isEnabledFor(Logger.DEBUG)) log.debug("Request " + path + ((queryString == null) ? "" : ("?" + queryString)));
            String postPath = preProcess(path);
            if (!postPath.equals(path))
            {
               if (log.isEnabledFor(Logger.DEBUG)) log.debug("Processor replaced path " + path + " with the path " + postPath);
               path = postPath;
            }
            OutputStream out = client.getOutputStream();
            httpOut = new HttpOutputStream(out, httpIn);
            if (!handleAuthentication(httpIn, httpOut))
            {
               return;
            }
            HttpCommandProcessor processor = getProcessor(path.substring(1, path.length()));
            if (processor == null)
            {
               if (log.isEnabledFor(Logger.DEBUG)) log.debug("No suitable command processor found, requesting from processor path " + path);
               findUnknownElement(path, httpOut, httpIn);
            }
            else
            {
               Document document = processor.executeRequest(httpIn);
               postProcess(httpOut, httpIn, document);
            }
         }
         catch (Exception ex)
         {
            log.warn("Exception during http request", ex);
            if (httpOut != null)
            {
               try
               {
                  postProcess(httpOut, httpIn, ex);
               }
               catch (IOException e)
               {
                  log.warn("IOException during http request", e);
               }
               catch (JMException e)
               {
                  log.warn("JMException during http request", e);
               }
               catch (RuntimeException rte)
               {
                  log.error("RuntimeException during http request", rte);
               }
               catch (Error er)
               {
                  log.error("Error during http request ", er);
               }
               catch (Throwable t)
               {
                  log.fatal("Throwable during http request ", t);
               }
            }
         }
         catch (Error ex)
         {
            log.error("Error during http request ", ex);
         }
         finally
         {
            try
            {
               if (httpOut != null)
               {
                  httpOut.flush();
               }
            }
            catch (IOException e)
            {
               log.warn("Exception during request processing", e);
            }
            finally
            {
               try
               {
// always close the socket
                  client.close();
               }
               catch (IOException e)
               {
                  log.info("Exception during socket close", e);
               }
            }
         }
      }
   }
}

