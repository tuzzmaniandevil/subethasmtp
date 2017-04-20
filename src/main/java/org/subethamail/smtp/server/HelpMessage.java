package org.subethamail.smtp.server;

import java.util.StringTokenizer;

/**
 * @author Ian McFarland &lt;ian@neo.com&gt;
 * @author Jon Stevens
 */
public class HelpMessage {

    private String commandName;

    private String argumentDescription;

    private String helpMessage;

    private String outputString;

    /**
     *
     * @param commandName command name i.e. HELO or EHLO
     * @param helpMessage help message to display
     * @param argumentDescription describes accepted arguments
     */
    public HelpMessage(String commandName, String helpMessage, String argumentDescription) {
        this.commandName = commandName;
        this.argumentDescription = argumentDescription == null ? "" : " " + argumentDescription;
        this.helpMessage = helpMessage;
        this.buildOutputString();
    }

    /**
     *
     * @param commandName command name i.e. HELO or EHLO
     * @param helpMessage help message to display
     */
    public HelpMessage(String commandName, String helpMessage) {
        this(commandName, helpMessage, null);
    }

    /**
     *
     * @return name of the command
     */
    public String getName() {
        return this.commandName;
    }

    /**
     *
     * @return result to send to client
     */
    public String toOutputString() {
        return this.outputString;
    }

    private void buildOutputString() {
        StringTokenizer stringTokenizer = new StringTokenizer(this.helpMessage, "\n");
        StringBuilder stringBuilder = new StringBuilder().append("214-").append(this.commandName).append(this.argumentDescription);
        while (stringTokenizer.hasMoreTokens()) {
            stringBuilder.append("\n214-    ").append(stringTokenizer.nextToken());
        }

        stringBuilder.append("\n214 End of ").append(this.commandName).append(" info");
        this.outputString = stringBuilder.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final HelpMessage that = (HelpMessage) o;
        if (this.argumentDescription != null ? !this.argumentDescription.equals(that.argumentDescription)
                : that.argumentDescription != null) {
            return false;
        }
        if (this.commandName != null ? !this.commandName.equals(that.commandName)
                : that.commandName != null) {
            return false;
        }
        return !(this.helpMessage != null ? !this.helpMessage.equals(that.helpMessage)
                : that.helpMessage != null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int result;
        result = (this.commandName != null ? this.commandName.hashCode() : 0);
        result = 29
                * result
                + (this.argumentDescription != null ? this.argumentDescription.hashCode()
                        : 0);
        result = 29 * result
                + (this.helpMessage != null ? this.helpMessage.hashCode() : 0);
        return result;
    }
}
