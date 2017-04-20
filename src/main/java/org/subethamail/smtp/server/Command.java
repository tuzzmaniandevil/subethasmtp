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
     *
     * @param commandString
     * @param sess
     * @throws java.io.IOException
     * @throws org.subethamail.smtp.DropConnectionException
     */
    public void execute(String commandString, Session sess) throws IOException,
            DropConnectionException;

    /**
     *
     * @return @throws org.subethamail.smtp.server.CommandException
     */
    public HelpMessage getHelp() throws CommandException;

    /**
     * Returns the name of the command in upper case. For example "QUIT".
     *
     * @return
     */
    public String getName();
}
