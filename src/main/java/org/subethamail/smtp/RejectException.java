/*
 * $Id$
 * $Source: /cvsroot/Similarity4/src/java/com/similarity/mbean/BindStatisticsManagerMBean.java,v $
 */
package org.subethamail.smtp;

/**
 * Thrown to reject an SMTP command with a specific code.
 *
 * @author Jeff Schnitzer
 */
@SuppressWarnings("serial")
public class RejectException extends Exception {

    int code;

    /**
     * Constructs a new exception with the detail message "Transaction failed"
     * and the SMTP error code 554
     */
    public RejectException() {
        this("Transaction failed");
    }

    /**
     * Constructs a new exception with the specified detail message and the SMTP
     * error code 554
     *
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public RejectException(String message) {
        this(554, message);
    }

    /**
     * Constructs a new exception with the specified detail message and the
     * specified SMTP error code.
     *
     * @param code SMTP specific error code
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public RejectException(int code, String message) {
        super(message);

        this.code = code;
    }

    /**
     *
     */
    public int getCode() {
        return this.code;
    }

    /**
     *
     */
    public String getErrorResponse() {
        return this.code + " " + this.getMessage();
    }
}
