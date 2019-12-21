/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.remote.caucho;

import java.io.IOException;

/**
 * @version $
 */
public interface CauchoInput
{
   public void startCall() throws IOException;

   public void completeCall() throws IOException;

   public String readHeader() throws IOException;

   public String readMethod() throws IOException;

   /**
    * Reads and returns an object of the given class,
    * or a generic object if the class is null.
    */
   public Object readObject(Class cls) throws IOException;

   /**
    * Starts reading a reply of a previous call; if the call threw an exception,
    * the exception is read and re-thrown, otherwise the result of the call
    * can be read using {@link #readObject}.
    */
   public void startReply() throws Exception;

   public void completeReply() throws IOException;
}
