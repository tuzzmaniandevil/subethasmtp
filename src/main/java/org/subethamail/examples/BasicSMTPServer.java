package org.subethamail.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.*;
import org.subethamail.smtp.server.SMTPServer;

/**
 * An example of a basic SMTP Server
 *
 * @author dylan
 */
public class BasicSMTPServer {

    private final static Logger log = LoggerFactory.getLogger(BasicSMTPServer.class);

    static int defaultListenPort = 25000;

    public static void main(String[] args) {
        new BasicSMTPServer().start(defaultListenPort);
        log.info("Server running!");
    }

    void start(int listenPort) {
        BasicMessageHandlerFactory myFactory = new BasicMessageHandlerFactory();
        SMTPServer smtpServer = new SMTPServer(myFactory);
        smtpServer.setPort(listenPort);
        log.info("Starting Basic SMTP Server on port " + listenPort + "...");
        smtpServer.start();
    }

    public class BasicMessageHandlerFactory implements MessageHandlerFactory {

        @Override
        public MessageHandler create(MessageContext ctx) {
            return new Handler(ctx);
        }

        class Handler implements MessageHandler {

            MessageContext ctx;

            public Handler(MessageContext ctx) {
                this.ctx = ctx;
            }

            @Override
            public void from(String from) throws RejectException {
                log.info("FROM:" + from);
            }

            @Override
            public void recipient(String recipient) throws RejectException {
                log.info("RECIPIENT:" + recipient);
            }

            @Override
            public void data(InputStream data) throws IOException {
                log.info("MAIL DATA");
                log.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
                log.info(this.convertStreamToString(data));
                log.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
            }

            @Override
            public void done() {
                log.info("Finished");
            }

            public String convertStreamToString(InputStream is) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch (IOException e) {
                    log.error("Error converting stream to text: {}", e.getMessage(), e);
                }
                return sb.toString();
            }

        }
    }

}
