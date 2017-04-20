package org.subethamail.smtp.command;

import java.io.IOException;
import org.subethamail.smtp.server.BaseCommand;
import org.subethamail.smtp.server.Session;

/**
 *
 * @author Michele Zuccala &lt;zuccala.m@gmail.com&gt;
 */
public class ExpandCommand extends BaseCommand {

    /**
     *
     */
    public ExpandCommand() {
        super("EXPN", "The expn command.");
    }

    /**
     *
     * @param sess
     * @throws java.io.IOException
     */
    @Override
    public void execute(String commandString, Session sess) throws IOException {
        sess.sendResponse("502 EXPN command is disabled");
    }
}
