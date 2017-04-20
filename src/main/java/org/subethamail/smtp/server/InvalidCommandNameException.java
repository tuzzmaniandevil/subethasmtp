package org.subethamail.smtp.server;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
@SuppressWarnings("serial")
public class InvalidCommandNameException extends CommandException {

    /**
     *
     * @see CommandException#CommandException()
     */
    public InvalidCommandNameException() {
        super();
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.String) CommandException}
     *
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public InvalidCommandNameException(String message) {
        super(message);
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.String, java.lang.Throwable) CommandException}
     *
     * @param message the detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public InvalidCommandNameException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.Throwable) CommandException}
     *
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown.)
     */
    public InvalidCommandNameException(Throwable cause) {
        super(cause);
    }
}
