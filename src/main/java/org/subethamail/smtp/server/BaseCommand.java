package org.subethamail.smtp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.DropConnectionException;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 * @author Jeff Schnitzer
 * @author Scott Hernandez
 */
abstract public class BaseCommand implements Command {

    @SuppressWarnings("unused")
    private final static Logger log = LoggerFactory.getLogger(BaseCommand.class);

    /**
     * Name of the command, ie HELO
     */
    private final String name;
    /**
     * The help message for this command
     */
    private final HelpMessage helpMsg;

    /**
     *
     * @param name name of the command. i.e. HELO or HELP
     * @param help the help text to describe this command
     */
    protected BaseCommand(String name, String help) {
        this.name = name;
        this.helpMsg = new HelpMessage(name, help);
    }

    /**
     *
     * @param name name of the command. i.e. HELO or HELP
     * @param help the help text to describe this command
     * @param argumentDescription describes accepted arguments
     */
    protected BaseCommand(String name, String help, String argumentDescription) {
        this.name = name;
        this.helpMsg = new HelpMessage(name, help, argumentDescription);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    abstract public void execute(String commandString, Session context)
            throws IOException, DropConnectionException;

    /**
     * {@inheritDoc }
     */
    @Override
    public HelpMessage getHelp() {
        return this.helpMsg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param commandString string to check
     * @return string trimmed to length
     */
    protected String getArgPredicate(String commandString) {
        if (commandString == null || commandString.length() < 4) {
            return "";
        }

        return commandString.substring(4).trim();
    }

    /**
     *
     * @param commandString string to check
     * @return string trimmed to length
     */
    protected String[] getArgs(String commandString) {
        List<String> strings = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(commandString);
        while (stringTokenizer.hasMoreTokens()) {
            strings.add(stringTokenizer.nextToken());
        }

        return strings.toArray(new String[strings.size()]);
    }
}
