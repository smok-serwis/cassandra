/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import mx4j.log.Log;
import mx4j.log.Logger;
import org.w3c.dom.Document;

/**
 * XSLTPostProcessor pass the document through an XSLT transformation
 *
 * @version $Revision: 1.8 $
 */
public class XSLTProcessor implements ProcessorMBean, XSLTProcessorMBean, URIResolver
{
   private String path = "mx4j/tools/adaptor/http/xsl";
   private String defaultPage = "serverbydomain";
   private TransformerFactory factory;
   private Map templatesCache = new HashMap();
   private File root;
   private Map mimeTypes = new HashMap();
   /**
    * Indicated whether the file are read from a file
    */
   private boolean useJar = true;
   private volatile boolean useCache = true;
   private ClassLoader targetClassLoader = ClassLoader.getSystemClassLoader();
   /**
    * The locale is set with the default as en_US since it is the
    * one bundled
    */
   private Locale locale = new Locale("en", "");

   public XSLTProcessor()
   {
      factory = TransformerFactory.newInstance();
      factory.setURIResolver(this);
      mimeTypes.put(".gif", "image/gif");
      mimeTypes.put(".jpg", "image/jpg");
      mimeTypes.put(".png", "image/png");
      mimeTypes.put(".tif", "image/tiff");
      mimeTypes.put(".tiff", "image/tiff");
      mimeTypes.put(".ico", "image/ico");
      mimeTypes.put(".html", "text/html");
      mimeTypes.put(".htm", "text/html");
      mimeTypes.put(".txt", "text/plain");
      mimeTypes.put(".xml", "text/xml");
      mimeTypes.put(".xsl", "text/xsl");
      mimeTypes.put(".css", "text/css");
      mimeTypes.put(".js", "text/x-javascript");
      mimeTypes.put(".jar", "application/java-archive");
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }

   public void writeResponse(HttpOutputStream out, HttpInputStream in, Document document) throws IOException
   {
      Logger log = getLogger();

      out.setCode(HttpConstants.STATUS_OKAY);
      out.setHeader("Content-Type", "text/html");
      // added some caching attributes to force not to cache
      out.setHeader("Cache-Control", "no-cache");
      out.setHeader("expires", "now");
      out.setHeader("pragma", "no-cache");
      out.sendHeaders();
      Transformer transformer = null;
      String path = preProcess(in.getPath());

      if (in.getVariable("template") != null)
      {
         transformer = createTransformer(in.getVariable("template") + ".xsl");
      }
      else
      {
         transformer = createTransformer(path + ".xsl");
      }

      if (transformer != null)
      {
         // added so that the document() function works
         transformer.setURIResolver(this);
         // The variables are passed to the XSLT as (param.name, value)
         Map variables = in.getVariables();
         Iterator j = variables.keySet().iterator();
         while (j.hasNext())
         {
            String key = (String)j.next();
            Object value = variables.get(key);
            if (value instanceof String)
            {
               transformer.setParameter("request." + key, value);
            }
            if (value instanceof String[])
            {
               String[] allvalues = (String[])value;
               // not a good solution, only the first one is presented
               transformer.setParameter("request." + key, allvalues[0]);
            }

         }
         if (!variables.containsKey("locale"))
         {
            transformer.setParameter("request.locale", locale.toString());
         }
         try
         {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (log.isEnabledFor(Logger.TRACE)) log.trace("transforming " + path);
            transformer.transform(new DOMSource(document), new StreamResult(output));
            output.writeTo(out);
         }
         catch (TransformerException e)
         {
            log.error("Transformation exception ", e);
         }
      }
      else
      {
         log.warn("Transformer for path " + path + " not found");
      }
   }

   protected Transformer createTransformer(String path)
   {
      Logger logger = getLogger();
      try
      {
         synchronized (this)
         {
            if (useCache && templatesCache.containsKey(path))
            {
               return ((Templates)templatesCache.get(path)).newTransformer();
            }
            else
            {
               InputStream stream = getInputStream(path);
               if (stream != null)
               {
                  if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Creating template for path " + path);
                  Templates template = factory.newTemplates(new StreamSource(stream));
                  if (useCache) templatesCache.put(path, template);
                  return template.newTransformer();
               }
               else
               {
                  if (logger.isEnabledFor(Logger.INFO)) logger.info("XSL template for path '" + path + "' not found");
               }
            }
         }
      }
      catch (TransformerConfigurationException e)
      {
         logger.error("Exception during XSL template construction", e);
      }
      return null;
   }

   protected void processHttpException(HttpInputStream in, HttpOutputStream out, HttpException e) throws IOException
   {
      out.setCode(e.getCode());
      out.setHeader("Content-Type", "text/html");
      out.sendHeaders();
      // hardcoded dir :-P
      Transformer transformer = createTransformer("error.xsl");
      transformer.setURIResolver(this);
      Document doc = e.getResponseDoc();
      if (doc != null)
      {
         try
         {
            if (!in.getVariables().containsKey("locale"))
            {
               transformer.setParameter("request.locale", locale.toString());
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(output));
            output.writeTo(out);
         }
         catch (TransformerException ex)
         {
            Logger log = getLogger();
            log.error("Exception during error output", ex);
         }
      }
   }

   public void writeError(HttpOutputStream out, HttpInputStream in, Exception e) throws IOException
   {
      Logger log = getLogger();
      Exception t = e;
      if (e instanceof RuntimeMBeanException)
      {
         t = ((RuntimeMBeanException)e).getTargetException();
      }
      if (log.isEnabledFor(Logger.DEBUG)) log.debug("Processing error " + t.getMessage());
      if (t instanceof HttpException)
      {
         processHttpException(in, out, (HttpException)t);
      }
      else if ((t instanceof MBeanException) && (((MBeanException)t).getTargetException() instanceof HttpException))
      {
         processHttpException(in, out, (HttpException)((MBeanException)t).getTargetException());
      }
      else if ((t instanceof ReflectionException) && (((ReflectionException)t).getTargetException() instanceof HttpException))
      {
         processHttpException(in, out, (HttpException)((ReflectionException)t).getTargetException());
      }
      else
      {
         out.setCode(HttpConstants.STATUS_INTERNAL_ERROR);
         out.setHeader("Content-Type", "text/html");
         out.sendHeaders();
      }
   }

   public String preProcess(String path)
   {
      if (path.equals("/"))
      {
         path = "/" + defaultPage;
      }
      return path;
   }

   public String notFoundElement(String path, HttpOutputStream out, HttpInputStream in) throws IOException, HttpException
   {
      Logger log = getLogger();

      File file = new File(this.path, path);
      if (log.isEnabledFor(Logger.DEBUG)) log.debug("Processing file request " + file);
      String name = file.getName();
      int extensionIndex = name.lastIndexOf('.');
      String mime = null;
      if (extensionIndex < 0)
      {
         log.warn("Filename has no extensions " + file.toString());
         mime = "text/plain";
      }
      else
      {
         String extension = name.substring(extensionIndex, name.length());
         if (mimeTypes.containsKey(extension))
         {
            mime = (String)mimeTypes.get(extension);
         }
         else
         {
            log.warn("MIME type not found " + extension);
            mime = "text/plain";
         }
      }
      try
      {
         if (log.isEnabledFor(Logger.DEBUG)) log.debug("Trying to read file " + file);
         BufferedInputStream fileIn = new BufferedInputStream(getInputStream(path));
         ByteArrayOutputStream outArray = new ByteArrayOutputStream();
         BufferedOutputStream outBuffer = new BufferedOutputStream(outArray);
         int piece = 0;
         while ((piece = fileIn.read()) >= 0)
         {
            outBuffer.write(piece);
         }
         outBuffer.flush();
         out.setCode(HttpConstants.STATUS_OKAY);
         out.setHeader("Content-type", mime);
         out.sendHeaders();
         if (log.isEnabledFor(Logger.DEBUG)) log.debug("File output " + mime);
         outArray.writeTo(out);
         fileIn.close();
      }
      catch (Exception e)
      {
         log.warn("Exception loading file " + file, e);
         throw new HttpException(HttpConstants.STATUS_NOT_FOUND, "file " + file + " not found");
      }
      return null;
   }

   protected InputStream getInputStream(String path)
   {
      InputStream file = null;
      if (!useJar)
      {
         try
         {
            // load from a dir
            file = new FileInputStream(new File(this.root, path));
         }
         catch (FileNotFoundException e)
         {
            Logger log = getLogger();
            log.error("File not found", e);
         }
      }
      else
      {
         // load from a jar
         String targetFile = this.path;
         // workaround, should tought of somehting better
         if (path.startsWith("/"))
         {
            targetFile += path;
         }
         else
         {
            targetFile += "/" + path;
         }
         if (root != null)
         {
            file = targetClassLoader.getResourceAsStream(targetFile);
         }
         if (file == null)
         {
            ClassLoader cl = getClass().getClassLoader();
            if (cl == null)
            {
               file = ClassLoader.getSystemClassLoader().getResourceAsStream(targetFile);
            }
            else
            {
               file = getClass().getClassLoader().getResourceAsStream(targetFile);
            }
            file = getClass().getClassLoader().getResourceAsStream(targetFile);
         }
      }

      return file;
   }

   public Source resolve(String href, String base)
   {
      StreamSource source = new StreamSource(getInputStream(href));
      // this works with saxon7/saxon6.5.2/xalan
      source.setSystemId(href);
      return source;
   }

   public void setFile(String file)
   {
      if (file != null)
      {
         Logger log = getLogger();

         File target = new File(file);
         if (!target.exists())
         {
            log.warn("Target file " + file + " does not exist, defaulting to previous");
            return;
         }
         if (target.isDirectory())
         {
            useJar = false;
            if (log.isEnabledFor(Logger.DEBUG)) log.debug("Using " + file + " as the root dir");
            this.root = target;
            return;
         }
         if (target.isFile() && (target.getName().endsWith(".jar") ||
                                 (target.getName().endsWith(".zip"))))
         {
            try
            {
               URL url = target.toURL();
               targetClassLoader = new URLClassLoader(new URL[]{url});
               if (log.isEnabledFor(Logger.DEBUG)) log.debug("Using compressed file " + url + " as the root file");
               this.root = target;
               useJar = true;
            }
            catch (MalformedURLException e)
            {
               log.warn("Unable to create class loader", e);
            }
         }
         else
         {
            log.warn("Target file " + file + " does not exist, defaulting to previous");
         }
      }
   }

   public String getFile()
   {
      return (root != null) ? root.getName() : null;
   }

   public String getPathInJar()
   {
      return path;
   }

   public void setPathInJar(String path)
   {
      this.path = path;
   }

   public String getDefaultPage()
   {
      return defaultPage;
   }

   public void setDefaultPage(String defaultPage)
   {
      this.defaultPage = defaultPage;
   }

   public boolean isUseJar()
   {
      return useJar;
   }

   public boolean isUsePath()
   {
      return !useJar;
   }

   public void addMimeType(String extension, String type)
   {
      if (extension != null && type != null)
      {
         Logger log = getLogger();
         if (log.isEnabledFor(Logger.DEBUG)) log.debug("Added MIME type " + type + " for extension " + extension);
         mimeTypes.put(extension, type);
      }
   }

   public void setUseCache(boolean useCache)
   {
      this.useCache = useCache;
   }

   public boolean isUseCache()
   {
      return useCache;
   }

   public String getName()
   {
      return "XSLT Processor";
   }

   public Locale getLocale()
   {
      return locale;
   }

   public void setLocale(Locale locale)
   {
      this.locale = locale;
   }

   public void setLocaleString(String locale)
   {
      if (locale == null || locale.length() == 0)
      {
         this.locale = new Locale("en", "");
      }
      else
      {
         // split locale based on underbar
         StringTokenizer tknzr = new StringTokenizer(locale, "_");
         String language = tknzr.nextToken();
         String country = "";
         String variant = "";
         if (tknzr.hasMoreTokens())
            country = tknzr.nextToken();
         if (tknzr.hasMoreTokens())
            variant = tknzr.nextToken();
         this.locale = new Locale(language, country, variant);
      }
   }
}
