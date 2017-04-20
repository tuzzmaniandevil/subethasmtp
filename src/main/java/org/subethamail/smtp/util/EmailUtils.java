package org.subethamail.smtp.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * @author Jeff Schnitzer
 */
public class EmailUtils {

    /**
     * @param address email address to verify
     * @return true if the string is a valid email address
     */
    public static boolean isValidEmailAddress(String address) {
        // MAIL FROM: <>
        if (address.length() == 0) {
            return true;
        }

        boolean result;
        try {
            InternetAddress[] ia = InternetAddress.parse(address, true);
            result = ia.length != 0;
        } catch (AddressException ae) {
            result = false;
        }
        return result;
    }

    /**
     * Extracts the email address within a &lt;&gt; after a specified offset.
     *
     * @param value The raw value
     * @param offset offset
     * @return extracted email address
     */
    public static String extractEmailAddress(String value, int offset) {
        String address = value.substring(offset).trim();
        if (address.indexOf('<') == 0) {
            address = address.substring(1, address.indexOf('>'));
            // spaces within the <> are also possible, Postfix apparently
            // trims these away:
            return address.trim();
        }

        // find space (e.g. SIZE argument)
        int nextarg = address.indexOf(" ");
        if (nextarg > -1) {
            address = address.substring(0, nextarg).trim();
        }
        return address;
    }

    /**
     * Normalize the domain-part to lowercase. If email address is missing an
     * '@' the email is returned as-is.
     *
     * @param email the email address to normalize
     * @return the normalized email address
     */
    public static String normalizeEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex < 0) {
            return email;
        } else {
            return email.substring(0, atIndex) + email.substring(atIndex).toLowerCase();
        }
    }
}
