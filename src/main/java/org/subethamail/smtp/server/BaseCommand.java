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
     * @param name
     * @param help
     */
    protected BaseCommand(String name, String help) {
        this.name = name;
        this.helpMsg = new HelpMessage(name, help);
    }

    /**
     *
     * @param name
     * @param help
     * @param argumentDescription
     */
    protected BaseCommand(String name, String help, String argumentDescription) {
        this.name = name;
        this.helpMsg = new HelpMessage(name, help, argumentDescription);
    }

    /**
     * This is the main method that you need to override in order to implement a
     * command.
     *
     * @param commandString
     * @param context
     * @throws java.io.IOException
     * @throws org.subethamail.smtp.DropConnectionException
     */
    @Override
    abstract public void execute(String commandString, Session context)
            throws IOException, DropConnectionException;

    /**
     *
     * @return
     */
    @Override
    public HelpMessage getHelp() {
        return this.helpMsg;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param commandString
     * @return
     */
    protected String getArgPredicate(String commandString) {
        if (commandString == null || commandString.length() < 4) {
            return "";
        }

        return commandString.substring(4).trim();
    }

    /**
     *
     * @param commandString
     * @return
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
