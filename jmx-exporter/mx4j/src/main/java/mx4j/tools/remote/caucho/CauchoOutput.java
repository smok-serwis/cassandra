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
public interface CauchoOutput
{
   public void startCall() throws IOException;

   public void completeCall() throws IOException;

   public void startReply() throws IOException;

   public void completeReply() throws IOException;

   public void writeHeader(String header) throws IOException;

   public void writeMethod(String methodName) throws IOException;

   public void writeObject(Object object) throws IOException;

   public void writeFault(Throwable fault) throws IOException;
}
