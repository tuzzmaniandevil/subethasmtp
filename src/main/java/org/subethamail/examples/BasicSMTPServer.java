package org.subethamail.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.subethamail.smtp.*;
import org.subethamail.smtp.server.SMTPServer;

public class BasicSMTPServer {

    static int defaultListenPort = 25000;

    public static void main(String[] args) {
        new BasicSMTPServer().start(defaultListenPort);
        System.out.println("Server running!");
    }

    void start(int listenPort) {
        BasicMessageHandlerFactory myFactory = new BasicMessageHandlerFactory();
        SMTPServer smtpServer = new SMTPServer(myFactory);
        smtpServer.setPort(listenPort);
        System.out.println("Starting Basic SMTP Server on port " + listenPort + "...");
        smtpServer.start();
    }

    public class BasicMessageHandlerFactory implements MessageHandlerFactory {

        public MessageHandler create(MessageContext ctx) {
            return new Handler(ctx);
        }

        class Handler implements MessageHandler {

            MessageContext ctx;

            public Handler(MessageContext ctx) {
                this.ctx = ctx;
            }

            public void from(String from) throws RejectException {
                System.out.println("FROM:" + from);
            }

            public void recipient(String recipient) throws RejectException {
                System.out.println("RECIPIENT:" + recipient);
            }

            public void data(InputStream data) throws IOException {
                System.out.println("MAIL DATA");
                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
                System.out.println(this.convertStreamToString(data));
                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
            }

            public void done() {
                System.out.println("Finished");
            }

            public String convertStreamToString(InputStream is) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return sb.toString();
            }

        }
    }

}
