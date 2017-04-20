package org.subethamail.wiser;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * This class wraps a received message and provides a way to generate a JavaMail
 * MimeMessage from the data.
 *
 * @author Jon Stevens
 */
public class WiserMessage {

    byte[] messageData;
    Wiser wiser;
    String envelopeSender;
    String envelopeReceiver;

    WiserMessage(Wiser wiser, String envelopeSender, String envelopeReceiver, byte[] messageData) {
        this.wiser = wiser;
        this.envelopeSender = envelopeSender;
        this.envelopeReceiver = envelopeReceiver;
        this.messageData = messageData;
    }

    /**
     * Generate a JavaMail MimeMessage.
     *
     * @return parsed MimeMessage
     * @throws MessagingException if there is an error parsing the {@link
     * javax.mail.internet.MimeMessage}
     */
    public MimeMessage getMimeMessage() throws MessagingException {
        return new MimeMessage(this.wiser.getSession(), new ByteArrayInputStream(this.messageData));
    }

    /**
     * Get's the raw message DATA.
     *
     * @return raw data
     */
    public byte[] getData() {
        return this.messageData;
    }

    /**
     * Get's the RCPT TO:
     *
     * @return raw RCPT TO:
     */
    public String getEnvelopeReceiver() {
        return this.envelopeReceiver;
    }

    /**
     * Get's the MAIL FROM:
     *
     * @return raw MAIL FROM:
     */
    public String getEnvelopeSender() {
        return this.envelopeSender;
    }

    /**
     * Dumps the rough contents of the message for debugging purposes
     *
     * @param out PrintStream to write to
     * @throws javax.mail.MessagingException if there is an error writing email
     */
    public void dumpMessage(PrintStream out) throws MessagingException {
        out.println("===== Dumping message =====");

        out.println("Envelope sender: " + this.getEnvelopeSender());
        out.println("Envelope recipient: " + this.getEnvelopeReceiver());

        // It should all be convertible with ascii or utf8
        String content = new String(this.getData());
        out.println(content);

        out.println("===== End message dump =====");
    }

    /**
     * Implementation of toString()
     *
     * @return getData() as a string or an empty string if getData is null
     */
    @Override
    public String toString() {
        if (this.getData() == null) {
            return "";
        }

        return new String(this.getData());
    }
}
