package org.subethamail.smtp;

import java.util.List;

/**
 * The factory interface for creating authentication handlers.
 *
 * @author Marco Trevisan &lt;mrctrevisan@yahoo.it&gt;
 * @author Jeff Schnitzer
 */
public interface AuthenticationHandlerFactory {

    /**
     * If your handler supports RFC 2554 at some degree, then it must return all
     * the supported mechanisms here. <br>
     * The order you use to populate the list will be preserved in the output of
     * the EHLO command. <br>
     *
     * @return the supported authentication mechanisms as List, names are in
     * upper case.
     */
    public List<String> getAuthenticationMechanisms();

    /**
     * Create a fresh instance of your handler.
     *
     * @return org.subethamail.smtp.AuthenticationHandler
     */
    public AuthenticationHandler create();

}
