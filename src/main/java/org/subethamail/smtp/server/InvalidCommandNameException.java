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
     * @param message
     */
    public InvalidCommandNameException(String message) {
        super(message);
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.String, java.lang.Throwable) CommandException}
     *
     * @param message
     * @param cause
     */
    public InvalidCommandNameException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * See
     * {@link CommandException#CommandException(java.lang.Throwable) CommandException}
     *
     * @param cause
     */
    public InvalidCommandNameException(Throwable cause) {
        super(cause);
    }
}
