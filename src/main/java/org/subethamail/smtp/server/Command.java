package org.subethamail.smtp.server;

import java.io.IOException;
import org.subethamail.smtp.DropConnectionException;

/**
 * Describes a SMTP command
 *
 * @author Jon Stevens
 * @author Scott Hernandez
 */
public interface Command {

    /**
     * This is the main method that you need to override in order to implement a
     * command.
     *
     * @param commandString string to execute
     * @param sess Mail Session
     * @throws java.io.IOException Signals that an I/O exception of some sort
     * has occurred
     * @throws org.subethamail.smtp.DropConnectionException A type of
     * RejectException that additionally causes the server to close the
     * connection to the client.
     */
    public void execute(String commandString, Session sess) throws IOException,
            DropConnectionException;

    /**
     *
     * @return an instance of {@link HelpMessage}
     * @throws org.subethamail.smtp.server.CommandException Signals that there
     * was an error processing the command
     */
    public HelpMessage getHelp() throws CommandException;

    /**
     * Returns the name of the command in upper case. For example "QUIT".
     *
     * @return the command name in upper case
     */
    public String getName();
}
