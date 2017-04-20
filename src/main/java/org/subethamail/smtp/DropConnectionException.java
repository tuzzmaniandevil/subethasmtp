/*
 * $Id: RejectException.java 337 2009-06-29 19:20:58Z latchkey $
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.smtp;

/**
 * A type of RejectException that additionally causes the server to close the
 * connection to the client.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class DropConnectionException extends RejectException {

    /**
     * Constructs a new exception with the detail message "Transaction failed"
     * and the SMTP error code 554
     */
    public DropConnectionException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message and the SMTP
     * error code 554
     *
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public DropConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and the
     * specified SMTP error code.
     *
     * @param code SMTP specific error code
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public DropConnectionException(int code, String message) {
        super(code, message);
    }
}
