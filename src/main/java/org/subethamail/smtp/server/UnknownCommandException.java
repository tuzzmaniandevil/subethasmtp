package org.subethamail.smtp.server;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 */
@SuppressWarnings("serial")
public class UnknownCommandException extends CommandException {

    /**
     *
     */
    public UnknownCommandException() {
        super();
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.String) CommandException}
     *
     * @param message
     */
    public UnknownCommandException(String message) {
        super(message);
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.String, java.lang.Throwable) CommandException}
     *
     * @param message
     * @param cause
     */
    public UnknownCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.Throwable) CommandException}
     *
     * @param cause
     */
    public UnknownCommandException(Throwable cause) {
        super(cause);
    }
}
