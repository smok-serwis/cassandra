/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package mx4j.tools.adaptor.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * InvokeOperationCommandProcessor, processes a request for unregistering an MBean
 *
 * @version $Revision: 1.3 $
 */
public class InvokeOperationCommandProcessor extends HttpCommandProcessorAdaptor
{
   public InvokeOperationCommandProcessor()
   {
   }

   public Document executeRequest(HttpInputStream in) throws IOException, JMException
   {
      Document document = builder.newDocument();

      Element root = document.createElement("MBeanOperation");
      document.appendChild(root);
      Element operationElement = document.createElement("Operation");
      operationElement.setAttribute("operation", "invoke");
      root.appendChild(operationElement);

      String objectVariable = in.getVariable("objectname");
      String operationVariable = in.getVariable("operation");
      if (objectVariable == null || objectVariable.equals("")
          || operationVariable == null || operationVariable.equals(""))
      {
         operationElement.setAttribute("result", "error");
         operationElement.setAttribute("errorMsg", "Incorrect parameters in the request");
         return document;
      }
      operationElement.setAttribute("objectname", objectVariable);
      List types = new ArrayList();
      List values = new ArrayList();
      int i = 0;
      boolean unmatchedParameters = false;
      boolean valid = false;
      do
      {
         String parameterType = in.getVariable("type" + i);
         String parameterValue = in.getVariable("value" + i);
         valid = (parameterType != null && parameterValue != null);
         if (valid)
         {
            types.add(parameterType);
            Object value = null;
            try
            {
               value = CommandProcessorUtil.createParameterValue(parameterType, parameterValue);
            }

            catch (Exception e)
            {
               operationElement.setAttribute("result", "error");
               operationElement.setAttribute("errorMsg", "Parameter " + i + ": " + parameterValue + " cannot be converted to type " + parameterType);
               return document;
            }
            if (value != null)
            {
               values.add(value);
            }
         }
         if (parameterType == null ^ parameterValue == null)
         {
            unmatchedParameters = true;
            break;
         }
         i++;
      }
      while (valid);
      if (objectVariable == null || objectVariable.equals("") ||
          operationVariable == null || operationVariable.equals(""))
      {
         operationElement.setAttribute("result", "error");
         operationElement.setAttribute("errorMsg", "Incorrect parameters in the request");
         return document;
      }
      if (unmatchedParameters)
      {
         operationElement.setAttribute("result", "error");
         operationElement.setAttribute("errorMsg", "count of parameter types doesn't match count of parameter values");
         return document;
      }
      ObjectName name = null;
      try
      {
         name = new ObjectName(objectVariable);
      }
      catch (MalformedObjectNameException e)
      {
         operationElement.setAttribute("result", "error");
         operationElement.setAttribute("errorMsg", "Malformed object name");
         return document;
      }

      if (server.isRegistered(name))
      {
         MBeanInfo info = server.getMBeanInfo(name);
         MBeanOperationInfo[] operations = info.getOperations();
         boolean match = false;
         if (operations != null)
         {
            for (int j = 0; j < operations.length; j++)
            {
               if (operations[j].getName().equals(operationVariable))
               {
                  MBeanParameterInfo[] parameters = operations[j].getSignature();
                  if (parameters.length != types.size())
                  {
                     continue;
                  }
                  Iterator k = types.iterator();
                  boolean signatureMatch = true;
                  for (int p = 0; p < types.size(); p++)
                  {
                     if (!parameters[p].getType().equals(k.next()))
                     {
                        signatureMatch = false;
                        break;
                     }
                  }
                  match = signatureMatch;
               }
               if (match)
               {
                  break;
               }
            }
         }
         if (!match)
         {
            operationElement.setAttribute("result", "error");
            operationElement.setAttribute("errorMsg", "Operation singature has no match in the MBean");
         }
         else
         {
            try
            {
               Object[] params = values.toArray();
               String[] signature = new String[types.size()];
               types.toArray(signature);
               Object returnValue = server.invoke(name, operationVariable, params, signature);
               operationElement.setAttribute("result", "success");
               if (returnValue != null)
               {
                  operationElement.setAttribute("returnclass", returnValue.getClass().getName());
                  operationElement.setAttribute("return", returnValue.toString());
               }
               else
               {
                  operationElement.setAttribute("returnclass", null);
                  operationElement.setAttribute("return", null);
               }
            }
            catch (Exception e)
            {
               operationElement.setAttribute("result", "error");
               operationElement.setAttribute("errorMsg", e.getMessage());
            }
         }
      }
      else
      {
         if (name != null)
         {
            operationElement.setAttribute("result", "error");
            operationElement.setAttribute("errorMsg", new StringBuffer("MBean ").append(name).append(" not registered").toString());
         }
      }
      return document;
   }

}
