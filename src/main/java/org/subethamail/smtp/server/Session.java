package org.subethamail.smtp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.Certificate;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.DropConnectionException;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.io.CRLFTerminatedReader;

/**
 * The thread that handles a connection. This class passes most of it's
 * responsibilities off to the CommandHandler.
 *
 * @author Jon Stevens
 * @author Jeff Schnitzer
 */
public class Session implements Runnable, MessageContext {

    private final static Logger log = LoggerFactory.getLogger(Session.class);

    /**
     * A link to our parent server
     */
    private final SMTPServer server;

    /**
     * A link to our parent server thread, which must be notified when this
     * connection is finished.
     */
    private final ServerThread serverThread;

    /**
     * Saved SLF4J mapped diagnostic context of the parent thread. The parent
     * thread is the one which calls the constructor. MDC is usually inherited
     * by new threads, but this mechanism does not work with executors.
     */
    private final Map<String, String> parentLoggingMdcContext = MDC.getCopyOfContextMap();

    /**
     * Uniquely identifies this session within an extended time period, useful
     * for logging.
     */
    private String sessionId;

    /**
     * Set this true when doing an ordered shutdown
     */
    private volatile boolean quitting = false;

    /**
     * I/O to the client
     */
    private Socket socket;
    private InputStream input;
    private CRLFTerminatedReader reader;
    private PrintWriter writer;

    /**
     * Might exist if the client has successfully authenticated
     */
    private AuthenticationHandler authenticationHandler;

    /**
     * It exists if a mail transaction is in progress (from the MAIL command up
     * to the end of the DATA command).
     */
    private MessageHandler messageHandler;

    /**
     * Some state information
     */
    private String helo;
    private int recipientCount;
    /**
     * The recipient address in the first accepted RCPT command, but only if
     * there is exactly one such accepted recipient. If there is no accepted
     * recipient yet, or if there are more than one, then this value is null.
     * This information is useful in the construction of the FOR clause of the
     * Received header.
     */
    private String singleRecipient;

    /**
     * If the client told us the size of the message, this is the value. If they
     * didn't, the value will be 0.
     */
    private int declaredMessageSize = 0;

    /**
     * Some more state information
     */
    private boolean tlsStarted;
    private Certificate[] tlsPeerCertificates;

    /**
     * Creates the Runnable Session object.
     *
     * @param server a link to our parent
     * @param serverThread current server thread
     * @param socket is the socket to the client
     * @throws IOException Signals that an I/O exception of some sort has
     * occurred.
     */
    public Session(SMTPServer server, ServerThread serverThread, Socket socket)
            throws IOException {
        this.server = server;
        this.serverThread = serverThread;

        this.setSocket(socket);
    }

    /**
     * @return a reference to the master server object
     */
    public SMTPServer getServer() {
        return this.server;
    }

    /**
     * The thread for each session runs on this and shuts down when the quitting
     * member goes true.
     */
    @Override
    public void run() {
        MDC.setContextMap(parentLoggingMdcContext);
        sessionId = server.getSessionIdFactory().create();
        MDC.put("SessionId", sessionId);
        final String originalName = Thread.currentThread().getName();
        Thread.currentThread().setName(
                Session.class.getName() + "-" + socket.getInetAddress() + ":"
                + socket.getPort());

        if (log.isDebugEnabled()) {
            InetAddress remoteInetAddress = this.getRemoteAddress().getAddress();
            remoteInetAddress.getHostName();	// Causes future toString() to print the name too

            log.debug("SMTP connection from {}, new connection count: {}", remoteInetAddress,
                    this.serverThread.getNumberOfConnections());
        }

        try {
            runCommandLoop();
        } catch (IOException e1) {
            if (!this.quitting) {
                try {
                    // Send a temporary failure back so that the server will try to resend
                    // the message later.
                    this.sendResponse("421 4.4.0 Problem attempting to execute commands. Please try again later.");
                } catch (IOException e) {
                }

                if (log.isWarnEnabled()) {
                    log.warn("Exception during SMTP transaction", e1);
                }
            }
        } catch (Throwable e) {
            log.error("Unexpected error in the SMTP handler thread", e);
            try {
                this.sendResponse("421 4.3.0 Mail system failure, closing transmission channel");
            } catch (IOException e1) {
                // just swallow this, the outer exception is the real problem.
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException("Unexpected exception", e);
            }
        } finally {
            this.closeConnection();
            this.endMessageHandler();
            serverThread.sessionEnded(this);
            Thread.currentThread().setName(originalName);
            MDC.clear();
        }
    }

    /**
     * Sends the welcome message and starts receiving and processing client
     * commands. It quits when {@link #quitting} becomes true or when it can be
     * noticed or at least assumed that the client no longer sends valid
     * commands, for example on timeout.
     *
     * @throws IOException if sending to or receiving from the client fails.
     */
    private void runCommandLoop() throws IOException {
        if (this.serverThread.hasTooManyConnections()) {
            log.debug("SMTP Too many connections!");

            this.sendResponse("421 Too many connections, try again later");
            return;
        }

        this.sendResponse("220 " + this.server.getHostName() + " ESMTP " + this.server.getSoftwareName());

        while (!this.quitting) {
            try {
                String line;
                try {
                    line = this.reader.readLine();
                } catch (SocketException ex) {
                    // Lots of clients just "hang up" rather than issuing QUIT,
                    // which would
                    // fill our logs with the warning in the outer catch.
                    if (log.isDebugEnabled()) {
                        log.debug("Error reading client command: " + ex.getMessage(), ex);
                    }

                    return;
                }

                if (line == null) {
                    log.debug("no more lines from client");
                    return;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Client: " + line);
                }

                this.server.getCommandHandler().handleCommand(this, line);
            } catch (DropConnectionException ex) {
                this.sendResponse(ex.getErrorResponse());
                return;
            } catch (SocketTimeoutException ex) {
                this.sendResponse("421 Timeout waiting for data from client.");
                return;
            } catch (CRLFTerminatedReader.TerminationException te) {
                String msg = "501 Syntax error at character position " + te.position()
                        + ". CR and LF must be CRLF paired.  See RFC 2821 #2.7.1.";

                log.debug(msg);
                this.sendResponse(msg);

                // if people are screwing with things, close connection
                return;
            } catch (CRLFTerminatedReader.MaxLineLengthException mlle) {
                String msg = "501 " + mlle.getMessage();

                log.debug(msg);
                this.sendResponse(msg);

                // if people are screwing with things, close connection
                return;
            }
        }
    }

    /**
     * Close reader, writer, and socket, logging exceptions but otherwise
     * ignoring them
     */
    private void closeConnection() {
        try {
            try {
                this.writer.close();
                this.input.close();
            } finally {
                this.closeSocket();
            }
        } catch (IOException e) {
            log.info(e.toString());
        }
    }

    /**
     * Initializes our reader, writer, and the i/o filter chains based on the
     * specified socket. This is called internally when we startup and when (if)
     * SSL is started.
     *
     * @param socket client socker
     * @throws java.io.IOException Signals that an I/O exception of some sort
     * has occurred.
     */
    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.input = this.socket.getInputStream();
        this.reader = new CRLFTerminatedReader(this.input);
        this.writer = new PrintWriter(this.socket.getOutputStream());

        this.socket.setSoTimeout(this.server.getConnectionTimeout());
    }

    /**
     * This method is only used by the start tls command
     *
     * @return the current socket to the client
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Close the client socket if it is open
     *
     * @throws java.io.IOException Signals that an I/O exception of some sort
     * has occurred.
     */
    public void closeSocket() throws IOException {
        if ((this.socket != null) && this.socket.isBound() && !this.socket.isClosed()) {
            this.socket.close();
        }
    }

    /**
     * @return the raw input stream from the client
     */
    public InputStream getRawInput() {
        return this.input;
    }

    /**
     * @return the cooked CRLF-terminated reader from the client
     */
    public CRLFTerminatedReader getReader() {
        return this.reader;
    }

    /**
     * Sends the response to the client
     *
     * @param response text to send to client
     * @throws java.io.IOException Signals that an I/O exception of some sort
     * has occurred.
     */
    public void sendResponse(String response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Server: " + response);
        }

        this.writer.print(response + "\r\n");
        this.writer.flush();
    }

    /**
     * Returns an identifier of the session which is reasonably unique within an
     * extended time period.
     *
     * @return session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /* (non-Javadoc)
	 * @see org.subethamail.smtp.MessageContext#getRemoteAddress()
     */
    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) this.socket.getRemoteSocketAddress();
    }

    /* (non-Javadoc)
	 * @see org.subethamail.smtp.MessageContext#getSMTPServer()
     */
    @Override
    public SMTPServer getSMTPServer() {
        return this.server;
    }

    /**
     * @return the current message handler
     */
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    /**
     * Simple state
     */
    @Override
    public String getHelo() {
        return this.helo;
    }

    /**
     *
     * @param value helo/ehlo value
     */
    public void setHelo(String value) {
        this.helo = value;
    }

    /**
     * @return true if mail from is present
     *
     * @deprecated use {@link #isMailTransactionInProgress()}
     */
    @Deprecated
    public boolean getHasMailFrom() {
        return isMailTransactionInProgress();
    }

    /**
     *
     * @param recipientAddress recipient address to add
     */
    public void addRecipient(String recipientAddress) {
        this.recipientCount++;
        this.singleRecipient = this.recipientCount == 1 ? recipientAddress : null;
    }

    /**
     *
     * @return recipient count
     */
    public int getRecipientCount() {
        return this.recipientCount;
    }

    /**
     * Returns the first accepted recipient if there is exactly one accepted
     * recipient, otherwise it returns null.
     *
     * @return first recipient email address
     */
    public String getSingleRecipient() {
        return singleRecipient;
    }

    /**
     *
     * @return true if the client is authenticated
     */
    public boolean isAuthenticated() {
        return this.authenticationHandler != null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AuthenticationHandler getAuthenticationHandler() {
        return this.authenticationHandler;
    }

    /**
     * This is called by the AuthCommand when a session is successfully
     * authenticated. The handler will be an object created by the
     * AuthenticationHandlerFactory.
     *
     * @param handler handler to use
     */
    public void setAuthenticationHandler(AuthenticationHandler handler) {
        this.authenticationHandler = handler;
    }

    /**
     * @return the maxMessageSize the set declared message size
     */
    public int getDeclaredMessageSize() {
        return this.declaredMessageSize;
    }

    /**
     * @param declaredMessageSize the size that the client says the message will
     * be
     */
    public void setDeclaredMessageSize(int declaredMessageSize) {
        this.declaredMessageSize = declaredMessageSize;
    }

    /**
     * Starts a mail transaction by creating a new message handler.
     *
     * @throws IllegalStateException if a mail transaction is already in
     * progress
     */
    public void startMailTransaction() throws IllegalStateException {
        if (this.messageHandler != null) {
            throw new IllegalStateException(
                    "Mail transaction is already in progress");
        }
        this.messageHandler = this.server.getMessageHandlerFactory().create(
                this);
    }

    /**
     * Returns true if a mail transaction is started, i.e. a MAIL command is
     * received, and the transaction is not yet completed or aborted. A
     * transaction is successfully completed after the message content is
     * received and accepted at the end of the DATA command.
     *
     * @return true if transaction is in progress
     */
    public boolean isMailTransactionInProgress() {
        return this.messageHandler != null;
    }

    /**
     * Stops the mail transaction if it in progress and resets all state related
     * to mail transactions.
     * <p>
     * Note: Some state is associated with each particular message (senders,
     * recipients, the message handler).<br>
     * Some state is not; seeing hello, TLS, authentication.
     */
    public void resetMailTransaction() {
        this.endMessageHandler();
        this.messageHandler = null;
        this.recipientCount = 0;
        this.singleRecipient = null;
        this.declaredMessageSize = 0;
    }

    /**
     * @deprecated use {@link #resetMailTransaction()}
     */
    @Deprecated
    public void resetMessageState() {
        resetMailTransaction();
    }

    /**
     * Safely calls done() on a message hander, if one exists
     */
    private void endMessageHandler() {
        if (this.messageHandler != null) {
            try {
                this.messageHandler.done();
            } catch (Throwable ex) {
                log.error("done() threw exception", ex);
            }
        }
    }

    /**
     * Reset the SMTP protocol to the initial state, which is the state after a
     * server issues a 220 service ready greeting.
     */
    public void resetSmtpProtocol() {
        resetMailTransaction();
        this.helo = null;
    }

    /**
     * Triggers the shutdown of the thread and the closing of the connection.
     */
    public void quit() {
        this.quitting = true;
        this.closeConnection();
    }

    /**
     * @return true when the TLS handshake was completed, false otherwise
     */
    public boolean isTLSStarted() {
        return tlsStarted;
    }

    /**
     * @param tlsStarted true when the TLS handshake was completed, false
     * otherwise
     */
    public void setTlsStarted(boolean tlsStarted) {
        this.tlsStarted = tlsStarted;
    }

    public void setTlsPeerCertificates(Certificate[] tlsPeerCertificates) {
        this.tlsPeerCertificates = tlsPeerCertificates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Certificate[] getTlsPeerCertificates() {
        return tlsPeerCertificates;
    }
}
