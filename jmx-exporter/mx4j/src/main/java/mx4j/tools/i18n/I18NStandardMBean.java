/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.i18n;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 * An extension of StandardMBean to support internationalization. <p>
 * <p/>
 * The I18N information is taken from a property bundle named MyImplMBeanResources
 * where "MyImpl" is the fully qualified class implementing the MBean. <p>
 * These bundles are nested following the class hierachy of the <b>implementation</b>
 * class. This means that a superclass of the real implementing class can
 * define the resource bundle for the common attributes and operations.
 * <p/>
 * The resource bundle naming rules defined by {@link java.util.ResourceBundle}
 * are used; in particular : <UL>
 * <LI>If a <b>class</b> called MyPackage.MyImplMBeanResources_localInfo exists it is used (programmatic methd)
 * <LI>Otherwise the <b>file</b> called MyPackage.MyImplMBeanResources_localInfo.properties is used.
 * </UL>
 * <p/>
 * localInfo consists of one or more sections of "language_country_variant" (eg en_GB or fr_FR).
 * <p/>
 * The locale to be used is determined by one of the following mechanisms (in this order) <UL>
 * <LI>The locale object explicitly passed to the constructor (if not null)
 * <LI>The static method {@link #setDefaultLocale}
 * <LI>The system property "mx4j.descriptionLocale"
 * <LI>The current system default locale
 * </UL>
 * <p/>
 * The bundle should contain keys as described below :
 * <H2>Global bean description</H2>
 * The global bean description is given by the single key "descr": <pre>
 * descr=The MBean Description
 * </pre>
 * <p/>
 * <H2>Attributes</H2>
 * Attribute desciptions are given by keys of form "attr.Name" where Name
 * is the attribute name (the method name minus the get/set prefix) : <pre>
 * attr.Counter=The counter
 * </pre>
 * <p/>
 * <H2>Constructors</H2>
 * <H3>Non ambiguous case</H3>
 * All constructors having a different <b>number</b> of arguments may be described in this way: <pre>
 * cons.N=desciption of constructor N
 * cons.N.param.1=Description of first parameter of constructor N
 * cons.N.paramName.1=paramName1
 * cons.N.param.2=Description of first parameter of constructor N
 * cons.N.paramName.2=paramName2
 * </pre>
 * Where N is a sequential number starting at one.
 * <p/>
 * <H3>Ambiguous case</H3>
 * Where several constructors exist with the same number of arguments an explicit
 * signature must be given. The signature is a comma separated list of class descriptions
 * (as returned by {@link java.lang.Class#getName} and has the key cons.N.sig : <pre>
 * cons.N.sig=int,java.lang.Object
 * cons.N.param.1=The int parameter
 * cons.N.param.2=The Object parameter
 * </pre>
 * <p/>
 * <H2>Operations</H2>
 * <H3>No overloading</H3>
 * When no overloaded versions of an operation exist (same method name but different parameters)
 * the simplest case shown below can be used : <pre>
 * op.<I>operationName</I>=The description
 * op.<I>operationName</I>.param.1=The first parameter
 * op.<I>operationName</I>.paramName.1=param1
 * </pre>
 * <p/>
 * <H3>Non ambiguous overloading case</H3>
 * When operation overloading is used but the overloaded versions differ in the number
 * of parameters the format below can be used : <pre>
 * op.<I>operationName</I>.1=The first version of operationName
 * op.<I>operationName</I>.1.param.1=parameter for first version
 * op.<I>operationName</I>.1.paramName.1=param1
 * op.<I>operationName</I>.2=The second version of operationName
 * op.<I>operationName</I>.2.paramName.1=param1
 * op.<I>operationName</I>.2.param.1=first parameter for second version
 * op.<I>operationName</I>.2.param.2=second parameter for second version
 * op.<I>operationName</I>.2.paramName.2=param2
 * </pre>
 * <p/>
 * <H3>Ambiguous overloading case</H3>
 * When operations with the same name have the same number of arguments an explicit
 * signature must be used : <pre>
 * op.<I>operationName</I>.1.sig=int
 * op.<I>operationName</I>.1=The first version of operationName (takes int)
 * op.<I>operationName</I>.1.param.1=parameter for first version
 * op.<I>operationName</I>.1.paramName.1=param1
 * op.<I>operationName</I>.2.sig=java.lang.Object
 * op.<I>operationName</I>.2=The second version of operationName (take Object)
 * op.<I>operationName</I>.2.paramName.1=param1
 * op.<I>operationName</I>.2.param.1=first parameter for second version
 * </pre>
 * <p/>
 * <H2>Restrictions</H2>
 * Parameter names must only contain characters allowed in a Java identifier
 * (in particular spaces are <b>not</b> allowed). This is required by the JMX specifications.
 * No such restrictions exist for the other descriptions.
 * <p/>
 * <H2>Behaviour with missing data</H2>
 * If no resource bunde exists for the MBean a java.util.MissingResourceException
 * will be thrown by the constructor. <p>
 * <p/>
 * If the resouce bundle is found but the bean description, constructor description or
 * parameter name is missing the String "??(key)" will be returned instead (eg
 * "??(op.myOperation)". <p>
 * <p/>
 * If a paramName key is missing (for constructor or operation) the version normally
 * given by StandardMBean is used (generally "pN").<p>
 * <p/>
 * If a non ambiguous description cannot be determined the fixed (non translatable)
 * descriptions "ambiguous constructor", "parameter for ambiguous constructor",
 * "ambiguous operation", "parameter for ambiguous operation" are returned.
 */
public class I18NStandardMBean extends StandardMBean
{
   private static final String IDPROP_DEFAULT_LOCALE =
           "mx4j.descriptionLocale";
   private static final String RESOURCE_SUFFIX = "MBeanResources";
   private static final String KEY_DESCR = "descr";
   private static final String KEY_CONS = "cons";
   private static final String KEY_ATTR = "attr";
   private static final String KEY_OP = "op";
   private static final String KEY_PARAM = "param";
   private static final String KEY_PARAM_NAME = "paramName";
   private static final String KEY_SIG = "sig";

   private static Locale g_defaultLocale = null;

   private NestedResourceBundle m_bundle;
   private Map m_mapConstructorSignatureToResourceIndex;
   private Map m_mapConstructorParamCountToResourceIndex;
   private Map m_mapConstructorToResourceIndex = new HashMap();
   private Map m_mapOperationNameToSignatures = new HashMap();
   private Map m_mapOperationNameToParamCounts = new HashMap();
   private Set m_setAmbiguousConstructors = new HashSet();
   private Set m_setAmbiguousOperations = new HashSet();

   /**
    * Makes an I18NStandardMBean for the default locale with a separate implementation class.
    *
    * @see javax.management.StandardMBean#StandardMBean(java.lang.Object, java.lang.Class)
    */
   public I18NStandardMBean(Object implementation, Class mbeanInterface)
           throws NotCompliantMBeanException
   {
      this(implementation, mbeanInterface, null);
   }

   /**
    * Makes an I18NStandardMBean for the given locale with a separate implementation class.
    *
    * @see javax.management.StandardMBean#StandardMBean(java.lang.Object, java.lang.Class)
    */
   public I18NStandardMBean(Object implementation,
                            Class mbeanInterface,
                            Locale locale)
           throws NotCompliantMBeanException
   {
      super(implementation, mbeanInterface);
      setupBundle(implementation, locale);
   }

   /**
    * Makes a I18NStandardMBean for the default locale implemented by a subclass.
    *
    * @see javax.management.StandardMBean#StandardMBean(java.lang.Class)
    */
   protected I18NStandardMBean(Class mbeanInterface)
           throws NotCompliantMBeanException
   {
      super(mbeanInterface);
      setupBundle(this, null);
   }

   /**
    * Makes a I18NStandardMBean for the given locale implemented by a subclass.
    *
    * @see javax.management.StandardMBean#StandardMBean(java.lang.Class)
    */
   protected I18NStandardMBean(Class mbeanInterface, Locale locale)
           throws NotCompliantMBeanException
   {
      super(mbeanInterface);
      setupBundle(this, locale);
   }

   private void setupBundle(Object implementation, Locale locale)
   {
      // calculate the effective locale:
      if (locale == null)
      {
         locale = g_defaultLocale;
      }
      if (locale == null)
      {
         locale = getLocaleFromSystemProperties();
      }

      // create bundle
      NestedResourceBundle cur = null;
      MissingResourceException ex = null;
      for (Class c = implementation.getClass(); c != null; c = c.getSuperclass())
      {
         String bundleName = c.getName() + RESOURCE_SUFFIX;
         try
         {
            ResourceBundle b = ResourceBundle.getBundle(bundleName, locale);
            NestedResourceBundle nb = new NestedResourceBundle(b);
            if (cur == null)
            {
               m_bundle = nb;
            }
            else
            {
               cur.setParent(nb);
            }
            cur = nb;
         }
         catch (MissingResourceException e)
         {
            if (m_bundle == null) ex = e; // save first exception
         }
      }
      if (m_bundle == null)
      {
         ex.fillInStackTrace();
         throw ex;
      }
   }

   // Obtain the default locale from system properties
   private Locale getLocaleFromSystemProperties()
   {
      Locale locale = Locale.getDefault();
      String stdLocale = System.getProperty(IDPROP_DEFAULT_LOCALE);
      if (stdLocale != null && stdLocale.length() > 0)
      {
         StringTokenizer st = new StringTokenizer(stdLocale, "_");
         switch (st.countTokens())
         {
            case 2:
               locale = new Locale(st.nextToken(), st.nextToken());
               break;
            case 3:
               locale =
               new Locale(st.nextToken(),
                          st.nextToken(),
                          st.nextToken());
               break;
            default :
               throw new IllegalArgumentException("Invalid locale in "
                                                  + IDPROP_DEFAULT_LOCALE
                                                  + ":"
                                                  + stdLocale);
         }
      }
      return locale;
   }

   /**
    * Set the locale which will be used for future I18NStandardMBeans. <p>
    * The locale specified can be overridden on a per class basis via the
    * constructors but overrides other means of setting the Locale (system properties). <p>
    * <p/>
    * Changing the locale has no effect on already constructed MBeans.
    *
    * @param locale the Locale for future MBeans
    */
   public static void setDefaultLocale(Locale locale)
   {
      g_defaultLocale = locale;
   }

   /**
    * Initialise internal data structures. <p>
    * This method is always called first during getMBeanInfo processing.
    * We use this to avoid keeping all our internal Maps in memory too long.
    *
    * @see javax.management.StandardMBean#getCachedMBeanInfo
    */
   protected MBeanInfo getCachedMBeanInfo()
   {
      MBeanInfo info = super.getCachedMBeanInfo();
      if (info == null)
      {
         // only setup if we are going to be called!
         m_mapConstructorToResourceIndex = new HashMap();
         m_mapOperationNameToSignatures = new HashMap();
         m_mapOperationNameToParamCounts = new HashMap();
         m_setAmbiguousConstructors = new HashSet();
         m_setAmbiguousOperations = new HashSet();
         m_mapConstructorSignatureToResourceIndex =
         getSignatureMap(KEY_CONS);
         m_mapConstructorParamCountToResourceIndex =
         getParamCountMap(KEY_CONS);
      }
      return info;
   }

   /**
    * Once the MBeanInfo has been obtained discard our caches.
    *
    * @see javax.management.StandardMBean#cacheMBeanInfo(javax.management.MBeanInfo)
    */
   protected void cacheMBeanInfo(MBeanInfo info)
   {
      super.cacheMBeanInfo(info);
      m_mapConstructorToResourceIndex = null;
      m_mapOperationNameToSignatures = null;
      m_mapOperationNameToParamCounts = null;
      m_setAmbiguousConstructors = null;
      m_setAmbiguousOperations = null;
      m_mapConstructorSignatureToResourceIndex = null;
      m_mapConstructorParamCountToResourceIndex = null;
   }

   /*
    * Initialises internal structures based on available constructors. <p>
    * Return value is supplied by superclass.<p>
    *
    * For all the constructors :<ul>
    * <li>Create a map of MBeanConstructorInfo=>resource bundle index for explicit signatures
    * <li>Create list of "ambiguous" constructors based on number of arguments.
    * </ul>
    * Note we assume that this metthod will be called BEFORE the constructor related
    * getDesciption methods. The spec does not say anything about this.
    */
   protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] cstrs,
                                                    Object impl)
   {
      Map argCountToCstr = new HashMap();
      for (int i = 0; i < cstrs.length; i++)
      {
         MBeanConstructorInfo ci = cstrs[i];
         MBeanParameterInfo[] params = ci.getSignature();

         // update potentially ambiguous constructors (same number of arguments)
         Integer count = new Integer(params.length);
         Object first = argCountToCstr.get(count);
         if (first != null)
         {
            // already have a constructor with this number of args
            m_setAmbiguousConstructors.add(first); // Set so no duplication
            m_setAmbiguousConstructors.add(ci);
            // this one is ambiguous too
         }
         else
         {
            argCountToCstr.put(count, ci);
         }

         // update signature=>resource index mapping (if explicit signature provided)
         String sig = makeSignatureString(params);
         Integer idx =
                 (Integer)m_mapConstructorSignatureToResourceIndex.get(sig);
         if (idx != null)
         {
            m_mapConstructorToResourceIndex.put(ci, idx);
         }
      }
      return super.getConstructors(cstrs, impl);
   }

   /**
    * Obtain global description for MBean. <p>
    * Taken from "descr" key in resource bundle. <p>
    * <p/>
    * Also performs internal initialisations requiring the MBeanInfo obtained
    * by introspection. Therefore the superclass must call this method BEFORE
    * the other hooks.
    *
    * @see javax.management.StandardMBean#getDescription(javax.management.MBeanInfo)
    */
   protected String getDescription(MBeanInfo info)
   {
      findAmbiguousOperations(info); // assume called first
      return getValueFromBundle(KEY_DESCR);
   }

   /**
    * Obtain the constructor description. <p>
    * Taken from "cons.N" key in resource bundle. <p>
    * <p/>
    * Maybe "ambiguous constructor" if correct index cannot be determined by
    * an explicit signature or parameter counts.
    *
    * @see javax.management.StandardMBean#getDescription(javax.management.MBeanConstructorInfo)
    */
   protected String getDescription(MBeanConstructorInfo cstr)
   {
      int idx = getConstructorIndex(cstr);
      if (idx < 1)
      {
         return "ambiguous constructor";
      }
      return getValueFromBundle(KEY_CONS + "." + idx);
   }

   /**
    * Obtain the constructor parameter description. <p>
    * Taken from "cons.N.param.<I>seq</I>" key in resource bundle. <p>
    * <p/>
    * Maybe "parameter for ambiguous constructor" if correct index cannot be determined by
    * an explicit signature or parameter counts.
    *
    * @see javax.management.StandardMBean#getDescription(javax.management.MBeanConstructorInfo, javax.management.MBeanParameterInfo, int)
    */
   protected String getDescription(MBeanConstructorInfo cstr,
                                   MBeanParameterInfo param,
                                   int seq)
   {
      int idx = getConstructorIndex(cstr);
      if (idx < 1)
      {
         return "parameter for ambiguous constructor";
      }
      return getValueFromBundle(KEY_CONS + "." + idx + ".param." + (seq + 1));
   }

   /**
    * Obtain constructor parameter name. <p>
    * Taken from "cons.N.paramName.<I>seq</I>" key in resource bundle. <p>
    * <p/>
    * If this key does not exist or if the correct index N cannot be determined by
    * an explicit signature or parameter counts the superclass method is called.
    *
    * @see javax.management.StandardMBean#getParameterName(javax.management.MBeanConstructorInfo, javax.management.MBeanParameterInfo, int)
    */
   protected String getParameterName(MBeanConstructorInfo cstr,
                                     MBeanParameterInfo param,
                                     int seq)
   {
      int idx = getConstructorIndex(cstr);
      String name = null;
      if (idx >= 1)
      {
         name =
         getValueOrNullFromBundle(KEY_CONS + "." + idx + ".paramName." + (seq + 1));
      }
      if (name == null)
      {
         name = super.getParameterName(cstr, param, seq);
      }
      return name;
   }

   /**
    * Obtain the attribute description. <p>
    * Taken from the "attr.<I>attributeName</I>" key in resource bundle.
    *
    * @see javax.management.StandardMBean#getDescription(javax.management.MBeanAttributeInfo)
    */
   protected String getDescription(MBeanAttributeInfo attr)
   {
      return getValueFromBundle(KEY_ATTR + "." + attr.getName());
   }

   /**
    * Obtain the operation description. <p>
    * Taken from the "op.<I>operationName</I>.N" or the "op.<I>operationName</I>"
    * key in the resource bundle. <p>
    * May be "ambiguous operation" if the correct key cannot be determined by
    * signature or parameter counts.
    *
    * @see javax.management.StandardMBean#getDescription(javax.management.MBeanOperationInfo)
    */
   protected String getDescription(MBeanOperationInfo op)
   {
      try
      {
         return getValueFromBundle(getOperationKey(op));
      }
      catch (IllegalStateException e)
      {
         return e.getMessage();
      }
   }

   /**
    * Obtain the operation parameter description. <p>
    * Taken from the "op.<I>operationName</I>.N.param.M" or the "op.<I>operationName</I>.param"
    * key in the resource bundle. <p>
    * May be "parameter for ambiguous operation" if the correct key cannot be determined by
    * signature or parameter counts.
    *
    * @see javax.management.StandardMBean#getDescription(javax.management.MBeanOperationInfo, javax.management.MBeanParameterInfo, int)
    */
   protected String getDescription(MBeanOperationInfo op,
                                   MBeanParameterInfo param,
                                   int seq)
   {
      try
      {
         return getValueFromBundle(getOperationKey(op) + "." + KEY_PARAM + "." + (seq + 1));
      }
      catch (IllegalStateException e)
      {
         return "parameter for " + e.getMessage();
      }
   }

   /**
    * Obtain operation parameter name. <p>
    * Taken from the "op.<I>operationName</I>.N.paramName.M" or the "op.<I>operationName.paramName</I>.M"
    * key in the resource bundle. <p>
    * <p/>
    * If this key does not exist or if the correct index N cannot be determined by
    * an explicit signature or parameter counts the superclass method is called.
    *
    * @see javax.management.StandardMBean#getParameterName(javax.management.MBeanOperationInfo, javax.management.MBeanParameterInfo, int)
    */
   protected String getParameterName(MBeanOperationInfo op,
                                     MBeanParameterInfo param,
                                     int seq)
   {
      String name = null;
      try
      {
         name =
         getValueOrNullFromBundle(getOperationKey(op)
                                  + "."
                                  + KEY_PARAM_NAME
                                  + "."
                                  + (seq + 1));
      }
      catch (IllegalStateException e)
      {
      }

      if (name == null)
      {
         name = super.getParameterName(op, param, seq);
      }
      return name;
   }

   /*
    * Obtain 1 based index of constructor in resource bundle.
    * First look for a signature match (.sig in bundle)
    * If not found and constuctor is potentially ambiguous (another constructor with the same number of params exists) return -1
    * If not found try a parameter number match.
    * If parameter number match is ambiguous return -1
    * If no match found return 0
    */
   private int getConstructorIndex(MBeanConstructorInfo cons)
   {
      Integer idx = (Integer)m_mapConstructorToResourceIndex.get(cons);
      if (idx != null)
      {
         return idx.intValue();
      }

      // do multiple constuctors with the same arg count exist?
      if (m_setAmbiguousConstructors.contains(cons))
         return -1;

      // no signature match - try using parameter count
      int nbParams = cons.getSignature().length;
      idx =
      (Integer)m_mapConstructorParamCountToResourceIndex.get(new Integer(nbParams));
      if (idx != null)
      {
         return idx.intValue();
      }
      return 0;
   }

   /*
    * Obtain the root bundle key for the given operation.
    * If a matching signature entry exists for this operation
    * is of form : "op.operationName.N"
    * otherwise it is of form "op.operationName"
    * where N is the index of the matching signature.
    * If the operation is ambiguous throw an IllegalStateException.
    */
   private String getOperationKey(MBeanOperationInfo op)
   {
      String operationName = op.getName();

      // lookup by signature
      Map sigMap = getOperationSignatureMap(operationName);
      MBeanParameterInfo[] params = op.getSignature();
      String sig = makeSignatureString(params);
      Integer idx = (Integer)sigMap.get(sig);

      StringBuffer sbRet = new StringBuffer(KEY_OP + ".");
      sbRet.append(operationName);

      if (idx == null)
      {
         if (m_setAmbiguousOperations.contains(op))
         {
            throw new IllegalStateException("ambiguous operation");
         }

         // no direct signature mapping, try matching by parameter counts
         Map countMap = getOperationParamCountMap(operationName);
         idx = (Integer)countMap.get(new Integer(params.length));
         if (idx != null && idx.intValue() < 1)
         {
            throw new IllegalStateException("ambiguous operation");
         }
      }

      if (idx != null)
      {
         sbRet.append(".");
         sbRet.append(idx);
      }
      return sbRet.toString();
   }

   /*
    * Initialise the set m_setAmbiguousOperations with those operations
    * that have the same name and same number of parameters.
    */
   private void findAmbiguousOperations(MBeanInfo info)
   {
      // obtain potentially ambiguous operations (same name, same number parameters)
      MBeanOperationInfo[] ops = info.getOperations();
      Map mapNameToArgCountMap = new HashMap();
      for (int i = 0; i < ops.length; i++)
      {
         MBeanOperationInfo op = ops[i];
         String name = op.getName();
         Map argCountToOp = (Map)mapNameToArgCountMap.get(name);
         if (argCountToOp == null)
         {
            argCountToOp = new HashMap();
            mapNameToArgCountMap.put(name, argCountToOp);
         }

         Integer count = new Integer(op.getSignature().length);
         Object first = argCountToOp.get(count);
         if (first != null)
         {
            // already have an operation with this number of args
            m_setAmbiguousOperations.add(first); // Set so no duplication
            m_setAmbiguousOperations.add(op); // this one is ambiguous too
         }
         else
         {
            argCountToOp.put(count, op);
         }
      }
   }

   /*
    * Obtain Map of operation signature=>resource bundle index.
    * Use lazy instantiation and caching.
    * The entries in resource bundle have form :
    * op.operationName.1.sig=xxx
    * op.operationName.2.sig=yyy
    *
    * The above example would give xxx=>1, yyy=>2
    */
   private Map getOperationSignatureMap(String operationName)
   {
      // look up in cache
      Map m = (Map)m_mapOperationNameToSignatures.get(operationName);
      if (m != null)
      {
         return m;
      }

      // construct map
      m = getSignatureMap(KEY_OP + "." + operationName);
      m_mapOperationNameToSignatures.put(operationName, m); // cache
      return m;
   }

   /*
    * Obtain Map of parameter count =>resource bundle index for the given operation
    * Use lazy instantiation and caching.
    */
   private Map getOperationParamCountMap(String operationName)
   {
      // look up in cache
      Map m = (Map)m_mapOperationNameToParamCounts.get(operationName);
      if (m != null)
      {
         return m;
      }

      // construct map
      m = getParamCountMap(KEY_OP + "." + operationName);
      m_mapOperationNameToParamCounts.put(operationName, m); // cache
      return m;
   }

   /*
    * Obtain a Map of parameter count => Integer index from resource bundle
    * The entries in resource bundle have form :
    * prefix.1.param.1=xxx
    * prefix.1.param.2=yyy
    * prefix.2.param.1=zzz
    *
    * The above example would give 2=>1, 1=>2 (index1 has 2 parameter, index 2 has 1 parameter)
    * If there are duplicate parameter counts map to -1
   */
   private Map getParamCountMap(String prefix)
   {
      int nb;
      Map m = new HashMap();

      for (int i = 1; ; i++)
      {
         String key = prefix + "." + i;
         String sig = getValueOrNullFromBundle(key);
         if (sig == null)
         {
            break;
         }
         nb = 0;
         for (int j = 1; ; j++)
         {
            key = prefix + "." + i + "." + KEY_PARAM + "." + j;
            if (getValueOrNullFromBundle(key) != null)
            {
               nb = j;
            }
            else
            {
               break;
            }
         }
         Integer nbObj = new Integer(nb);
         int idx = m.containsKey(nbObj) ? -1 : i;
         m.put(nbObj, new Integer(idx));
      }
      return m;
   }

   /*
    * Create a map of signature string=>Integer index from resource bundle.
    * The entries in resource bundle have form :
    * prefix.1.sig=signature1
    *  prefix.2.sig=signature2
    * ..
    * The list stops at the first non existant index.
    * The signatures are comma separated types of the form returned by
    * Class.getName(), eg: java.lang.Object,Z,[Z;
    */
   private Map getSignatureMap(String prefix)
   {
      Map m = new HashMap();
      for (int i = 1; ; i++)
      {
         String key = prefix + "." + i + "." + KEY_SIG;
         String sig = getValueOrNullFromBundle(key);
         if (sig == null)
         {
            break;
         }
         m.put(sig, new Integer(i));
      }
      return m;
   }

   // create a comma separated list of signatures.
   private String makeSignatureString(MBeanParameterInfo[] params)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < params.length; i++)
      {
         if (i > 0)
         {
            sb.append(",");
         }
         sb.append(params[i].getType());
      }
      return sb.toString();
   }

   private String getValueFromBundle(String key)
   {
      String value;
      try
      {
         value = m_bundle.getString(key);
      }
      catch (MissingResourceException e)
      {
         value = "??(" + key + ")";
      }
      return value;
   }

   private String getValueOrNullFromBundle(String key)
   {
      String value = null;
      try
      {
         value = m_bundle.getString(key);
      }
      catch (MissingResourceException e)
      {
      }
      return value;
   }

   private static class NestedResourceBundle extends ResourceBundle
   {
      private ResourceBundle _impl;

      NestedResourceBundle(ResourceBundle impl)
      {
         _impl = impl;
      }

      void setParent(NestedResourceBundle parent)
      {
         super.setParent(parent);
      }

      /* (non-Javadoc)
       * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
       */
      protected Object handleGetObject(String key)
      {
         try
         {
            return _impl.getString(key);
         }
         catch (MissingResourceException e)
         {
            return null; // Resource bundle will ask parent
         }
      }

      /* (non-Javadoc)
       * @see java.util.ResourceBundle#getKeys()
       */
      public Enumeration getKeys()
      {
         // obtain union of all keys in bundle hierachy (no doublons)
         HashSet hs = new HashSet();
         addEnumeration(hs, _impl.getKeys());
         if (parent != null)
         {
            addEnumeration(hs, parent.getKeys());
         }
         return Collections.enumeration(hs);
      }

      private void addEnumeration(Collection col, Enumeration e)
      {
         while (e.hasMoreElements())
         {
            col.add(e.nextElement());
         }
      }

   }

}
