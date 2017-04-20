/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.smtp;

import java.io.IOException;

/**
 * Thrown by message listeners if an input stream provides more data than the
 * listener can handle.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class TooMuchDataException extends IOException {

    /**
     * Constructs a new exception.
     */
    public TooMuchDataException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method)
     */
    public TooMuchDataException(String message) {
        super(message);
    }
}
